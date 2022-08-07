/* 
 * TODO:
 * - Add spinning wheel while waiting for the computation so it does not look
 *   like the page has an error or that it hangs
 * - Cluster correlation values as absolute value? (still show direction in plot)
 *   currently done this way already but not sure if its the most correct approach
 * - Find a way to be able to select all dendrogram cuttofs, the clustergrammer
 *   developer promised to make some examples as this is not in documentation 
 * - Network with G seems to have weird clustering progression, potentially
 *   because of setting the diagonals to 0? make sure it works well, probably
 *   neet to sort out last point to look at the clustering progression in detail
 * - Dinamically estimate a good default dendrogram cut point when the network
 *   is drawn (probably after two points before this is done)
 * - Fix headers css which seems to be overwritten by clustergrammers css
 * 
 */

$(document).ready(function() {
    
    // request network from info from last page
    $.ajax({
        url: "NetworkServlet",
        type: "GET",
        data: parseNetworkRequest(),
        success: function(resp) {
           console.log(resp);
           drawNetwork(resp); // draw heatmap
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
    
    // get ensembl IDs retrieved by the checker from previous page and also version and metric sent from previous page
    let goodGenesInput = document.getElementById('Gene set');
    let networkDbVersionInput = document.getElementById('Version');
    let networkDbInput = document.getElementById('Database');
    
    return {ensembls: goodGenesInput.value, 
        version: networkDbVersionInput.value, db: networkDbInput.value};
    
}

function drawNetwork(network_data) {
    
    let args = {
        root: '#heatmapDiv',
        network_data: network_data
    };
    let cgm = Clustergrammer(args);
    
}
