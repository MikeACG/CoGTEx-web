/* 
 * AUTHOR: Miguel A. Cortés Guzmán based on previous versions by Victor M. Treviño Alvarado
 * 
 * TODO:
 * - Implement a more efficient column search: currently each time the table is
 *   drawn, the data of all input column boxes is checked against every row
 * - help information for: columns, buttons (e.g. Qunatile 99 actual values)
 * - Prevent deselecting all fields
 * - Box plots: add options for showing outliers, convert to violin plot. Place
 *   tickmarks if possible, make outlier points filled with black border, add
 *   boxplot for overall distribution 
 *   (requires server work)
 * - For boxplots of z-score version, there is no "All data" matrix to plot
 *   (its is not calculated in our workflow) yet the option is in the page
 *   either compute this in the server from the base expression matrix or
 *   calculate it separately and put sqlite in "source-data" folder or
 *   remove the option conditionally when z-score version is selected
 * - Add some option to plot original unprocessed GTEx TPMs (need to add an
 *   sqlite to "source-data" of the original matrix then figure out hot to neatly
 *   give the option to display it in the page)
 * - Add version to title of boxplots
 * - Make SQLites for covariate coexpressions and enable them for networks?
 * - For the network metric to use, it looks a bit ugly to pass the path in the
 *   "source-matrix/sql" folder as the value of the selector menu, we could pass
 *   a shorter name for the metric and find the file in the server
 * - Show genes that are missing in database before building network
 * - style select boxes 
 * 
 */

/* global Plotly */
var delayMiliseconds = 500; // time waited before searching after typing in input boxes
var timeout = null; // for waiting until thee user finishes typing to search columns
var plotCount = 0; // for assigning different div Ids to the plots generated in a session
// the following object is implemented to search the dataTable more efficiently, 
// we will be using a custom function programmed for the dataTables search plugin ($.fn.dataTable.ext.search.push)
// to search which is fired for each row in the table, this object computes and holds the necessary data
// for performing the search and avoids processing this data inside the plugin unnecessarily repeating
// pre-calculations for each row and just grabbbing the data as neccessary from this global object
var searchObj = {
    perColumnInputs: [], // holds the <input> objects of the footers of the data table 
    shownHeaders: [], // headers of the currently shown rows in the data table
    readPerColumnInputs: function() { // read the text inputed by the user in the footer of table, which cols are shown can also be inferred from what inputs are catched by the document selector
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
        this.generalInput = $('div.dataTables_filter input').val().toLowerCase();
    }
};

// after the page loads, request the information for the genes in the database and configure some events
$(document).ready(function() {
    
    switchSpinner('tabsDiv');
    homeRequest(); // request data for the first time
    
});

// switches on/off the visibility of a div with a spinning wheel while waiting for a process
// if div does not exist in target container it is created and shown
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

// requests the genes in the database of the correct version
function homeRequest() {
    
    $.ajax({
        url: "HomeServlet",
        type: "GET",
        data: {},
        success: function(resp) {
           console.log(resp);
           configPageControls(); // show page controls
           drawInfo(resp); // draw table
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log(jqXHR);
            console.log(textStatus);
            console.log(errorThrown);
        },
        dataType: "json"
    });
    
}

// parses the request for the genes in the database based on version
// unnecesary since merge of versions in a single page
/*
function parseHomeRequest() {
    
    // get database version and copy to hidden field to pass to gene lists page
    let version = document.getElementById('versionSup').innerHTML;
    let versionInput = document.getElementById('dataVersion');
    versionInput.value = version;
    
    // reflect the version in the network form hidden input
    let networkVersionInput = document.getElementById('networkDbVersion');
    networkVersionInput.value = version;
    
    return {version: version};
}
*/

// shows the page controls and attaches the proper events
function configPageControls() {
    
    // get array of tab divs and of list elements associated with the tabs
    let tabDivs = [...document.getElementById('tabsDiv').children];
    let tabLinksList = [...document.getElementById('tabLinksList').children];
    
    // configure each tab's control
    setupTab('oneGene', tabDivs, tabLinksList);
    setupTab('network', tabDivs, tabLinksList);
    setupTab('downloads', tabDivs, tabLinksList);
    
    // show page controls after table is drawn
    let tabIndependentDiv = document.getElementById('tabIndependentDiv');
    tabIndependentDiv.style.display = "block";
    
    // show one gene tab
    let oneGeneTab = document.getElementById('oneGeneTab');
    oneGeneTab.style.display = "block";
    
    // get database version and copy to fields that use it
    // unnecessary since merge of versions in a single page
    /*
    let version = document.getElementById('versionSup').innerHTML;
    let plotVersionSelect = document.getElementById('expDistPlotVersion');
    let listVersionInput = document.getElementById('dataVersion');
    let networkVersionInput = document.getElementById('networkDbVersion');
    plotVersionSelect.value = listVersionInput.value = networkVersionInput.value = version;
    */
    
    // configure link and form to go to opposite version of database
    // unnecessary since merge of versions in a single page
    /*
    let versionChangeInput = document.getElementById('versionChangeInput');
    let versionChangeLink = document.getElementById('versionChangeLink');
    versionChangeLink.href = '#';
    versionChangeLink.addEventListener('click', function() {
        $('#versionChangeForm').submit();
    });
    if (version === 'Base') {
       versionChangeLink.innerHTML = 'Go to CoGtex z-score by cluster';
       versionChangeInput.value = 'Z-score';
    } else {
       versionChangeLink.innerHTML = 'Go to CoGtex base expression';
       versionChangeInput.value = 'Base';
    }
    */
    
    // configure table plots type select to hide/show plot preferences depending on the type of plot
    let plotTypeSelect = document.getElementById('plotType');
    plotTypeSelect.addEventListener('change', function(e) {
        // hide all forms and only show appropiate one
        let plotPreferencesForms = [...document.getElementById('plotFormsDiv').children];
        plotPreferencesForms.forEach(form => form.style.display = 'none');
        switch (e.target.value) {
            case 'expDist':
                document.getElementById('expDistPreferencesForm').style.display = 'block';
                break;
            case 'coexpRankComp':
                document.getElementById('coexpRankCompPreferencesForm').style.display = 'block';
                break;
        }
    });
    
    // network submission form is not hidden here because currently genes
    // are the same between versions so there is no need to recheck genes
    // to reshow the form if they are already checked before we chenged version
    // if this changes hide the network build form here
    
}

// configures a tab to control the page
function setupTab(tabPrefix, tabDivs, tabLinksList) {
    
    let link = document.getElementById(tabPrefix + 'Link');
    
    link.addEventListener('click', function(e) {
        if (!e.target.classList.contains('tab-active')) {
            tabDivs.forEach(div => div.style.display = 'none'); // hide all tabs
            tabDivs.find(div => div.id === tabPrefix + 'Tab').style.display = 'block'; // show the div associated with the tab
            tabLinksList.forEach(link => link.classList.remove('tab-active')); // set all tabs inactive
            e.target.classList.add('tab-active'); // set this tab active
        }
    });
    
}

// draws and configures the gene information using DataTables third-party library
function drawInfo (info) {
    
    let infoTable = document.getElementById('infoTable');
    infoTable.innerHTML = info.dataTableHTML; // header and footer of table is generated in server
    // set properties of the search object that are neccessary to build the data table for the first time
    searchObj.readAllHeaders(info);
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
    let dataTable = $(infoTable).DataTable(info.dataTable);
    searchObj.readPerColumnInputs(); // now that table is drawn, update shown cols
    // hide the waiting div
    switchSpinner('tabsDiv');
    
    // configure the general search in the table with delay
    $('div.dataTables_filter input').off('keyup.DT input.DT'); // remove default behavior of general search box
    $('div.dataTables_filter input').on('keyup search', function() { // listen to both keyboard presses and clicking the "X"
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
    let infoTableBody = infoTable.getElementsByTagName('tbody')[0];
    $(infoTableBody).on('click', 'td', function () {
        let tdClasses = [...this.classList];
        if (tdClasses.indexOf("details-control") >= 0) { // only fire event if the cell has the details-control class
            
            let tr = this.parentNode; // gets row to which the clicked cell belongs
            let row = dataTable.row(tr); // allows to use DataTables features with the row
            let rowIdx = row.index(); // index of clicked row in the context of all rows (even filtered non-visible ones)

            if ( row.child.isShown() ) { // simply hide the row and switch CSS
                row.child.hide();
                tr.classList.remove('shown');
            }
            else {
                let allHeaders = dataTable.context[0].aoColumns
                    .map(col => col.title.toLowerCase()); // get all column headers including non-visible columns
                let ensemblIdx = allHeaders.findIndex(h => h.indexOf('ensembl') >= 0); // index of ensembl column
                let symbolIdx = allHeaders.findIndex(h => h.indexOf('symbol') >= 0); // index of gene symbol column
                let symbol = dataTable.context[0].aoData[rowIdx]._aData[symbolIdx]; // gene symbol of clicked row
                let ensembl = dataTable.context[0].aoData[rowIdx]._aData[ensemblIdx]; // ensembl of clicked row
                
                let plotType = detectTablePlotType();
                row.child(createDashboard(plotType.divId + plotCount, plotType.divClass)).show(); // append divs for showing plot of the row
                switchSpinner(plotType.divId + plotCount); // insert spinning wheel while waiting
                tr.classList.add('shown');
                tablePlotsRequestDispatch(plotType.formId, ensembl, symbol); // request table plot of clicked gene
            }
        }
    });
    
    // add click event to table's body cells in general (this must be defined after the details-control cell event)
    $(infoTableBody).on('click', 'td', function() {
        let tdClasses = [...this.classList];
        if (tdClasses.indexOf('details-control') < 0) { // do not fire if the clicked cell has the details-control class (i.e. do not deselct the row, just hide/show plot)
            let tr = this.parentNode; // row to which the clicked cell belongs
            let controlTd = tr.firstChild; // first cell of each rown (first column) is the responsible for details-control
            let trClasses = [...tr.classList];
            if (trClasses.indexOf('selected') >= 0) { // row was already highlighted
                cleanSelectedGene(infoTableBody); // deselect all rows and switch CSS
                controlTd.classList.remove('details-control'); // deactivate details-control from the first cell opf the row
                dataTable.row(this).child.hide(); // hide the row's plot if shown otherwise no effect
                tr.classList.remove('shown'); // remove shown CSS if shown, otherwise no effect
            } else if (trClasses.length > 0) { // row is not highlighted nor it is a child row (these have no classes)
                //cleanDashboard(infoTable, dataTable); // hide any shown rows
                cleanSelectedGene(infoTableBody); // deselect all rows and switch CSS
                tr.classList.add('selected'); // highlight row
                configGeneActions(tr, dataTable); // create actions for selected row (gene)
                controlTd.classList.add('details-control'); // activate details-control for the first cell of this row
            }
        }
    });
    
    // add checkboxes to the document which will act as selectors to hide/show columns
    let selectorsDiv = document.getElementById('selectorsDiv');
    selectorsDiv.innerHTML = info.selectorsHTML; // generated in server according to column names of the data
    $(selectorsDiv).buttonset(); // add jquery user interface (UI) css
    
    // add checkboxes change event to show/hide columns
    selectorsDiv.addEventListener('change', function(e) {
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
    
    // when changing pages of the table, sorting the table or searching something:
    // clean any highlighted rows
    $(infoTable).on('page.dt order.dt search.dt', function (){
        cleanSelectedGene(infoTableBody);
    //    cleanDashboard(infoTable, dataTable);
    });
    
}

// removes datatables 'selected' CSS class from current active rows and
// hides all actions associated with the gene from the page
function cleanSelectedGene(tbody) {
    
    let trs = [...tbody.getElementsByTagName('tr')];
    trs.forEach(tr => {
        tr.classList.remove('selected');
        tr.firstChild.classList.remove('details-control');
    });
    
    document.getElementById('listPreferencesDiv').style.display = 'none'; // hide list preferences
    document.getElementById('externalLinksDiv').style.display = 'none'; // hide external links
    
}

// configures the actions that can be performed with a selected gene in the table
function configGeneActions(tr, dataTable) {
    
    // get data neccessary to pinpoint clicked gene in the table
    let allHeaders = dataTable.context[0].aoColumns
            .map(col => col.title.toLowerCase()); // get all column headers including non-visible columns
    let rowIdx = dataTable.row(tr).index(); // index of clicked row in the context of all rows (even filtered non-visible ones)
    
    // get gene info and add to form for gene list
    let symbol = getGeneInfo(dataTable, allHeaders, rowIdx, 'symbol', 'geneSymbol');
    let ensembl = getGeneInfo(dataTable, allHeaders, rowIdx, 'ensembl', 'geneEnsembl');
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
    link.firstChild.innerHTML = webName + ' for ' + symbol; // jquery UI uses a span inside the anchor
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
                                row[j].toLowerCase().indexOf(filter.cleanFilter) >= 0); // just test exact substring match
                }
                if (searchObj.generalInput === '') { // user not using the general search box
                    generalLogicals.push(true); // auto passes filter
                } else { // user wrote something in general search box which works as a literal substring match
                    generalLogicals.push(row[j].toLowerCase().indexOf(searchObj.generalInput) >= 0);
                }
                i++; // increase index of per col input boxes
            }
        }
        
        // row should be shown if all per column input boxes tests are true AND if at least one of the general search tests is true
        return perColLogicals.every(Boolean) && generalLogicals.some(Boolean);
    }
);

// parses the user's filter string applied to a column of the DataTable
function cleanSearchFilter(filter) {
    
    filter = filter.trim().toLowerCase(); // get rid of leading and trailing whitespace and make case insensitive
    
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
    if (isNaN(val)) return false; // missing value in data
    inequality = inequality.split(" ");
    let expression = inequality[0]; // angle brackets and/or equals signs
    let threshold = parseFloat(inequality[1]); // user's value
    if (isNaN(threshold)) return false; // user inputed an invalid number
    
    if (expression === "<") {
        return val < threshold;
    } else if (expression === ">") {
        return val > threshold;
    } else if (expression === "<=") {
        return val <= threshold;
    } else if (expression === ">=") {
        return val >= threshold;
    }
    
    return false; // invalid expression
    
}

// returns the structure of divs used for the table plot of a gene
function createDashboard(divId, divClass) {
    
    
    return '<div id="dashboard" class="dashboardDiv_' + divClass + '">' + 
            '<div id="' + divId + '" class="' + divClass + '"></div>' + 
            '</div>';
    
}

// hides all plots that are currently visible in the table
function cleanDashboard(listTable, dataTable) {
    
    let trs = [...listTable.getElementsByTagName('tr')];
    for (let i = 0; i < trs.length; i++) {
        if ([...trs[i].classList].includes('shown')) {
            dataTable.row(trs[i]).child.hide();
            trs[i].classList.remove('shown');
        }
    }
    
}

// identifies which type of plot to display in the table the user wants
function detectTablePlotType() {

    let plotPreferencesForms = [...document.getElementById('plotFormsDiv').children];
    let selectedForm = plotPreferencesForms.find(form => form.style.display === 'block');
    
    let plotType = {formId: selectedForm.id};
    switch (plotType.formId) {
        case 'expDistPreferencesForm':
            plotType.divId = 'expDist_div';
            plotType.divClass = 'expDistDiv';
            break;
        case 'coexpRankCompPreferencesForm':
            plotType.divId = 'coexpRankComp_div';
            plotType.divClass = 'coexpRankCompDiv';
            break;
    }
    
    return plotType;
    
}

// fires the appropiate request function depending on what the user wants for the table plots
function tablePlotsRequestDispatch(formId, ensembl, symbol) {
    
    switch (formId) {
        case 'expDistPreferencesForm':
            requestBox(ensembl, symbol);
            break;
        case 'coexpRankCompPreferencesForm':
            requestRankComp(ensembl, symbol);
            break;
        default:
            console.log(formId);
    }
    
}

// requests the server for the data necessary to build a box plot by expression
// groups of the gene
function requestBox(ensembl, symbol) {
    
    $.ajax({
        url: "BoxServlet",
        type: "GET",
        data: parseBoxRequest(ensembl, symbol),
        success: function(resp) {
            // display plot
            console.log(resp);
            drawBox(resp.data, resp.layout, resp.medianColors);
            plotCount++;
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log(jqXHR);
            console.log(textStatus);
            console.log(errorThrown);
            plotCount++;
        },
        dataType: "json"
    });
 
}

// creates an object which the server can read to return the data for the box plot
function parseBoxRequest(ensembl, symbol) {
    
    let request = {};
    
    request.ensembl = ensembl;
    request.symbol = symbol;
    request.db = document.getElementById('expDistPlotDb').value;
    request.version = document.getElementById('expDistPlotVersion').value;
    return request;
    
}

// draws chart inside div given an array of traces and a layout formated as per 
// the plotly javascript documentation. medianColors is an array of server-calculated
// colors for the median lines of the boxes
function drawBox(data, layout, medianColors) {
    
    switchSpinner('expDist_div' + plotCount);
    
    let expDistDiv = document.getElementById('expDist_div' + plotCount); // div created dinamically in gene list row click event
    // the then function is a workaround to change the median line color as it is not supported natively by plotly
    Plotly.newPlot(expDistDiv, data, layout).then(gd => {
        // https://community.plotly.com/t/box-and-whisker-median-line-color/1849/2
        let update = {};
        gd.calcdata.forEach((cd, i) => {
           let median = cd[0].med;
           let bdPos = cd[0].t.bdPos;
            update['shapes[' + i + ']'] = {
              type: 'line',
              y0: median,
              y1: median,
              x0: i - bdPos,
              x1: i + bdPos,
              line: {color: medianColors[i], width: 2} // width should match trace.line.width in the server
            };
        });
        Plotly.relayout(gd, update);
    });
    
}

// requests the server for the data necessary to build a scatter plot comparing
// two versions and or databases of the coexpressed partners of a gene
function requestRankComp(ensembl, symbol) {
    
    $.ajax({
        url: "RankCompServlet",
        type: "GET",
        data: parseRankCompRequest(ensembl, symbol),
        success: function(resp) {
            // display plot
            console.log(resp);
            drawRankComp(resp.trace, resp.layout);
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
function parseRankCompRequest(ensembl, symbol) {
    
    let request = {};
    
    request.ensembl = ensembl;
    request.symbol = symbol;
    request.xdb = document.getElementById('coexpRankCompDb1').value;
    request.xVersion = document.getElementById('coexpRankCompVersion1').value;
    request.ydb = document.getElementById('coexpRankCompDb2').value;
    request.yVersion = document.getElementById('coexpRankCompVersion2').value;
    request.compSize = document.getElementById('coexpRankCompSize').value;
    
    return request;
    
}

// draws a rank comparison scatterplot using plotly js
function drawRankComp(trace, layout) {
    
    trace.opacity = document.getElementById("coexpRankCompAlpha").value;
    let coexpRankDiv = document.getElementById("coexpRankComp_div");
    
     switchSpinner('coexpRankComp_div');
    Plotly.newPlot(coexpRankDiv, [trace], layout);
    
}
