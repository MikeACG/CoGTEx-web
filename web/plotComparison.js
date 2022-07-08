/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global google */
google.charts.load('current', {'packages':['corechart']});

// constructor for objects keeping plot information
function Coplot(dashboardSuffix, scatterData, options, brksSeries, 
    selectedSeries, legendMode, legend, contingency, request) {
    
    this.dashboardSuffix = dashboardSuffix; // created divs for displaying plots in the data table (gene list) will have this suffix  in their ids
    this.scatterData = scatterData; // keeps the current shown plot's in the data table base data
    this.options = options; // keeps the current shown plot's in the data table options
    this.brksSeries = brksSeries; // indices indicating which series from scatterData are break lines (giving the discrete bins of gene expression) as opposed to points
    this.selectedSeries = selectedSeries; // keeps the current series (legend items) selected by the user to be displayed in the plot inside the data table
    this.legendMode = legendMode; // keeps the current legend mode (show/hide series) for the plot shown in the data table
    this.legend = legend; // keeps the current legend for the plot shown in the data table 
    this.contingency = contingency; // keeps the current contngency table for the plot shown in the data table
    this.scatterDataMod = scatterData; // keeps a version of scatterData which may be modified later
    this.request = request; // the request to the server that originated this coplo
    
}
var plots = [{}, {}];


// after the page loads, request the plots
$(document).ready(function() {
    
    requestScatter(0);
    requestScatter(1);
    
});

// requests the server for the data necessary to build a scatterplot between a
// pair of genes including legend and contigency table info
function requestScatter(index) {
    
    let request = parseScatterRequest(index);
    $.ajax({
        url: "ScatterServlet",
        type: "GET",
        data: request,
        success: function(resp) {
            // global variable assignment of gene list plot
            plots[index] = new Coplot(index, resp.scatter, resp.scatteropts, 
                resp.brksSeries, new Set, '', resp.legend, resp.contingencyTable, request);
            // display plot
            let plotComparisonDiv = document.getElementById('plotComparisonDiv');
            plotComparisonDiv.appendChild(createDashboard(index));
            parseBrks(plots[index]);
            drawScatter(plots[index]);
            drawLegend(plots[index]);
            drawContingency(plots[index]);
            drawScatterActions(plots[index]);
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
function parseScatterRequest(index) {
    
    let request = {};
    
    request.xEnsembl = document.getElementById('xEnsembl').value;
    request.xSymbol = document.getElementById('xSymbol').value;
    request.yEnsembl = document.getElementById('y' + index + 'Ensembl').value;
    request.ySymbol = document.getElementById('y' + index + 'Symbol').value;
    request.db = document.getElementById('plot' + index + 'Db').value;
    request.version = document.getElementById('plot' + index + 'V').value;
    request.contingencyVertical = 'falss';
    
    return request;
    
}

// returns the structure of divs used for scatter plot between a pair of genes, ids of divs are appended a suffix
function createDashboard(suffix) {
    
    let dashboard = Object.assign(document.createElement('div'), 
        {id: 'dashboard_' + suffix, classList: ['dashboardDiv'], 
            innerHTML: '<div id="legend_div_' + suffix + '" class="legendDiv"></div>' + 
            '<div id="scatter_div_' + suffix + '" class="scatterDiv"></div>' + 
            '<div id="contingency_div_' + suffix + '" class="contingencyDiv"></div>' +
            '<div id="scatterActions_div_' + suffix + '" class=scatterActionsDiv></div>'
        }
    );
    
    return dashboard;
}

// states in the plot options what are the data series which are meant to be lines
// (default is points), these line series are the breaks for discretizing
// the expression of genes. This is the only plot option currently configured
// client side as it is difficult to parse in java. The series indices must be
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
function drawScatterActions(plotObj) {
    
    let scatterActionsDiv = document.getElementById('scatterActions_div_' + plotObj.dashboardSuffix); // div created dinamically in gene list row click event

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
    scatterActionsDiv.appendChild(resetButton);
    
    // add link to gene info for passed gene
    let infoLink = document.createElement('a');
    infoLink.innerHTML = 'Go to ' + plotObj.request.ySymbol + ' GeneCards page';
    infoLink.target = "_blank"; // open in new tab
    infoLink.href = "https://www.genecards.org/cgi-bin/carddisp.pl?gene=" 
            + plotObj.request.yEnsembl; // keep the base URL updated if gene cards structure changes
    $(infoLink).button(); // assign jquery's UI CSS class
    scatterActionsDiv.appendChild(infoLink);
    
}
