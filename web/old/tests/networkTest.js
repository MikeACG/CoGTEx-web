/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function() {
        
    $.ajax({
        url: "NetworkServlet",
        type: "GET",
        data: parseNetworkRequest(),
        success: function(resp) {
           console.log(resp);
           drawNetwork(resp);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log(jqXHR);
            console.log(textStatus);
            console.log(errorThrown);
        },
        dataType: "json"
    });
    
});

function parseNetworkRequest() {
    
    let genesetBox = document.getElementById('geneSet');
    let geneset = genesetBox.value.trim().replace(/\s+/g, "|");
    console.log(geneset);
    
    return {ensembls: geneset};
    
}

function drawNetwork(network_data) {
    
    var args = {
        root: '#container',
        network_data: network_data
    };
    var cgm = Clustergrammer(args);
    
    // add event to gene formatter
    let genesForm = document.getElementById('genesForm');
    genesForm.addEventListener('submit', function(e) {
        e.preventDefault();
        let genesArea = document.getElementById('genes');
        let genes = genesArea.value.trim().replace(/\s+/g, '');
        genes = genes.replace(/,/g, '\n');
        genesArea.value = genes;
    });
    
}

/* this was a very early test with a fixed network
$(document).ready(function() {
    
    //var network_data = {"row_nodes": [{"name": "POMC", "ini": 6, "clust": 3, "rank": 0, "rankvar": 5}, {"name": "GZMK", "ini": 5, "clust": 0, "rank": 1, "rankvar": 1}, {"name": "GZMA", "ini": 4, "clust": 1, "rank": 3, "rankvar": 4}, {"name": "PRL", "ini": 3, "clust": 4, "rank": 4, "rankvar": 3}, {"name": "PRF1", "ini": 2, "clust": 2, "rank": 2, "rankvar": 2}, {"name": "GH1", "ini": 1, "clust": 5, "rank": 5, "rankvar": 0}], "col_nodes": [{"name": "    POMC", "ini": 6, "clust": 3, "rank": 0, "rankvar": 5}, {"name": "GZMK", "ini": 5, "clust": 0, "rank": 1, "rankvar": 1}, {"name": "GZMA", "ini": 4, "clust": 1, "rank": 3, "rankvar": 4}, {"name": "PRL", "ini": 3, "clust": 4, "rank": 4, "rankvar": 3}, {"name": "PRF1", "ini": 2, "clust": 2, "rank": 2, "rankvar": 2}, {"name": "GH1", "ini": 1, "clust": 5, "rank": 5, "rankvar": 0}], "links": [], "mat": [[1.0, -0.09, -0.1, 0.86, -0.09, 0.83], [-0.09, 1.0, 0.9, -0.03, 0.83, -0.01], [-0.1, 0.9, 1.0, -0.01, 0.91, 0.0], [0.86, -0.03, -0.01, 1.0, -0.02, 0.96], [-0.09, 0.83, 0.91, -0.02, 1.0, -0.01], [0.83, -0.01, 0.0, 0.96, -0.01, 1.0]], "cat_colors": {"row": {}, "col": {}}, "global_cat_colors": {}, "matrix_colors": {"pos": "red", "neg": "blue"}, "linkage": {"row": [[3.0, 5.0, 0.0685565460040105, 2.0], [2.0, 4.0, 0.14628738838327793, 2.0], [1.0, 7.0, 0.16431676725154984, 3.0], [0.0, 6.0, 0.2696293752542553, 3.0], [8.0, 9.0, 2.3196335917553874, 6.0]], "col": [[3.0, 5.0, 0.0685565460040105, 2.0], [2.0, 4.0, 0.14628738838327793, 2.0], [1.0, 7.0, 0.16431676725154984, 3.0], [0.0, 6.0, 0.2696293752542553, 3.0], [8.0, 9.0, 2.3196335917553874, 6.0]]}, "views": []};
    if (true) {
        var network_data = {
            "row_nodes": [
                {"name": "POMC", "ini": 6, "clust": 3, "rank": null, "rankvar": null, "group": [3, 3, 8, 10]}, 
                {"name": "GZMK", "ini": 5, "clust": 0, "rank": null, "rankvar": null, "group": [0, 0, 9, 10]}, 
                {"name": "GZMA", "ini": 4, "clust": 1, "rank": null, "rankvar": null, "group": [1, 7, 9, 10]}, 
                {"name": "PRL", "ini": 3, "clust": 4, "rank": null, "rankvar": null, "group": [4, 6, 8, 10]}, 
                {"name": "PRF1", "ini": 2, "clust": 2, "rank": null, "rankvar": null, "group": [2, 7, 9, 10]}, 
                {"name": "GH1", "ini": 1, "clust": 5, "rank": null, "rankvar": null, "group": [5, 6, 8, 10]}
            ], 
            "col_nodes": [
                {"name": "POMC", "ini": 6, "clust": 3, "rank": null, "rankvar": null, "group": [3, 3, 8, 10]}, 
                {"name": "GZMK", "ini": 5, "clust": 0, "rank": null, "rankvar": null, "group": [0, 0, 9, 10]}, 
                {"name": "GZMA", "ini": 4, "clust": 1, "rank": null, "rankvar": null, "group": [1, 7, 9, 10]}, 
                {"name": "PRL", "ini": 3, "clust": 4, "rank": null, "rankvar": null, "group": [4, 6, 8, 10]}, 
                {"name": "PRF1", "ini": 2, "clust": 2, "rank": null, "rankvar": null, "group": [2, 7, 9, 10]}, 
                {"name": "GH1", "ini": 1, "clust": 5, "rank": null, "rankvar": null, "group": [5, 6, 8, 10]}
            ],
            "mat": [
                [1.0, -0.09, -0.1, 0.86, -0.09, 0.83], 
                [-0.09, 1.0, 0.9, -0.03, 0.83, -0.01], 
                [-0.1, 0.9, 1.0, -0.01, 0.91, 0.0], 
                [0.86, -0.03, -0.01, 1.0, -0.02, 0.96], 
                [-0.09, 0.83, 0.91, -0.02, 1.0, -0.01], 
                [0.83, -0.01, 0.0, 0.96, -0.01, 1.0]
            ], 
            "matrix_colors": {"pos": "red", "neg": "blue"}
        };
    }
    var args = {
        root: '#container',
        network_data: network_data
    };
    var cgm = Clustergrammer(args);
});
*/
