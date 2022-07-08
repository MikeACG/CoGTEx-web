/* 
 * TODO:
 * - Implement info for (legends, search boxes, columns)
 * - Implement info button for the usage of the search boxes
 * - Implement arrays of the existant global variables to be able to display 
 *   and control multiple plots in the table at the same time
 * - Implement saving the plots generated in the session at the bottom of the 
 *   page or in a tab (related to the last task), possibly with a way to put
 *   2 plots side-by-side for comparison
 * - hightlight last clicked row
 * - Take a look at interactive zoom for google charts OR change to plotly (probs better)
 * - Spinning wheels while waiting
 * - Add support for two-way inequalities and right-sided expressions in column
 *   search, currently only supports one-way left-sided expressions
 *   (e.g. '>= 0.2' and not '0.2 <= <= 0.8' or '0.2 <=')
 * - Implement column histograms
 * - Implement downloading scatter plot data (not figure just data)
 * - Implement displaying an error graphically on failed request
 * - Updating chart can be avoided if the selectedSeries set is empty or full
 *   , currently its ran unnecessarily when its full
 * - Make column searches work together with scrollX = true as a datatable
 *   option (currently it breaks it as scrollX duplicates the boxes)
 * - Make contingency table narrower
 * - display genecards for gene's child row
 * - Make default database for figures the one from previous page to order the table
 * - Add version of data to scatter plot title
 */

/* global google */
google.charts.load('current', {'packages':['corechart']});

var scatterData; // keeps the current shown plot's base data
var options; // keeps the current shown plot's options
var brksSeries; // indices indicating which series from scatterData are break lines (giving the discrete bins of gene expression) as opposed to points
var selectedSeries; // keeps thee current series (legend items) selected by the user to be displayed in the plot
var legendMode; // keeps the current legend mode (show/hide series)
var delayMiliseconds = 750; // time waited before searching after typing in input boxes
var timeout = null; // for waiting until thee user finishes typing to search columns

// after the page loads, request the gene list for the gene selected in the
// previous page.
$(document).ready(function() {
    
    $.ajax({
        url: "ListServlet",
        type: "GET",
        data: parseListRequest(),
        success: function(resp) {
           console.log(resp);
           drawList(resp);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log(jqXHR);
            console.log(textStatus);
            console.log(errorThrown);
        },
        dataType: "json"
    });
    
});

// gets the parameters sent from the last page to this page in an object which
// is used by the server to return the correct gene list
function parseListRequest () {
    
    let request = {};
    let parameters = document.getElementById('parameters').
            getElementsByTagName('input');
    for (let i = 0; i < parameters.length; i++) {
        request[parameters[i].id] = parameters[i].value;
    }
    return request;
    
}

// draws and configures the gene list once the server sends it using the
// DataTables third-party library
function drawList (list) {
    
    let listTable = document.getElementById('listTable');
    listTable.innerHTML = list.dataTableHTML; // header and footer of table is generated in server
    let dataTable = $(listTable).DataTable(list.dataTable); // make DataTable from two-dimensional array (sent by server) with the data
    
    // add keyup event to all input boxes in the footer of the table
    let searchBoxes = [...document.getElementsByClassName('searchCol')]; // class given in server
    searchBoxes.forEach(box => box.addEventListener('keyup', function() {
        clearTimeout(timeout);
        timeout = setTimeout(function () {
            dataTable.draw(); // redrawing the table will fire the search functions of the table, a custom one is defined at fn.dataTable.ext.search.push in this document
        }, delayMiliseconds); // wait a bit before firing the actual event
    }));
    
    // configure the general search in the table with delay
    $('div.dataTables_filter input').off('keyup.DT input.DT'); // remove default behavior of general search box
    $('div.dataTables_filter input').on('keyup', function() {
        let query = $('div.dataTables_filter input').val();
        clearTimeout(timeout);
        timeout = setTimeout(function() {
            if (query !== null) {
                dataTable.search(query).draw();
            }
        }, delayMiliseconds);
    });
    
    // add click event to all cells with the details-control (given in server) CSS class in the gene list
    $(listTable).on('click', 'td.details-control', function () {
        let tr = $(this).closest('tr'); // gets row to which the clicked cell belongs
        let row = dataTable.row(tr); // allows to use DataTables features with the row
        let headers = [...listTable.getElementsByTagName('th')]
                .map(th => th.innerHTML); // array of gene list's column names
        // extract ensembl and symbol of clicked gene
        let ensemblIdx = headers.findIndex(h => h.indexOf("Ensembl") >= 0);
        let yEnsembl = row.data()[ensemblIdx];
        let symbolIdx = headers.findIndex(h => h.indexOf("Symbol") >= 0);
        let ySymbol = row.data()[symbolIdx];
 
        if ( row.child.isShown() ) { // simply hide the row and switch CSS
            row.child.hide();
            tr.removeClass('shown');
        }
        else {
            cleanDashboard(listTable, dataTable); // hide any shown rows
            row.child(createDashboard()).show(); // append divs for showing plot of the row
            tr.addClass('shown');
            requestScatter(yEnsembl, ySymbol); // request scatter between the clicked gene and the gene of the gene list
        }
    });
    
    // when changing pages of the table, sorting the table or searching something, hide any shown row plots
    $(listTable).on('preDraw.dt', function (){
        cleanDashboard(listTable, dataTable);
    });
    
}

// hides all plots that are currently visible in the table
// also resets legend related global variables for when a new plot is shown
function cleanDashboard(listTable, dataTable) {
    
    selectedSeries = new Set;
    legendMode = "";
    
    let trs = [...listTable.getElementsByTagName('tr')];
    for (let i = 0; i < trs.length; i++) {
        if ([...trs[i].classList].includes('shown')) {
            dataTable.row(trs[i]).child.hide();
            trs[i].classList.remove('shown');
        }
    }
    
}

// returns the structure of divs used for scatter plot between a pair of genes
function createDashboard() {
    return '<div id="dashboard" class="dashboardDiv">' + 
            '<div id="legend_div" class="legendDiv"></div>' + 
            '<div id="scatter_div" class="scatterDiv"></div>' + 
            '<div id="contingency_div" class="contingencyDiv"></div>' + 
            '</div>';
}

// this function will be applied to each row of the data when searching an input field
$.fn.dataTable.ext.search.push(
    function(settings, row) { // possible parameters are detailed in DataTables documentation, here we only use the second one

        // collect the information from the column input fields
        let searchInputs = document.getElementsByClassName('searchCol'); // class given in server
        let nInputs = row.length; // row is an array with its td.innerHTML values (length of number of columns)
        let rowPassesCol = new Array(nInputs);
        let filter, i;
        
        for (i = 0; i < nInputs; i++) { // for each col test if the row value passes the filter
            if (searchInputs[i].value === "") { // the input box was not used, nothing to filter
                rowPassesCol[i] = true;
            } else { // something in the input box
                filter = cleanSearchFilter(searchInputs[i].value); // standardize the input string
                rowPassesCol[i] = (filter.type === "inequality") ? 
                    testInequality(row[i], filter.cleanFilter) : // evaluate the mathematical expression
                            row[i].indexOf(filter.cleanFilter) >= 0; // just test exact substring match
            }
        }
        
        // general search is handled internally by DatatTables but its basically another AND with the result of this function, it operates with exact substring matches
        return rowPassesCol.every(Boolean); // logical AND of array, row is only displayed if it passes the tests of all columns
    }
);

// parses the user's filter string applied to a column of gene list
function cleanSearchFilter(filter) {
    
    filter = filter.trim(); // get rid of leading and trailing whitespace
    
    // search for angle brackets and/or equals sign
    let rightIdx = filter.indexOf('<');
    let leftIdx = filter.indexOf('>');
    let bracketIdx = Math.max(rightIdx, leftIdx); // index further right on the string
    let eq = filter.indexOf('=');
    
    let type;
    if ( bracketIdx === 0) { // assume user is trying to filter with an inequality as he starts string with an angle bracket
        // insert a space between the bracket and the rest of the string to have control over it later
        let spaceIdx = (eq === 1) ? 3 : 2; // if there is an equals sign after the angle bracket change insert index
        filter = filter.slice(0, spaceIdx) + " " + filter.slice(spaceIdx);
        filter = filter.replace(/\s\s+/g, ' '); // replace all consecutive whitespace with a single space as user could have already placed a space where we inserted it
        type = "inequality";
    } else { // assume string is meant for literal match
        if (eq >= 0) filter = filter.replace("=", "").trimStart(); // user could use "= filter" to denote a literal match
        type = "literal";
    }
    
    return {cleanFilter: filter, type: type};
}

// evaluates a user's inequality applied to search a column gene list
function testInequality(val, inequality) {
    
    val = parseFloat(val);
    if (isNaN(val)) return false; // misisng value in data
    inequality = inequality.split(" ");
    let expression = inequality[0]; // angle brackets and/or equals signs
    let threshold = parseFloat(inequality[1]); // user's value
    if (isNaN(threshold)) return false; // user inputed an invalid number
    val = Math.abs(val);
    
    if (expression === "<") {
        return val < threshold;
    } else if (expression === ">") {
        return val > threshold;
    } else if (expression === "<=") {
        return val <= threshold;
    } else if (expression === ">=") {
        return val >= threshold;
    } else { // invalid expression
        return false;
    } 
    
}

// implements an extension to sort a DataTable column using the absolute values
// the prefix 'abs-num' is added in the column definitions (at the server) to columns where this function is applied
jQuery.extend( jQuery.fn.dataTableExt.oSort, {
    
    "abs-num-pre": function ( a ) {
        return (Math.abs(a));
    },
 
    "abs-num-asc": function ( a, b ) {
        return ((a < b) ? -1 : ((a > b) ? 1 : 0));
    },
 
    "abs-num-desc": function ( a, b ) {
        return ((a < b) ? 1 : ((a > b) ? -1 : 0));
    }
    
} );

// requests the server for the data necessary to build a scatterplot between a
// pair of genes including legend and contigency table info
function requestScatter(yEnsembl, ySymbol) {
    
    $.ajax({
        url: "ScatterServlet",
        type: "GET",
        data: parseScatterRequest(yEnsembl, ySymbol),
        success: function(resp) {
            // global variable assignment
            scatterData = resp.scatter;
            options = resp.scatteropts;
            brksSeries = resp.brksSeries;
            // display plot
            parseBrks();
            drawScatter(resp.scatter);
            drawLegend(resp.legend);
            drawContingency(resp.contingencyTable);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log(jqXHR);
            console.log(textStatus);
            console.log(errorThrown);
        },
        dataType: "json"
    });
 
}

// creates an object which the server can read to return the data for the scatter plot
// element names/ids correspond to the ones sent from the form of the previous page to this page
function parseScatterRequest(yEnsembl, ySymbol) {
    
    let request = {};
    
    request.xEnsembl = document.getElementById('Gene ensembl').value;
    request.xSymbol = document.getElementById('Gene symbol').value;
    request.yEnsembl = yEnsembl;
    request.ySymbol = ySymbol;
    request.db = document.getElementById('database').value;
    request.version = document.getElementById('Version').value;
    
    return request;
    
}

// states in the plot options what are the data series which are meant to be lines
// (default is points), these line series are the breaks for discretizing
// the expression of genes. This is the only plot option currently configured
// client side as it is difficult to parse in java. The series indices must be
// the keys of the object giving the series type appended to the options object
function parseBrks() {
    
    options.series = {};
    for (let i = 0; i < brksSeries.length; i++) {
        options.series[brksSeries[i]] = {
            type: 'line',
            color: '#8a8a8a',
            lineDashStyle: [2, 2],
            lineWidth: 1.5
        };
    }
    
}

// draws chart inside div given a literal object and options formated as per 
// the google charts documentation
function drawScatter(currentData) {
    let gcTable = new google.visualization.DataTable(currentData);
    let scatterDiv = document.getElementById('scatter_div'); // div created dinamically in gene list row click event
    let chart = new google.visualization.ComboChart(scatterDiv); // ComboChart supports both scatter and line in one chart
    chart.draw(gcTable, options);
}

// draws a custom legend for the scatter plots as the built-in for google charts
// is very limited regarding customizations
function drawLegend(legend) {
    
    let legendDiv = document.getElementById('legend_div'); // div created dinamically in gene list row click event
    legendDiv.addEventListener("contextmenu", e => e.preventDefault()); // do not bring out the right-click menu when right clicking anywhere in the legend
    legendDiv.innerHTML += legend.html; // server-generated html table
    
    // add an mouse click event to the cells of the legend for interactivity
    let legendTab = legendDiv.getElementsByTagName('table')[0]; // just added this element to the document
    let rows = legendTab.getElementsByTagName('tr');
    let i, j, cells, td;
    for (i = 0; i < rows.length; i++) {
        cells = rows[i].getElementsByTagName('td');
        for (j = 0; j < cells.length; j++) {
            td = cells[j];
            if ( td.innerHTML !== " " && j % 2 === 1) { // cell is not empty (server sends empty cells with a single space) nor it is a css (shape of the legend at even indices of table), add event
                td.addEventListener("mousedown", function(e) {
                    switch (e.which) {
                        case 1: // left click
                            showSeries(e);
                            break;
                        case 3: // right click
                            hideSeries(e);
                            break;
                    }
                });
            }
        }
    }
    
    // add a button to the legend which can reset it to its original state
    let restoreButton = document.createElement("button");
    restoreButton.innerHTML = 'Restore';
    restoreButton.addEventListener('click', function(e) {
        selectedSeries.clear();
        legendMode = "";
        updateChart(); // redraw chart
        restoreLegend(e.target.previousSibling.firstChild); // passes legend div's table tbody to deselect clicked cells
    });
    legendDiv.appendChild(restoreButton);
    
}

// exclusively shows in the scatter plots clicked groups in the legend 
function showSeries(event) {
    
    let td = event.target; // clicked legend table cell
    if (legendMode !== "show") { // exit "hide" mode
        selectedSeries.clear();
        restoreLegend(td.parentNode.parentNode); // passes legend div's table tbody to deselect clicked cells
        legendMode = "show";
    }
    
    // select or deselect series
    let series = td.innerHTML.split(" ")[0]; // strip sample size (server sends the group's name together with sample size separated by space)
    if (selectedSeries.has(series)) {
        td.style.backgroundColor = 'white';
        selectedSeries.delete(series);
    } else {
        td.style.backgroundColor = '#b0bcff';
        selectedSeries.add(series);
    }
    
    updateChart();
    
}

// exclusively shows in the scatter plots non-clicked groups in the legend 
function hideSeries(event) {
    
    let td = event.target; // clicked legend table cell
    if (legendMode !== "hide") { // exit 'show' mode
        scatterData.cols.filter(col => col.role === "data")
                .map(col => col.label)
                .forEach(lab => selectedSeries.add(lab)); // select all series in the data
        restoreLegend(td.parentNode.parentNode); // passes legend div's table tbody to deselect clicked cells
        legendMode = "hide";
    }
    
    // select or deselect series
    let series = td.innerHTML.split(" ")[0]; // strip sample size (server sends the group's name together with sample size separated by space)
    if (selectedSeries.has(series)) { 
        td.style.backgroundColor = '#ffc2c2';
        selectedSeries.delete(series);
    } else {
        td.style.backgroundColor = 'white';
        selectedSeries.add(series);
    }
    
    updateChart();
    
}

// redraws the chart based on the series selected by the user when clicking
// the interactive table legend
function updateChart() {

    let updatedData;
    
    if (selectedSeries.size > 0) { // work to do!
        // find indices of columns to be kept for each selected series
        let reg = new RegExp([...selectedSeries].join('|'));
        let regMatch = scatterData.cols.map(col => reg.test(col.label)); // see if data columns match any selected series
        regMatch[0] = true; // domain x-values should always be selected
        let ncols = regMatch.length;
        for (let i = ncols - brksSeries.length; i < ncols; i++) regMatch[i] = true; // series for break lines should always be selected

        // construct a new scatterData object with all columns ()
        updatedData = {};
        updatedData.cols = scatterData.cols;

        // for every row in the scatterData keep fields to be shown and set the ones to be hidden to missing values
        let nrows = scatterData.rows.length;
        updatedData.rows = new Array(nrows);
        for (let i = 0; i < nrows; i++) {
            updatedData.rows[i] = {c: scatterData.rows[i]
                        .c.map((col, j) => regMatch[j] ? col : null)}; // the 'c' key is used according to the google charts format
        }
            
    } else { // all series should be shown
        updatedData = scatterData;
    }

    drawScatter(updatedData); // redraw chart
}

// appends to div a contingency html table sent by the server to complement 
// the corresponding scatter plot 
function drawContingency(contingency) {
    
    let contingencyDiv = document.getElementById('contingency_div'); // div created dinamically in gene list row click event
    contingencyDiv.innerHTML += contingency.html;
    
}

// visually restores the legend to its original state (no clicked/highlighted cells)
function restoreLegend(tbody) {
    
    for (let i = 0; i < tbody.children.length; i++) {
        for (let j = 0; j < tbody.children[i].children.length; j++) {
            tbody.children[i].children[j].style.backgroundColor = "white";
        }
    }
    
}
