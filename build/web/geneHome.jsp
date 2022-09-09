<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
        <script type="text/javascript" src="https://code.jquery.com/ui/1.11.4/jquery-ui.min.js"></script>
        <script type="text/javascript" src="https://cdn.datatables.net/1.10.25/js/jquery.dataTables.min.js"></script>
        <script type="text/javascript" src="https://cdn.datatables.net/buttons/1.3.1/js/dataTables.buttons.min.js"></script>
        <script type="text/javascript" src="https://cdn.datatables.net/buttons/1.3.1/js/buttons.html5.min.js"></script>
        <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.1.3/jszip.min.js"></script>
        <script type="text/javascript" src="https://cdn.plot.ly/plotly-2.3.1.min.js"></script>
        <script type="text/javascript" src="geneHome.js"></script>
        <script type="text/javascript" src="networkCheck.js"></script>
        <link rel="stylesheet" href="http://code.jquery.com/ui/1.11.4/themes/redmond/jquery-ui.css"> 
        <link rel="stylesheet" href="https://cdn.datatables.net/1.10.25/css/jquery.dataTables.min.css">
        <link rel="stylesheet" href="https://cdn.datatables.net/buttons/1.3.1/css/buttons.dataTables.min.css">
        <link rel="stylesheet" href="geneHome.css">
        <title>CoGTEx</title>
    </head>
    
    <body>
        
        <h1>
            <span class="title">CoGTEx: </span>
            <span>a database of system-level human gene expression associations</span>
        </h1>
        
        <div id="tabIndependentDiv" style="display: none;">
            
            <form id="versionChangeForm" style="display: none;" target="_blank" action="geneHome.jsp">
                <input type="hidden" id="versionChangeInput" name="Version">
            </form>
            
            <div>
                <ul id="tabLinksList" class="tab">
                    <li id="oneGeneLink" class="tab tab-active">One gene</li>
                    <li id="networkLink" class="tab">Network</li>
                    <li id="downloadsLink" class="tab">Downloads</li>
                </ul>
            </div>
            
        </div>
        
        <div id="tabsDiv">
            
            <div id="oneGeneTab" style="display: none">

                <div id="infoDiv">
                    
                    <div id="plotPreferencesDiv">
                        <h3 class="title">Table plots preferences</h3>
                        
                        <form id="plotTypeForm">
                            <label for="plotType">Plot type:</label>
                            <select id="plotType">
                                <option value="expDist">Expression distribution</option>
                                <option value="coexpRankComp">Coexpressed genes rank comparison</option>
                            </select>
                        </form>
                        
                        <div id="plotFormsDiv">
                            <form id="expDistPreferencesForm" style="display: block">
                                <label for="expDistPlotDb">Database for figures:</label>
                                <select id="expDistPlotDb">
                                    <optgroup label="Common">
                                      <option value="all">All Samples</option>
                                      <option value="ref" selected>Reference Realization (1 of 20)</option>
                                    </optgroup>
                                    <optgroup label="Covariates">
                                      <option value="SEX_female">Female Only</option>
                                      <option value="SEX_male">Male Only</option>
                                      <option value="AGE_less50">Age < 50</option>
                                      <option value="AGE_50-59">Age 50-59</option>
                                      <option value="AGE_more59">Age > 59</option>
                                      <option value="SMTSISCH_high">Ischemia High</option>
                                      <option value="SMTSISCH_low">Ischemia Low</option>    
                                    </optgroup>
                                </select>
                                <label for="expDistPlotVersion">Data version:</label>
                                <select id="expDistPlotVersion">
                                    <option value="Base">Base expression</option>
                                    <option value="Z-score">Z-score by cluster expression</option>
                                </select>
                            </form>

                            <form id ="coexpRankCompPreferencesForm" style="display: none">
                                <label for="coexpRankCompDb1">Ranking 1:</label>
                                <select id="coexpRankCompDb1">
                                    <optgroup label="Common">
                                        <option value="Min Pearson">Robust Minimum Pearson Realization</option>
                                        <option value="Min Spearman">Robust Minimum Spearman Realization</option>
                                        <option value="Min G">Robust Minimum G Realization</option>
                                    </optgroup>
                                    <optgroup label="Covariates">
                                        <option value="Pearson Age 50-59">Age 50-59</option>
                                        <option value="Pearson Age < 50">Age < 50</option>
                                        <option value="Pearson Age > 59">Age > 59</option>
                                        <option value="Pearson Sex Female">Female Only</option>
                                        <option value="Pearson Sex Male">Male Only</option>
                                        <option value="Pearson Ischemia High">Ischemia High</option>
                                        <option value="Pearson Ischemia Low">Ischemia Low</option>    
                                    </optgroup> 
                                  </optgroup>
                                </select>
                                <label for="coexpRankCompVersion1">Ranking 1 version:</label>
                                <select id="coexpRankCompVersion1">
                                    <option value="Base">TPM-scale expression</option>
                                    <option value="Z-score">Z-score by cluster expression</option>
                                </select>
                                <label for="coexpRankCompDb2">Ranking 2:</label>
                                <select id="coexpRankCompDb2">
                                    <optgroup label="Common">
                                        <option value="Min Pearson">Robust Minimum Pearson Realization</option>
                                        <option value="Min Spearman">Robust Minimum Spearman Realization</option>
                                        <option value="Min G">Robust Minimum G Realization</option>
                                    </optgroup>
                                    <optgroup label="Covariates">
                                        <option value="Pearson Age 50-59">Age 50-59</option>
                                        <option value="Pearson Age < 50">Age < 50</option>
                                        <option value="Pearson Age > 59">Age > 59</option>
                                        <option value="Pearson Sex Female">Female Only</option>
                                        <option value="Pearson Sex Male">Male Only</option>
                                        <option value="Pearson Ischemia High">Ischemia High</option>
                                        <option value="Pearson Ischemia Low">Ischemia Low</option>    
                                    </optgroup>
                                </select>
                                <label for="coexpRankCompVersion2">Ranking 2 version:</label>
                                <select id="coexpRankCompVersion2">
                                    <option value="Base">TPM-scale expression</option>
                                    <option value="Z-score" selected>Z-score by cluster expression</option>
                                </select>
                                <label for="coexpRankCompSize">Number of top genes of ranking 1 to plot:</label>
                                <select id="coexpRankCompSize">
                                    <option value="100">100</option>
                                    <option value="200">200</option>
                                    <option value="300" >300</option>
                                    <option value="500">500</option>
                                    <option value="1000" selected>1000</option>
                                    <option value="2500">2500</option>
                                    <option value="5000">5000</option>
                                    <option value="10000">10000</option>
                                    <option value="20000">20000</option>
                                    <option value="max">All (may take a while depending on your computer specs)</option>
                                </select>
                                <label for="coexpRankCompAlpha">Points transparency:</label>
                                <input type="range" min="0" max="1" step="0.1" value="0.5" id="coexpRankCompAlpha">
                            </form>
                        </div>
                        
                    </div>
                    
                    <div id="selectorsDiv"></div>
                    
                    <table class="display" id="infoTable"></table>

                </div>

                <div id="listPreferencesDiv" style="display: none;">
                    <h3 class="title">Associations list preferences for selected gene</h3>
                    <form id="listPreferencesForm" action="geneList.jsp" method="get" target="_blank">
                        <input type="hidden" id="geneSymbol" name="Gene symbol">
                        <input type="hidden" id="geneEnsembl" name="Gene ensembl">
                        <label for="listSize">Number of top associations to show:</label>
                        <select  name="List size" id="listSize">
                            <option value="100">100</option>
                            <option value="200">200</option>
                            <option value="300" >300</option>
                            <option value="500">500</option>
                            <option value="1000" selected>1000</option>
                            <option value="2500">2500</option>
                            <option value="5000">5000</option>
                            <option value="10000">10000</option>
                            <option value="20000">20000</option>
                            <option value="max">All (may take a while depending on your computer specs)</option>
                        </select>
                        <br>
                        <label for="db">Database to order top associations:</label>
                        <select name="Order database" id="db">
                            <optgroup label="Common">
                                <option value="TPM_Min Pearson">TPM Robust Minimum Pearson Realization</option>
                                <option value="TPM_Min Spearman">TPM Robust Minimum Spearman Realization</option>
                                <option value="TPM_Min G">TPM Robust Minimum G Realization</option>
                                <option value="Z-score_Min Pearson">Z-score Robust Minimum Pearson Realization</option>
                                <option value="Z-score_Min Spearman">Z-score Robust Minimum Spearman Realization</option>
                                <option value="Z-score_Min G">Z-score Robust Minimum G Realization</option>
                            </optgroup>
                            <optgroup label="Covariates">
                                <option value="TPM_Pearson Age 50-59">TPM Age 50-59</option>
                                <option value="TPM_Pearson Age <50">TPM Age < 50</option>
                                <option value="TPM_Pearson Age >59">TPM Age > 59</option>
                                <option value="TPM_Pearson Sex Female">TPM Female Only</option>
                                <option value="TPM_Pearson Sex Male">TPM Male Only</option>
                                <option value="TPM_Pearson Ischemia High">TPM Ischemia High</option>
                                <option value="TPM_Pearson Ischemia Low">TPM Ischemia Low</option>
                                <option value="Z-score_Pearson Age 50-59">Z-score Age 50-59</option>
                                <option value="Z-score_Pearson Age <50">Z-score Age < 50</option>
                                <option value="Z-score_Pearson Age >59">Z-score Age > 59</option>
                                <option value="Z-score_Pearson Sex Female">Z-score Female Only</option>
                                <option value="Z-score_Pearson Sex Male">Z-score Male Only</option>
                                <option value="Z-score_Pearson Ischemia High">Z-score Ischemia High</option>
                                <option value="Z-score_Pearson Ischemia Low">Z-score Ischemia Low</option>   
                            </optgroup>
                        </select>
                        <br>
                        <input type="submit" id="listPreferencesSubmit">
                    </form>
                </div>
                
                <br>
                
                <div id="externalLinksDiv" style="display: none;">
                    <h3 class="title">External links for selected gene</h3>
                    <a id="genecardsLink" target="_blank"></a>
                    <a id="ncbiLink" target="_blank"></a>
                    <a id="ensemblLink" target="_blank"></a>
                    <a id="gtexLink" target="_blank"></a>
                </div>

            </div>

            <div id="networkTab" style="display: none;">
                
                <h2>Build a network heatmap of co-expression with a list of whitespace separated gene symbols and/or ensembl IDs</h2>
                
                <div id="networkFormsDiv" style="display: flex">
                    
                    <form id="checkNetworkForm">
                        <label for="networkGeneSet">Paste your genes in the box:</label>
                        <textarea id="networkGeneSet" rows="10"></textarea>
                        <button type="submit" id="checkNetworkGenes">Check existence in database</button>
                    </form>
                    <form id="buildNetworkForm" action="network.jsp" target="_blank" style="display: none;">
                        <input type="hidden" id="goodNetworkGenes" name="Gene set">
                        <input type="hidden" id="networkDbVersion" name="Version">
                        <label for="nforNetwork"># of genes found in database:</label>
                        <input type="text" id="nforNetwork" name="Genes" readonly>
                        <label for="networkDb">Database for network:</label>
                        <select name="Database" id="networkDb">
                            <option value="mins/G">Robust Minimum G Realization</option>
                            <option value="mins/pearson" selected>Robust Minimum Pearson Realization</option>
                            <option value="mins/spearman">Robust Minimum Spearman Realization</option>
                        </select>
                        <button type="submit" id="buildNetwork">Build network</button>
                    </form>
                    
                </div>
                
            </div>
            
            <div id="downloadsTab" style="display: none;">
                
                <h2>Downloads</h2>
                
                <ul>
                    <li>
                        <a href="http://victortrevino.bioinformatics.mx:8080/geneinfo.txt" download>geneinfo.txt:</a>
                        tab-separated file with general information for all genes in the database (same as shown in the "One gene" tab in this page).
                    </li>
                    <li>
                        <a href="http://victortrevino.bioinformatics.mx:8080/geneinfoHeaders.txt" download>geneinfoHeaders.txt:</a>
                        headers for the previous file.
                    </li>
                </ul>
                
                <h3>Bulk associations/co-expression</h3>
                <p>
                    Each of the following files contain a R object with all
                    associations calculated for all pairs of genes in the database.
                    These are useful for custom analysis across many genes. If you
                    only require associations between a single gene and others,
                    you may download this information directly from the page of the gene
                    that you desire without having to operate the raw database. 
                </p>
                <table style="border: 1px solid black;">
                    <tr>
                        <th>Version</th>
                        <th>Database</th>
                        <th>File size</th>
                        <th>Link</th>
                    </tr>
                    <tr>
                        <td>TPM</td>
                        <td>Pearson (minimum across 20)</td>
                        <td>662M</td>
                        <td><a href="http://victortrevino.bioinformatics.mx:8080/TPM_pearson_min.RData" download>TPM_pearson_min.RData</a></td>
                    </tr>
                    <tr>
                        <td>TPM</td>
                        <td>Spearman (minimum across 20)</td>
                        <td>635M</td>
                        <td><a href="http://victortrevino.bioinformatics.mx:8080/TPM_spearman_min.RData" download>TPM_spearman_min.RData</a></td>
                    </tr>
                    <tr>
                        <td>TPM</td>
                        <td>G-statistic (minimum across 20)</td>
                        <td>1.4G</td>
                        <td><a href="http://victortrevino.bioinformatics.mx:8080/TPM_G_min.RData" download>TPM_G_min.RData</a></td>
                    </tr>
                    <tr>
                        <td>Z-score</td>
                        <td>Pearson (minimum across 20)</td>
                        <td>369M</td>
                        <td><a href="http://victortrevino.bioinformatics.mx:8080/Z-score_pearson_min.RData" download>Z-score_pearson_min.RData</a></td>
                    </tr>
                    <tr>
                        <td>Z-score</td>
                        <td>Spearman (minimum across 20)</td>
                        <td>370M</td>
                        <td><a href="http://victortrevino.bioinformatics.mx:8080/Z-score_spearman_min.RData" download>Z-score_spearman_min.RData</a></td>
                    </tr>
                    <tr>
                        <td>Z-score</td>
                        <td>G-statistic (minimum across 20)</td>
                        <td>1.1G</td>
                        <td><a href="http://victortrevino.bioinformatics.mx:8080/Z-score_G_min.RData" download>Z-score_G_min.RData</a></td>
                    </tr>
                </table> 
                <p>
                    Databases are represented as the upper triangle of the gene
                    association/co-expression matrix "flattened" into a vector
                    in order to avoid saving redundant associations and the diagonal elements.
                    To further optimize space, the vectors are of integer type and only
                    save two positions of the decimal part of the estimates. We provide the following
                    <a href="http://victortrevino.bioinformatics.mx:8080/getIndexPairs.r" download>R function</a>
                    to programatically extract associations between gene <code>idx</code> and all <code>n</code> genes in the database:
                </p>
                <pre>
                    #' @param idx Index of gene in the database 
                    #' @param db CoGTEx raw associations/co-expression vector
                    #' @param n Number of genes in the database
                    #' @return Vector of associations/co-expression estimates between i and all genes in the database
                    getIndexPairs <- function(idx, db, n) {

                        pidx <- idx - 1
                        if (idx > 1) {

                            i <- j <- 1:n
                            i[idx:n] <- j[1:pidx] <- idx

                            k <- i - 1
                            pairIdxs <- (k * n) - ( (k * (k + 1)) / 2 ) + (j - i)
                            pairIdxs[idx] <- 0L

                        } else {

                            pairIdxs <- 1:(n - 1)

                        }

                        r <- db[pairIdxs]
                        return(append(r, 0L, after = pidx)) # own association is set to 0

                    }
                </pre>
                <p>
                    The following
                    <a href="http://victortrevino.bioinformatics.mx:8080/example.r" download>R script</a>
                    script is an example of usage:
                </p>
                <pre>
                    # load the downloaded data
                    source("getIndexPairs.r")
                    x <- setNames(
                        read.csv("geneinfo.txt", sep = "\t", header = FALSE, stringsAsFactors = FALSE),
                        readLines("geneinfoHeaders.txt")
                    )
                    n <- nrow(x)
                    print(load("TPM_pearson_min.RData"))
                    # m

                    # get the index in the database of genes of interest
                    query <- c("GAPDH", "TP53", "GH1")
                    cogtexIdxs <- match(query, x$'Gene Symbol')
                    estimates <- lapply(cogtexIdxs, getIndexPairs, m, n)
                    estimates <- lapply(estimates, '/', 100) # divide over 100 to turn integers to real values

                    # returned vector elements are always in the same order (the database order)
                    estimates <- setNames(lapply(estimates, setNames, x$'Gene Symbol'), query)
                    lapply(estimates, head, 5) # each vector in the list is of length n
                    # $GAPDH
                    #        WASH7P         OR4F5 RP11-34P13.15 RP11-34P13.16 RP11-34P13.14 
                    #         -0.18          0.32         -0.26         -0.31         -0.01 

                    # $TP53
                    #        WASH7P         OR4F5 RP11-34P13.15 RP11-34P13.16 RP11-34P13.14 
                    #          0.20         -0.30          0.29          0.35          0.20 

                    # $GH1
                    #        WASH7P         OR4F5 RP11-34P13.15 RP11-34P13.16 RP11-34P13.14 
                    #          0.03         -0.07          0.04          0.03          0.00
                </pre>
            </div>
            
        </div>
            
        <div id="logosDiv">
            <img src="resources/TecSalud-Bioinformatics-TecMty-Logo.png">
        </div>
        
    </body>
</html>
