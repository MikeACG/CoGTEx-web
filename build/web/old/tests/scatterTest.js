/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/* global google */

google.charts.load('current', {'packages':['corechart']});

var scatterData;
var options;
var brksSeries;
var selectedSeries;
var legendMode;

function drawScatter(currentData) {
    let gcTable = new google.visualization.DataTable(currentData);
    let scatterDiv = document.getElementById('scatter_div');
    let chart = new google.visualization.ComboChart(scatterDiv);
    chart.draw(gcTable, options);
}

function drawLegend(legend) {
    
    let legendDiv = document.getElementById('legend_div');
    legendDiv.addEventListener("contextmenu", e => e.preventDefault()); // do not bring out the right-click menu when right clicking anywhere in the legend
    legendDiv.innerHTML += legend.html;
    
    let restoreButton = document.createElement("button");
    restoreButton.innerHTML = "Restore";
    restoreButton.addEventListener("click", function(e) {
        selectedSeries.clear();
        updateChart();
        restoreLegend(e.target.previousSibling.firstChild); // points to legend div's table tbody
    });
    legendDiv.appendChild(restoreButton);
    
    
    let legendTab = legendDiv.getElementsByTagName('table')[0];
    let rows = legendTab.getElementsByTagName('tr');
    let nrows = rows.length;
    let i, j, cells, td;
    for (i = 0; i < nrows; i++) {
        cells = rows[i].getElementsByTagName('td');
        for (j = 0; j < cells.length; j++) {
            td = cells[j];
            if ( td.innerHTML !== " " && j % 2 === 1) { // cell is not empty nor it is a css, add event
                td.addEventListener("mousedown", function(e) {
                    switch (e.which) {
                        case 1:
                            showSeries(e);
                            break;
                        case 3:
                            hideSeries(e);
                            break;
                    }
                });
            }
        }
    }
    
}

function drawContingency(contingency) {
    let contingencyDiv = document.getElementById('contingency_div');
    contingencyDiv.innerHTML += contingency.html;
}

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

function cleanDashboard() {
    let dash = document.getElementById('dashboard').children;
    for (let i = 0; i < dash.length; i++) {
        dash[i].innerHTML = "";
    }
    selectedSeries = new Set;
    legendMode = "";
}

function parseScatterRequest() {
    let r = {};
    r.db = document.getElementById('database').value;
    r.x = document.getElementById('xgene').value;
    r.y = document.getElementById('ygene').value;
    return r;
}

$(document).ready(function() {
    
    $("#submit").click(function(e) {
        e.preventDefault();
        cleanDashboard();
        $.ajax({
            url: "ScatterServlet",
            type: "GET",
            data: parseScatterRequest(),
            success: function(resp) {
                scatterData = resp.scatter;
                options = resp.scatteropts;
                brksSeries = resp.brksSeries;
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
    });
});

function updateChart() {

    let updatedData;
    
    if (selectedSeries.size > 0) { // work to do!
        // find indices of columns to be kept for each selected series
        let reg = new RegExp([...selectedSeries].join('|'));
        let regMatch = scatterData.cols.map(col => reg.test(col.label));
        regMatch[0] = true; // domain x-values should always be selected
        let ncols = regMatch.length;
        for (let i = ncols - brksSeries.length; i < ncols; i++) regMatch[i] = true; // series for break lines should always be selected

        // construct a new scatterData object with all columns ()
        updatedData = {};
        updatedData.cols = scatterData.cols;

        // for every row in the scatterData keep fields to be shown and set the ones to be hidden to missing values
        let nrows = scatterData.rows.length;
        updatedData.rows = new Array(nrows);
        for (let i = 0; i < nrows; i++) updatedData.rows[i] = {c: scatterData.rows[i].c.map((col, j) => regMatch[j] ? col : null)};
            
    } else {
        updatedData = scatterData;
    }

    // redraw chart
    drawScatter(updatedData);
}

function restoreLegend(tbody) {
    
    for (let i = 0; i < tbody.children.length; i++) {
        for (let j = 0; j < tbody.children[i].children.length; j++) {
            tbody.children[i].children[j].style.backgroundColor = "white";
        }
    }
    
}

function showSeries(event) {
    
    let td = event.target;
    if (legendMode !== "show") {
        selectedSeries.clear();
        restoreLegend(td.parentNode.parentNode); // points to the legend's table tbody
        legendMode = "show";
    }
    
    // select or deselect series
    let series = td.innerHTML.split(" ")[0]; // strip sample size
    
    if (selectedSeries.has(series)) {
        td.style.backgroundColor = 'white';
        selectedSeries.delete(series);
    } else {
        td.style.backgroundColor = '#b0bcff';
        selectedSeries.add(series);
    }
    updateChart();
    
}

function hideSeries(event) {
    
    let td = event.target;
    if (legendMode !== "hide") {
        scatterData.cols.filter(col => col.role === "data")
                .map(col => col.label)
                .forEach(lab => selectedSeries.add(lab)); // select all scatterData series
        restoreLegend(td.parentNode.parentNode); // point to the legend's table tbody
        legendMode = "hide";
    }
    
    // select or deselect series
    let series = td.innerHTML.split(" ")[0]; // strip sample size
    if (selectedSeries.has(series)) {
        td.style.backgroundColor = '#ffc2c2';
        selectedSeries.delete(series);
    } else {
        td.style.backgroundColor = 'white';
        selectedSeries.add(series);
    }
    updateChart();
    
}
