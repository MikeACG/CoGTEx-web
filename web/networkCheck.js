/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var maxNetworkGenes = 200; // max number of genes that can be plotted in heatmap

$(document).ready(function() {
    
    // attach event to gene checker button
    let checkButton = document.getElementById('checkNetworkGenes');
    checkButton.addEventListener('click', function(e) {
        e.preventDefault();
        $.ajax({
            url: "NetworkCheckServlet",
            type: "GET",
            data: parseNetworkCheckRequest(),
            success: function(resp) {
               console.log(resp);
               showBuildNetworkForm(resp);
            },
            error: function(jqXHR, textStatus, errorThrown) {
                console.log(jqXHR);
                console.log(textStatus);
                console.log(errorThrown);
            },
            dataType: "json"
        });
    });
    
    // show a default gene set (WNT beta catenin signaling from GSEA)
    let defaultSet = 'ADAM17 AXIN1 AXIN2 CCND2 CSNK1E CTNNB1 CUL1 DKK1 DKK4 DLL1 DVL2 FRAT1 FZD1 FZD8 GNAI1 HDAC11 HDAC2 HDAC5 HEY1 HEY2 JAG1 JAG2 KAT2A LEF1 MAML1 MYC NCOR2 NCSTN NKD1 NOTCH1 NOTCH4 NUMB PPARD PSEN2 PTCH1 RBPJ SKP2 TCF7 TP53 WNT1 WNT5B WNT6';
    let networkGeneBox = document.getElementById('networkGeneSet');
    networkGeneBox.innerHTML = defaultSet.replace(/ /g, '\n');
    // attach event to gene set input box to hide network submission form if it changes
    networkGeneBox.addEventListener('input', function() {
        let buildNetworkForm = document.getElementById('buildNetworkForm');
        buildNetworkForm.style.display = 'none';
    });
    
});

function parseNetworkCheckRequest() {
    
    // hide build network form
    let buildNetworkForm = document.getElementById('buildNetworkForm');
    buildNetworkForm.style.display = 'none';
    
    // get input genes
    let genesetBox = document.getElementById('networkGeneSet');
    let geneset = genesetBox.value.trim().replace(/\s+/g, "|");
    
    return {genes: geneset};
    
}

function showBuildNetworkForm(geneArray) {
    
    // place ensembls ids checked by server in hidden input
    let goodGenesInput = document.getElementById('goodNetworkGenes');
    goodGenesInput.value = geneArray.join('|');
    
    // display number of genes in the database and allow build network if possible
    let buildNetworkButton = document.getElementById('buildNetwork');
    let nGood = geneArray.length;
    let msg = ' ';
    if (nGood < 2) {
        msg = msg + '(not enough genes matched)';
        buildNetworkButton.style.display = 'none';
    } else if (nGood > maxNetworkGenes) {
        msg = msg + '(maximum genes to plot is ' + maxNetworkGenes + ', please reduce your query)';
        buildNetworkButton.style.display = 'none';
    } else { // network can be built, show button to build
        buildNetworkButton.style.display = 'block';
    }
    let nGoodGenesInput = document.getElementById('nforNetwork');
    nGoodGenesInput.value = nGood + msg;
    
    // show build network form
    let buildNetworkForm = document.getElementById('buildNetworkForm');
    buildNetworkForm.style.display = 'block';
}


