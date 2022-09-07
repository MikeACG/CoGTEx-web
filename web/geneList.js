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

// constructor for objects keeping plot information
function Coplot(dashboardSuffix, scatterData, options, brksSeries, 
    selectedSeries, legendMode, legend, contingency, info, request) {
    
    this.dashboardSuffix = dashboardSuffix; // created divs for displaying plots in the data table (gene list) will have this suffix  in their ids
    this.scatterData = scatterData; // keeps the current shown plot's in the data table base data
    this.options = options; // keeps the current shown plot's in the data table options
    this.brksSeries = brksSeries; // indices indicating which series from scatterData are break lines (giving the discrete bins of gene expression) as opposed to points
    this.selectedSeries = selectedSeries; // keeps the current series (legend items) selected by the user to be displayed in the plot inside the data table
    this.legendMode = legendMode; // keeps the current legend mode (show/hide series) for the plot shown in the data table
    this.legend = legend; // keeps the current legend for the plot shown in the data table 
    this.contingency = contingency; // keeps the current contngency table for the plot shown in the data table
    this.info = info; // keeps a table howing the info of the gene in the y-axis of the coplot
    this.scatterDataMod = scatterData; // keeps a version of scatterData which may be modified later
    this.request = request; // the request to the server that originated this coplot
    
}
var listPlots = []; // global array for the plots shown in the data table
var quickView = []; // global array to keep plot objects neccessary to interact the quick view
var quickSuffix = 'quick'; // suffix to avoid conflict between ids of the listPlots and quickView arrays
var delayMiliseconds = 500; // time waited before searching after typing in input boxes
var timeout = null; // for waiting until the user finishes typing to search columns
// the following object is implemented to search the dataTable more eeficiently, 
// we will be using a custom function programmed for the dataTables search plugin ($.fn.dataTable.ext.search.push)
// to search which is fired for each row in the table, this object computes and holds the necessary data
// for performing the search and avoids processing this data inside the plugin unnecessarily repeating
// pre-calculations for each row and just grabbbing the data as neccessary from this global object
var searchObj = {
    perColumnInputs: [], // holds the <input> objects of the footers of the data table 
    shownHeaders: [], // headers of the currently shown rows in the data table
    readPerColumnInputs: function() { // read the text inputed by the user in the footer of table, which rows are shown can also be inferred from what inputs are catched by the document selector
       this.perColumnInputs = [...document.getElementsByClassName('searchCol')]; // class given in server
       this.shownHeaders = this.perColumnInputs
            .map(si => si.placeholder.replace('?', '')); // get the headers of shown columns, these are placeholders in the input preceded by '?' (added in the server)
    },
    allHeaders: [], // holds all headers of the data table
    readAllHeaders: function(info) { // reads all headers from the object sent by the server to build the data table
        this.allHeaders = info.dataTable.columns.map(c => c.title); // get all headers of data including hidden columns
    },
    generalInput: '', // holds what is inputed by the user in the general search box of the data table
    readGeneralInput: function() { // reads the default search box of the data table
        this.generalInput = $('div.dataTables_filter input').val();
    }
};
var infoObj = {}; // global object to keep the information of all genes shwon in the gene list which can be plotted as part of the coplots
// function to convert user shown names of versions of the data to the internal ID used to handle it
function versionName2id(vn) {
    let id;
    switch (vn) {
        case 'TPM':
           id = 'v0.2A';
           break;
        case 'Z-score':
           id = 'v0.2C';
           break; 
    }
    return id;
}

// after the page loads, request the gene list for the gene selected in the
// previous page.
$(document).ready(function() {
    
    switchSpinner('listDiv');
    
    $.ajax({
        url: "ListServlet",
        type: "GET",
        data: parseListRequest(),
        success: function(resp) {
           console.log(resp);
           requestInfo(resp);
           configPageControls();
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

// switches on/off the visibility of a div with a spinning wheel while waiting for a process
// if div does not exist in target container it is created snd shown
function switchSpinner(target) {
    
    let targetDiv = document.getElementById(target);
    let spinnerIdx = [...targetDiv.children].findIndex(c => c.id === 'spinnerDiv');
    let spinnerDiv;
    if (spinnerIdx < 0) {
        spinnerDiv = document.createElement('div');
        spinnerDiv.id = 'spinnerDiv';
        spinnerDiv.innerHTML = '<img src="resources/spin.gif">';
        spinnerDiv.classList.add('spinner');
        targetDiv.appendChild(spinnerDiv);
    } else {
        spinnerDiv = targetDiv.children[spinnerIdx];
        spinnerDiv.style.display = (spinnerDiv.style.display === 'none') ? 
            'flex' : 'none';
    }
    
}

// gets the parameters sent from the last page to this page in an object which
// is used by the server to return the correct gene list
function parseListRequest () {
    
    let orderVersionedDbInput = document.getElementById('Order database');
    let orderVersionedDb = orderVersionedDbInput.value.split('_');
    
    let request = {
        orderVersionName: orderVersionedDb[0],
        orderDatabase: orderVersionedDb[1],
        ensembl: document.getElementById('Gene ensembl').value,
        listSize: document.getElementById('List size').value
    };
    
    
    return request;
    
}

// request info (i.e. Metadata for the genes shown in the gene list)
function requestInfo(requestListResponse) {
    
    $.ajax({
        url: "InfoServlet",
        type: "POST", // needs to be post to work due to the long string sent
        data: parseInfoRequest(requestListResponse),
        success: function(resp) {
           console.log(resp);
           infoObj = resp;
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log(jqXHR);
            console.log(textStatus);
            console.log(errorThrown);
        },
        dataType: "json"
    });
    
}

// gets the data necessary to request the info for the genes in the gene list
function parseInfoRequest(requestListResponse) {
    
    let request = {};
    
    // get ids of all genes in the table
    let idColIdx = requestListResponse.dataTable.columns.
            findIndex(c => c.title.toLowerCase() === 'ensembl');
    request.ids = requestListResponse.dataTable.data.
            map(row => row[idColIdx]).join('|');
    
    // get the data version
    // unnecessary since merge of both versions
    //request.version = document.getElementById('Version').value; // hidden input box filled with jstl on page load
    return request;
    
}

// shows the page controls and attaches the proper events
function configPageControls() {
    
    // get database version and copy to fields that use it
    // unused since merge of versions in a single page
    /*
    let version = document.getElementById('Version').value; // hidden box filled with jstl on page load 
    let plotVersionSelect = document.getElementById('plotVersion');
    let listVersionSelect = document.getElementById('listVersion');
    plotVersionSelect.value = listVersionSelect.value = version;
    */
    
    // set other parameters of this page to the defaults for a new page of the same kind for another gene if user requests it
    let versionedListDb = document.getElementById('Order database').value;
    document.getElementById('listSize').value = document.getElementById('List size').value; // hidden box filled with jstl on page load
    document.getElementById('listDb').value = versionedListDb; // hidden box filled with jstl on page load
    
    // get database and set default plot option the corresponding
    let versionedListDbSplt = versionedListDb.split("_"); // sep given in geneHome page
    let listVersion = versionedListDbSplt[0];
    let listDb = versionedListDbSplt[1];
    let db;
    switch (listDb) { // hidden box filled with jstl on page load, this case is to map the database header name to sql file in the server
        case 'Min Pearson':
            db = 'min_pearson';
            break;
        case 'Min Spearman':
            db = 'min_spearman';
            break;
        case 'Min G':
            db = 'min_G';
            break;
        case 'Pearson Age 50-59':
            db = 'AGE_50-59';
            break;
        case 'Pearson Age < 50':
            db = 'AGE_less50';
            break;
        case 'Pearson Age > 59':
            db = 'AGE_more59';
            break;
        case 'Pearson Sex Male':
            db = 'SEX_male';
            break;
        case 'Pearson Sex Female':
            db = 'SEX_female';
            break;
        case 'Pearson Ischemia Low':
            db = 'SMTSISCH_low';
            break;
        case 'Pearson Ischemia High':
            db = 'SMTSISCH_high';
            break;
        default:
            db = 'min_pearson';
    }
    document.getElementById('plotDatabase').value = listVersion + "," + db; // separator given in this page's JSP
    
    // show plot preferences
    document.getElementById('plotPreferencesDiv').style.display = 'block';
    
}

// draws and configures the gene list once the server sends it using the
// DataTables third-party library
function drawList (list) {
    
    let listTable = document.getElementById('listTable');
    listTable.innerHTML = list.dataTableHTML; // header and footer of table is generated in server
    // set properties of the search object that are neccessary to build the data table for the first time
    searchObj.readAllHeaders(list);
    searchObj.readPerColumnInputs();
    // add keyup event to all input boxes in the footer of the table (before drawing the table for the first time to catch all inputs)
    let searchBoxes = [...document.getElementsByClassName('searchCol')]; // class given in server
    searchBoxes.forEach(box => box.addEventListener('keyup', function() {
        clearTimeout(timeout);
        timeout = setTimeout(function () {
            searchObj.readPerColumnInputs(); // update shown columns and their inputs
            dataTable.draw(); // redrawing the table will fire the search functions of the table, a custom one is defined at fn.dataTable.ext.search.push in this document
        }, delayMiliseconds); // wait a bit before firing the actual event
    }));
    // make DataTable from two-dimensional array (sent by server) with the data
    let dataTable = $(listTable).DataTable(list.dataTable);
    searchObj.readPerColumnInputs(); // now that table is drawn, update shown cols
    // hide the waiting div
    switchSpinner('listDiv');
    
    // configure the general search in the table with delay
    $('div.dataTables_filter input').off('keyup.DT input.DT'); // remove default behavior of general search box
    $('div.dataTables_filter input').on('keyup', function() {
        let query = $('div.dataTables_filter input').val();
        clearTimeout(timeout);
        timeout = setTimeout(function() {
            if (query !== null) {
                searchObj.readGeneralInput(); // update what the user types in the general search box
                dataTable.search(query).draw();
            }
        }, delayMiliseconds);
    });
    
    // add click event to all cells in the table's body
    let listTableBody = listTable.getElementsByTagName('tbody')[0];
    $(listTableBody).on('click', 'td', function () {
        let tdClasses = [...this.classList];
        if (tdClasses.indexOf("details-control") >= 0) { // only fire event if the cell has the details-control class
            let tr = this.parentNode; // gets row to which the clicked cell belongs
            let row = dataTable.row(tr); // allows to use DataTables features with the row
            let rowIdx = row.index(); // index of clicked row in the context of all rows (even filtered non-visible ones)
            if ( row.child.isShown() ) { // simply hide the row and switch CSS
                row.child.hide();
                tr.classList.remove('shown');
                rmCoplot(listPlots, rowIdx);
            }
            else {
                let allHeaders = dataTable.context[0].aoColumns
                    .map(col => col.title.toLowerCase()); // get all column headers including non-visible columns
                let ensemblIdx = allHeaders.findIndex(h => h.indexOf('ensembl') >= 0); // index of ensembl column
                let symbolIdx = allHeaders.findIndex(h => h.indexOf('symbol') >= 0); // index of gene symbol column
                let ySymbol = dataTable.context[0].aoData[rowIdx]._aData[symbolIdx]; // gene symbol of clicked row
                let yEnsembl = dataTable.context[0].aoData[rowIdx]._aData[ensemblIdx]; // ensembl of clicked row
                
                row.child(createDashboard(rowIdx)).show(); // append divs for showing plot of the row
                switchSpinner('scatter_div_' + rowIdx); // show spinning wheel while waiting (div created dynamically in createDashboard())
                tr.classList.add('shown');
                requestScatter(yEnsembl, ySymbol, rowIdx); // request box plot of clicked gene
            }
        }
    });
    
    // add click event to table's body cells in general (this must be defined after the details-control cell event)
    $(listTableBody).on('click', 'td', function() {
        let tdClasses = [...this.classList];
        if (tdClasses.indexOf('details-control') < 0) { // do not fire if the clicked cell has the details-control class (i.e. do not deselct the row, just hide/show plot)
            let tr = this.parentNode; // row to which the clicked cell belongs
            let rowIdx = dataTable.row(tr).index();
            let controlTd = tr.firstChild; // first cell of each rown (first column) is the responsible for details-control
            let trClasses = [...tr.classList];
            if (trClasses.indexOf('selected') >= 0) { // row was already highlighted
                // hide the child row first to avoid looping the rows of dashboard tables, then clean
                rmCoplot(listPlots, rowIdx);
                dataTable.row(this).child.hide(); // hide the row's plot if shown otherwise no effect
                tr.classList.remove('shown'); // remove shown CSS if shown, otherwise no effect
                cleanSelectedGene(listTableBody); // deselect all rows and switch CSS
                controlTd.classList.remove('details-control'); // deactivate details-control from the first cell opf the row
            } else if (trClasses.length > 0) { // row is not highlighted nor it is a child row (these have no classes)
                //cleanDashboard(listTable, dataTable); // hide any shown rows
                cleanSelectedGene(listTableBody); // deselect all rows and switch CSS
                tr.classList.add('selected'); // highlight row
                configGeneActions(tr, dataTable); // create actions for selected row (gene)
                controlTd.classList.add('details-control'); // activate details-control for the first cell of this row
            }
        }
    });
    
    // add checkboxes to the document which will act as selectors to hide/show columns
    let selectorsCheckboxesDiv = document.getElementById('selectorsCheckboxesDiv');
    selectorsCheckboxesDiv.innerHTML = list.selectorsHTML; // generated in server according to column names of the data
    let selectors = [...document.querySelectorAll('.versionedColSelector')]; // class given in server
    selectors.forEach(function(child) { // add jquery user interface (UI) css;
        [...child.children].forEach(checkbox => $(checkbox).tooltip().off('focusin focusout')); // do not show duplicated tooltip on click
        $(child).controlgroup();
    });

    // add checkboxes change event to show/hide columns
    selectors.forEach(function(child) {
        
       child.addEventListener('change', function(e) {
            let input = e.target;
            if (input.tagName === "INPUT") {
                let column = dataTable.column(':contains(' + input.id + ')'); // id of checkbox is added as the column title/header in the server
                if (input.checked) {
                    column.visible(true);
                } else {
                    column.visible(false);
                }
                searchObj.readPerColumnInputs(); // update shown columns and their inputs
                dataTable.draw(); // redraw to reload searches
            }
        });
        
    });
    
    // when changing pages of the table, sorting the table or searching something:
    // clean any highlighted rows
    $(listTable).on('page.dt order.dt search.dt', function (){
        cleanSelectedGene(listTableBody);
    //    cleanDashboard(infoTable, dataTable);
    });
    
}

// returns the structure of divs used for scatter plot between a pair of genes, ids of divs are appended a suffix
function createDashboard(suffix) {
    return  '<div id="dashboard_' + suffix + '" class="dashboardDiv">' + 
            '<div id="legend_div_' + suffix + '" class="legendDiv"></div>' + 
            '<div id="scatter_div_' + suffix + '" class="scatterDiv"></div>' + 
            '<div id="contingency_div_' + suffix + '" class="contingencyDiv"></div>' +
            '<div id="scatterActions_div_' + suffix + '" class=scatterActionsDiv></div>' +
            '<div id="scatterInfo_div_' + suffix + '" class=scatterInfoDiv></div>' +
            '</div>';
}

// removes datatables 'selected' CSS class from current active rows and
// hides all actions associated with the gene from the page
function cleanSelectedGene(tbody) {
    
    let trs = [...tbody.children]; // this instead of getElementsByTagName works
    trs.forEach(tr => {
        tr.classList.remove('selected');
        tr.firstChild.classList.remove('details-control');
    });
    document.getElementById('listPreferencesDiv').style.display = 'none'; // hide list preferences
    document.getElementById('externalLinksDiv').style.display = 'none'; // hide external links
    
}

// hides all plots that are currently visible in the table
// also resets legend related global variables for when a new plot is shown
//  UNUSED as from when I switched to allow show multiple plots in the table
function cleanDashboard(listTable, dataTable) {
    
    if (!listPlot) return; // no plot has been requested yet
    listPlot.selectedSeries = new Set;
    listPlot.legendMode = '';
    
    let trs = [...listTable.getElementsByTagName('tr')];
    for (let i = 0; i < trs.length; i++) {
        if ([...trs[i].classList].includes('shown')) {
            dataTable.row(trs[i]).child.hide();
            trs[i].classList.remove('shown');
        }
    }
    
}

// removes a coplot with specified suffix from an array
function rmCoplot(coplots, suffix) {
    
    let sus = coplots.map(coplot => coplot.dashboardSuffix);
    let idx = sus.find(su => su === suffix);
    if (idx > -1) {
        coplots.splice(idx, 1);
    }
    
}

// configures the actions that can be performed with a selected gene in the table
function configGeneActions(tr, dataTable) {
    
    // get data neccessary to pinpoint clicked gene in the table
    let allHeaders = dataTable.context[0].aoColumns
            .map(col => col.title.toLowerCase()); // get all column headers including non-visible columns
    let rowIdx = dataTable.row(tr).index(); // index of clicked row in the context of all rows (even filtered non-visible ones)
    
    // get gene info and add to form for gene list
    let symbol = getGeneInfo(dataTable, allHeaders, rowIdx, 'symbol', 'listGeneSymbol');
    let ensembl = getGeneInfo(dataTable, allHeaders, rowIdx, 'ensembl', 'listGeneEnsembl');
    let entrez = getGeneInfo(dataTable, allHeaders, rowIdx, 'entrez', '');
    
    // configure submit button of gene list form and show the form
    let listButton = document.getElementById('listPreferencesSubmit');
    listButton.value = 'Go to CoGTEx associations for ' + symbol;
    $(listButton).button(); // assign jquery's UI CSS class
    document.getElementById('listPreferencesDiv').style.display = 'block'; // show form
    
    // add external links and show the links
    configExternalLink('genecardsLink', symbol, 'GeneCards', 
        'https://www.genecards.org/cgi-bin/carddisp.pl?gene=', ensembl);
    configExternalLink('ncbiLink', symbol, 'NCBI Gene', 
        'https://www.ncbi.nlm.nih.gov/gene/', entrez);
    configExternalLink('ensemblLink', symbol, 'Ensembl Gene', 
        'https://grch37.ensembl.org/Homo_sapiens/Gene/Summary?g=', ensembl);
    configExternalLink('gtexLink', symbol, 'GTEx Gene', 
        'https://gtexportal.org/home/gene/', ensembl);
    document.getElementById('externalLinksDiv').style.display = 'block'; // show plot preferences
    
}

// get gene info from table and pass it to  the gene list form
function getGeneInfo(dataTable, headers, rowIdx, colname, inputId) {
    
    let idx = headers.findIndex(h => h.indexOf(colname) >= 0); // index of column
    let id = dataTable.context[0].aoData[rowIdx]._aData[idx]; // id of clicked row
    if (inputId !== '') {
        document.getElementById(inputId).value = id; // assign gene symbol to hidden input box in form
    }
    
    return id;
}

// configures a link to an external web to see info of a gene
function configExternalLink(anchorId, symbol, webName, baseUrl, target) {
    
    let link = document.getElementById(anchorId);
    $(link).button({margin: '5px'}); // jquery UI style
    link.innerHTML = webName + ' for ' + symbol; // jquery UI uses a span inside the anchor but the children here is null?? see the geneHome.js for a counterexample
    link.href = baseUrl + target; // keep the base URL updated if URL of the corresponding web structure changes
    
}

// this function will be applied to each row of the data when searching an input field
$.fn.dataTable.ext.search.push(
    function(settings, row) { // possible parameters are detailed in DataTables documentation

        let perColLogicals = [];
        let generalLogicals = [];
        let filter, i = 0, j;
        for (j = 0; j < row.length; j++) { // for each column in the row (all hidden and non-hidden)
            if (searchObj.shownHeaders.indexOf(searchObj.allHeaders[j]) >= 0) { // column is not hidden, check its corresponding input box
                // i keeps track of the indices of the input boxes which is always <= row.length. The inputs are naturally in order already with the row if we removed the hidden columns from the row
                if (searchObj.perColumnInputs[i].value === '') { // the input box was not used, nothing to filter
                    perColLogicals.push(true); // auto passes filter
                } else { // something in the input box
                    filter = cleanSearchFilter(searchObj.perColumnInputs[i].value); // standardize the input string
                    perColLogicals.push((filter.type === "inequality") ? 
                        testInequality(row[j], filter.cleanFilter) : // evaluate the mathematical expression
                                row[j].indexOf(filter.cleanFilter) >= 0); // just test exact substring match
                }
                if (searchObj.generalInput === '') { // user not using the general search box
                    generalLogicals.push(true); // auto passes filter
                } else { // user wrote something in general search box which works as a literal substring match
                    generalLogicals.push(row[j].indexOf(searchObj.generalInput) >= 0);
                }
                i++; // increase index of per col input boxes
            }
        }
        
        // row should be shown if all per column input boxes tests are true AND if at least one of the general search tests is true
        return perColLogicals.every(Boolean) && generalLogicals.some(Boolean);
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
function requestScatter(yEnsembl, ySymbol, listPlotSuffix) {
    
    let request = parseScatterRequest(yEnsembl, ySymbol);
    $.ajax({
        url: "ScatterServlet",
        type: "GET",
        data: request,
        success: function(resp) {
            console.log(resp);
            // global variable assignment of gene list plot
            let listPlot = new Coplot(listPlotSuffix, resp.scatter, resp.scatteropts, 
                resp.brksSeries, new Set, '', resp.legend, resp.contingencyTable, 
                makeInfoTable(yEnsembl, ySymbol), request);
            // display plot
            parseBrks(listPlot);
            switchSpinner('scatter_div_' + listPlotSuffix); // hide spinning wheel while waiting (div created dynamically in createDashboard())
            drawScatter(listPlot);
            drawLegend(listPlot);
            drawContingency(listPlot);
            drawScatterActions(true, listPlot);
            drawScatterInfo(listPlot);
            listPlots.push(listPlot);
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
    
    let versionedDb = document.getElementById('plotDatabase').value;
    let versionedDbSplt = versionedDb.split(","); // separator given in this page's JSP
    
    let request = {};
    
    request.xEnsembl = document.getElementById('Gene ensembl').value;
    request.xSymbol = document.getElementById('Gene symbol').value;
    request.yEnsembl = yEnsembl;
    request.ySymbol = ySymbol;
    request.db = versionedDbSplt[1];
    request.version = versionName2id(versionedDbSplt[0]);
    request.versionName = versionedDbSplt[0];
    request.contingencyVertical = 'true';
    request.cvar = document.getElementById('tblPlotShape').value; // options match cvarsHeaders.txt file in webFiles/
    
    return request;
    
}

// makes an html table with the global info for the specified gene
function makeInfoTable(geneId, geneLab) {
    
    let info = infoObj.geneInfoArray.find(gi => gi.id === geneId).info;
    console.log(infoObj.geneInfoArray.find(gi => gi.id === geneId));
    
    let nrows = infoObj.headers.length;
    let html = '<table> <tr> <th></th> <th>' + geneLab + '</th> </tr>';
    for (let i = 0; i < nrows; i++) {
        html += '<tr>';
        html += '<th>' + infoObj.headers[i] + '</th>';
        html += '<td>' + info[i] + '</td>';
        html += '</tr>';
    }
    html += '</table>';
    
    return html;
    
}

// states in the plot options what are the data series which are meant to be lines
// (default is points), these line series are the breaks for discretizing
// the expression of genes. This is the only plot option currently configured
// client side as it is difficult to parse in server java. The series indices must be
// the keys of the object giving the series type appended to the options object
function parseBrks(plotObj) {
    
    plotObj.options.series = {};
    for (let i = 0; i < plotObj.brksSeries.length; i++) {
        plotObj.options.series[plotObj.brksSeries[i]] = {
            type: 'line',
            color: '#8a8a8a',
            lineDashStyle: [2, 2],
            lineWidth: 1.5
        };
    }
    
}

// draws chart inside div given a literal object and options formated as per 
// the google charts documentation
function drawScatter(plotObj) {
    
    let gcTable = new google.visualization.DataTable(plotObj.scatterDataMod);
    let scatterDiv = document.getElementById('scatter_div_' + plotObj.dashboardSuffix); // div created dinamically in gene list row click event
    let chart = new google.visualization.ComboChart(scatterDiv); // ComboChart supports both scatter and line in one chart
    chart.draw(gcTable, plotObj.options);
    
}

// draws a custom legend for the scatter plots as the built-in for google charts
// is very limited regarding customizations
function drawLegend(plotObj) {
    
    let legendDiv = document.getElementById('legend_div_' + plotObj.dashboardSuffix); // div created dinamically in gene list row click event
    legendDiv.addEventListener("contextmenu", e => e.preventDefault()); // do not bring out the right-click menu when right clicking anywhere in the legend
    legendDiv.innerHTML += plotObj.legend.html; // server-generated html table
    
    // add an mouse click event to the cells of the legend for interactivity
    let legendTab = legendDiv.getElementsByTagName('table')[0]; // just added this element to the document
    let rows = legendTab.getElementsByTagName('tr');
    let i, j, cells, td;
    for (i = 0; i < rows.length; i++) {
        cells = rows[i].getElementsByTagName('td');
        for (j = 0; j < cells.length; j++) {
            td = cells[j];
            if ( td.innerHTML !== " " && j % 2 === 1) { // cell is not empty (server sends empty cells with a single space) nor it is a css (shape of the legend at even indices of table), add event
                restoreLegend(plotObj, td); // useful whe redrawing the legends in the quick view to keep the clicked cells after changing between dashboards
                td.addEventListener("mousedown", function(e) {
                    switch (e.which) {
                        case 1: // left click
                            showSeries(e, plotObj);
                            break;
                        case 3: // right click
                            hideSeries(e, plotObj);
                            break;
                    }
                });
            }
        }
    }
    
}

// restores a clicked table cell in a legend if the value of the cell is in the selected series of a plot
function restoreLegend(plotObj, td) {
    
    if (plotObj.legendMode === '') { // plot was just requested, cant have a previous legend state
        return;
    }
    
    let group = td.innerHTML.split(" ")[0]; // strip sample size (server sends the group's name together with sample size separated by space)
    if (plotObj.legendMode === 'show') { // colour cell if group was selected
        if (plotObj.selectedSeries.has(group)) td.style.backgroundColor = '#b0bcff';
    } else if (plotObj.legendMode === 'hide') { // colour cell if group was not selected
        if (!plotObj.selectedSeries.has(group)) td.style.backgroundColor = '#ffc2c2';
    }
    
}

// exclusively shows in the scatter plots clicked groups in the legend 
function showSeries(event, plotObj) {
    
    let td = event.target; // clicked legend table cell
    if (plotObj.legendMode !== "show") { // exit "hide" mode
        plotObj.selectedSeries.clear();
        resetLegend(td.parentNode.parentNode); // passes legend div's table tbody to deselect clicked cells
        plotObj.legendMode = "show";
    }
    
    // select or deselect series
    let series = td.innerHTML.split(" ")[0]; // strip sample size (server sends the group's name together with sample size separated by space)
    if (plotObj.selectedSeries.has(series)) {
        td.style.backgroundColor = 'white';
        plotObj.selectedSeries.delete(series);
    } else {
        td.style.backgroundColor = '#b0bcff';
        plotObj.selectedSeries.add(series);
    }
    
    updateChart(plotObj);
    
}

// exclusively shows in the scatter plots non-clicked groups in the legend 
function hideSeries(event, plotObj) {
    
    let td = event.target; // clicked legend table cell
    if (plotObj.legendMode !== "hide") { // exit 'show' mode
        plotObj.scatterData.cols.filter(col => col.role === "data")
                .map(col => col.label)
                .forEach(lab => plotObj.selectedSeries.add(lab)); // select all series in the data
        resetLegend(td.parentNode.parentNode); // passes legend div's table tbody to deselect clicked cells
        plotObj.legendMode = "hide";
    }
    
    // select or deselect series
    let series = td.innerHTML.split(" ")[0]; // strip sample size (server sends the group's name together with sample size separated by space)
    if (plotObj.selectedSeries.has(series)) { 
        td.style.backgroundColor = '#ffc2c2';
        plotObj.selectedSeries.delete(series);
    } else {
        td.style.backgroundColor = 'white';
        plotObj.selectedSeries.add(series);
    }
    
    updateChart(plotObj);
    
}

// redraws the chart based on the series selected by the user when clicking
// the interactive table legend
function updateChart(plotObj) {
    
    if (plotObj.selectedSeries.size > 0) { // work to do!
        // find indices of columns to be kept for each selected series
        let reg = new RegExp([...plotObj.selectedSeries].join('|'));
        let regMatch = plotObj.scatterData.cols.map(col => reg.test(col.label)); // see if data columns match any selected series
        regMatch[0] = true; // domain x-values should always be selected
        let ncols = regMatch.length;
        for (let i = ncols - plotObj.brksSeries.length; i < ncols; i++) regMatch[i] = true; // series for break lines should always be selected

        // construct a new scatterData object with all columns ()
        plotObj.scatterDataMod = {};
        plotObj.scatterDataMod.cols = plotObj.scatterData.cols;

        // for every row in the scatterData keep fields to be shown and set the ones to be hidden to missing values
        let nrows = plotObj.scatterData.rows.length;
        plotObj.scatterDataMod.rows = new Array(nrows);
        for (let i = 0; i < nrows; i++) {
            plotObj.scatterDataMod.rows[i] = {c: plotObj.scatterData.rows[i]
                        .c.map((col, j) => regMatch[j] ? col : null)}; // the 'c' key is used according to the google charts format
        }
            
    } else { // all series should be shown
        plotObj.scatterDataMod = plotObj.scatterData;
    }

    drawScatter(plotObj); // redraw chart
}

// visually restores the legend to its original state (no clicked/highlighted cells)
function resetLegend(tbody) {
    
    for (let i = 0; i < tbody.children.length; i++) {
        for (let j = 0; j < tbody.children[i].children.length; j++) {
            tbody.children[i].children[j].style.backgroundColor = "white";
        }
    }
    
}

// appends to div a contingency html table sent by the server to complement 
// the corresponding scatter plot 
function drawContingency(plotObj) {
    
    let contingencyDiv = document.getElementById('contingency_div_' + plotObj.dashboardSuffix); // div created dinamically in gene list row click event
    contingencyDiv.innerHTML += plotObj.contingency.html;
    
}

// draws some action buttons related to the curretly shown scatter plot
function drawScatterActions(forTable, plotObj) {
    
    // a nested div is neccessary to avoid taking space of other divs in the dashboard when displaying buttons with the same size through display: table
    let scatterActionsDiv = document.getElementById('scatterActions_div_' + plotObj.dashboardSuffix); // div created dinamically in gene list row click event
    let scatterActionsButtonsDiv = document.createElement('div');
    scatterActionsButtonsDiv.classList.add('scatterActionsButtons'); // add class defined in CSSS for the page
    scatterActionsDiv.appendChild(scatterActionsButtonsDiv);
    
    // add a button to the legend which can reset it to its original state
    let resetButton = document.createElement('button');
    resetButton.innerHTML = 'Reset legend';
    resetButton.addEventListener('click', function(e) {
        plotObj.selectedSeries.clear();
        plotObj.legendMode = '';
        updateChart(plotObj); // redraw chart
        let legendDiv = document.getElementById('legend_div_' + plotObj.dashboardSuffix);
        resetLegend(legendDiv.querySelector('tbody')); // passes legend div's table tbody to deselect clicked cells
    });
    $(resetButton).button(); // assign jquery's UI CSS class
    scatterActionsButtonsDiv.appendChild(resetButton);
    
    if (forTable) {
        // add button and actions to add plot to quick view
        let quickViewButton = document.createElement("button");
        quickViewButton.innerHTML = 'Add to quick view';
        quickViewButton.addEventListener('click', function() {
            document.getElementById('quickViewDiv').style.display = 'block'; // show the div where all quick view related goes
            let quickViewDashsDiv = document.getElementById('quickViewDashsDiv');
            let nquickDash = quickView.length;
            // create a dashboard for the plot and add it to dashboards div
            quickViewDashsDiv.innerHTML += createDashboard(quickSuffix + nquickDash); // use dashboard index as suffix for divs
            // add current plot data to global quick view array (deep copy)
            quickView.push($.extend(true, {}, plotObj));
            quickView[nquickDash].selectedSeries = new Set(plotObj.selectedSeries); // set needs to be handled manually for deep copy
            quickView[nquickDash].dashboardSuffix = quickSuffix + nquickDash; // update with correct suffix for this plot's data
            // create a radio button to show the plot and click it
            let radio = createQuickViewRadio(quickView[nquickDash]);
            $(radio).click();
        });
        // add to DOM and style
        scatterActionsButtonsDiv.appendChild(quickViewButton);
        $(quickViewButton).button();
        
    } else { // for quick view
        scatterActionsButtonsDiv.appendChild(createPlotCompDropdown(plotObj));
    }
    
}

// makes a radio button for the quick view functionality
function createQuickViewRadio(plotObj) {
    
    let radio = document.createElement('input');
    radio.type = 'radio';
    radio.value = plotObj.dashboardSuffix;
    radio.name = 'quickViewRadios';
    radio.id = 'quickViewRadio_' + plotObj.dashboardSuffix;
    let label = document.createElement('label');
    label.setAttribute('for', radio.id);
    label.innerHTML =  plotObj.request.ySymbol + ' - ' + plotObj.request.db + 
            ' ' + plotObj.request.version;
    
    let quickViewRadiosDiv = document.getElementById('quickViewRadiosDiv');
    // add event to show corresponding dashboard
    radio.addEventListener('change', function(e) {
            // hide all dashboards
            let quickViewDashsDiv = document.getElementById('quickViewDashsDiv');
            let quickViewDashboards = [...quickViewDashsDiv.children];
            let targetDash = document.getElementById('dashboard_' + e.target.value);
            if (quickViewDashboards.length > 0) { // if there are plots in quick view hide them and clean the dashboard that will be used to plot
                quickViewDashboards.forEach(d => d.style.display = 'none');
                [...targetDash.children].forEach(div => div.innerHTML = '');
            }
            // show corresponding dashboard and plot
            targetDash.style.display = "flex";
            updateChart(plotObj); // update is used instead of drawScatter to keep the series shown when changing between dashboards
            drawLegend(plotObj);
            drawContingency(plotObj);
            drawScatterActions(false, plotObj);
            drawScatterInfo(plotObj);
    });
    
    // add to DOM and style radio button
    quickViewRadiosDiv.appendChild(label);
    quickViewRadiosDiv.appendChild(radio);
    $(radio).checkboxradio();
    
    return radio;
}

function createPlotCompDropdown(plotObj) {
    
    let plotCompDropdownDiv = document.createElement('div');
    plotCompDropdownDiv.classList.add('plotCompDropdown');
    plotCompDropdownDiv.style.display = 'none';
    plotCompDropdownDiv.addEventListener('click', function(e) {
        if (e.target.tagName === 'A') {
            e.preventDefault();
            let i = parseInt(e.target.attributes['name'].value);
            document.getElementById('xgeneEnsembl').value = // gene of the gene list
                    plotObj.request.xEnsembl;
            document.getElementById('y0geneEnsembl').value = // gene in y-axis of the currently shown plot
                    plotObj.request.yEnsembl;
            document.getElementById('y1geneEnsembl').value = // gene in y-axis of the selected plot in the menu
                    quickView[i].request.yEnsembl;
            document.getElementById('xgeneSymbol').value = // gene of the gene list
                    plotObj.request.xSymbol;
            document.getElementById('y0geneSymbol').value = // gene in y-axis of the currently shown plot
                    plotObj.request.ySymbol;
            document.getElementById('y1geneSymbol').value = // gene in y-axis of the selected plot in the menu
                    quickView[i].request.ySymbol;
            document.getElementById('plot0Version').value = // data version of the currently shown plot
                    plotObj.request.version;
            document.getElementById('plot1Version').value = // data version of the selected plot in the menu
                    quickView[i].request.version;
            document.getElementById('plot0Database').value = // database of the currently shown plot
                    plotObj.request.db;
            document.getElementById('plot1Database').value = // databasen of the selected plot in the menu
                    quickView[i].request.db;
            // submit form and hide the dropdown
            document.getElementById('plotComparisonForm').submit();
            plotCompDropdownDiv.style.display = 'none';
        }
    });
    
    // add "options" which will be implemented as anchors
    let option, i;
    for (i = 0; i < quickView.length; i++) { // rest of options
        option = document.createElement('a');
        option.href = '#';
        option.setAttribute('name', i);
        option.innerHTML = quickView[i].request.ySymbol + ' - ' + 
                quickView[i].request.db + ' ' + quickView[i].request.version;
        option.classList.add('dropdownOption');
        plotCompDropdownDiv.appendChild(option);
    }
    
    // create the button to trigger the dropdown
    let plotCompButton = document.createElement('button');
    plotCompButton.innerHTML = 'Side by side with...';
    plotCompButton.addEventListener('click', function() {
        if (plotCompDropdownDiv.style.display === 'none') {
            plotCompDropdownDiv.style.display = 'block';
        } else {
            plotCompDropdownDiv.style.display = 'none';
        }
    });
    $(plotCompButton).button(); // add jquery ui style
    
    // create the wrapper div and add the elements
    let plotCompWrapperDiv = document.createElement('div');
    plotCompWrapperDiv.classList.add('plotCompWrapper');
    plotCompWrapperDiv.appendChild(plotCompButton);
    plotCompWrapperDiv.appendChild(plotCompDropdownDiv);
    
    return plotCompWrapperDiv;
}

function drawScatterInfo(plotObj) {

    let scatterInfoDiv = document.
            getElementById('scatterInfo_div_' + plotObj.dashboardSuffix); // div created dinamically in gene list row click event
    scatterInfoDiv.innerHTML = plotObj.info;
    
}
