<%-- 
    Document   : geneList
    Created on : 11 ago 2021, 15:26:43
    Author     : INTEL
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
        <script type="text/javascript" src="https://code.jquery.com/ui/1.13.0/jquery-ui.min.js"></script>
        <script type="text/javascript" src="https://cdn.datatables.net/1.10.25/js/jquery.dataTables.min.js"></script>
        <script type="text/javascript" src="https://cdn.datatables.net/buttons/1.3.1/js/dataTables.buttons.min.js"></script>
        <script type="text/javascript" src="https://cdn.datatables.net/buttons/1.3.1/js/buttons.html5.min.js"></script>
        <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.1.3/jszip.min.js"></script>
        <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
        <script type="text/javascript" src="geneList.js"></script>
        <link rel="stylesheet" href="http://code.jquery.com/ui/1.13.0/themes/redmond/jquery-ui.css"> 
        <link rel="stylesheet" href="https://cdn.datatables.net/1.10.25/css/jquery.dataTables.min.css">
        <link rel="stylesheet" href="https://cdn.datatables.net/buttons/1.3.1/css/buttons.dataTables.min.css">
        <link rel="stylesheet" href="geneList.css">
        <title> <%= request.getParameter("Gene symbol") %> Gene List </title>
    </head>
    <body>
        
        <div style="display: flex; align-items: center; justify-content: space-between">
            <h1 style="color: #1166AA;">CoGTEx</h1>
            <h2 id="parameters">
                <c:forEach var="parameter" items="${param}">
                    <span> ${parameter.key}: ${parameter.value} | </span>
                    <input type="hidden" id="${parameter.key}" value="${parameter.value}">
                </c:forEach>
            </h2>
        </div>
        
        <div id="listDiv">
            
            <div id="plotPreferencesDiv" style="display: none;">
                <h3 class="title">Table plots preferences</h3>
                <form id="plotPreferencesForm">
                    <label for="plotDatabase">Database for figures:</label>
                    <select id="plotDatabase">
                      <optgroup label="Common">
                        <option value="min_G">Robust Minimum G Realization</option>
                        <option value="ref">Reference G Realization (1 of 20)</option>
                        <option value="min_pearson">Robust Minimum Pearson Realization</option>
                        <option value="min_spearman">Robust Minimum Spearman Realization</option>
                      </optgroup>
                      <optgroup label="Covariates">
                        <option value="SEX_female">Female Only</option>
                        <option value="SEX_male">Male Only</option>
                        <option value="AGE_less50">Age < 50</option>
                        <option value="AGE_50-59">Age 50-59</option>
                        <option value="AGE_more59">Age > 59</option>
                        <option value="SMTSISCH_High">Ischemia High</option>
                        <option value="SMTSISCH_Low">Ischemia Low</option>    
                      </optgroup>
                      <optgroup label="Individual Realizations (1..20)">
                        <option value="1">Realization 1</option>
                        <option value="2">Realization 2</option>
                        <option value="3">Realization 3</option>
                        <option value="4">Realization 4</option>
                        <option value="5">Realization 5</option>
                        <option value="6">Realization 6</option>
                        <option value="7">Realization 7</option>
                        <option value="8">Realization 8</option>
                        <option value="9">Realization 9</option>
                        <option value="10">Realization 10</option>
                        <option value="11">Realization 11</option>
                        <option value="12">Realization 12</option>
                        <option value="13">Realization 13</option>
                        <option value="14">Realization 14</option>
                        <option value="15">Realization 15</option>
                        <option value="16">Realization 16</option>
                        <option value="17">Realization 17</option>
                        <option value="18">Realization 18</option>
                        <option value="19">Realization 19</option>
                        <option value="20">Realization 20</option>
                      </optgroup>
                    </select>
                    <label for="plotVersion">Data version:</label>
                    <select id="plotVersion">
                        <option value="Base">Base expression</option>
                        <option value="Z-score">Z-score by cluster expression</option>
                    </select>
                </form>
            </div>
            
            <table class="display" id="listTable"></table>
            
            <div id="listPreferencesDiv" style="display: none;">
                <h3 class="title">Associations list preferences for selected gene</h3>
                <form id="listPreferencesForm" action="geneList.jsp" method="get" target="_blank">
                    <input type="hidden" id="listGeneSymbol" name="Gene symbol">
                    <input type="hidden" id="listGeneEnsembl" name="Gene ensembl">
                    <input type="hidden" id="listVersion" name="Version">
                    <label for="listSize">Number of top associations to show:</label>
                    <select  name="List size" id="listSize">
                        <option value="100">100</option>
                        <option value="200">200</option>
                        <option value="300" >300</option>
                        <option value="500">500</option>
                        <option value="1000">1000</option>
                        <option value="2500">2500</option>
                        <option value="5000">5000</option>
                        <option value="10000">10000</option>
                        <option value="20000">20000</option>
                        <option value="max">All (may take a while depending on your computer specs)</option>
                    </select>
                    <br>
                    <label for="listDb">Database to order top associations:</label>
                    <select name="Order database" id="listDb">
                        <optgroup label="Common">
                          <option value="Min G">Robust Minimum G Realization</option>
                          <option value="Min Pearson">Robust Minimum Pearson Realization</option>
                          <option value="Min Spearman">Robust Minimum Spearman Realization</option>
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
            
        </div>
        
        <br>
        
        <div id="quickViewDiv" style="display: none;">
            <h2 class="title">Quick View</h2>
            <div id="quickViewRadiosDiv"></div>
            <div id="quickViewDashsDiv"></div>
            <form id="plotComparisonForm" style="display: none;" action="plotComparison.jsp" method="get" target="_blank">
                <input type="hidden" id="xgeneEnsembl" name="xEnsembl">
                <input type="hidden" id="y0geneEnsembl" name="y0Ensembl">
                <input type="hidden" id="y1geneEnsembl" name="y1Ensembl">
                <input type="hidden" id="xgeneSymbol" name="xSymbol">
                <input type="hidden" id="y0geneSymbol" name="y0Symbol">
                <input type="hidden" id="y1geneSymbol" name="y1Symbol">
                <input type="hidden" id="plot0Version" name="plot0V">
                <input type="hidden" id="plot1Version" name="plot1V">
                <input type="hidden" id="plot0Database" name="plot0Db">
                <input type="hidden" id="plot1Database" name="plot1Db">
            </form>
        </div>
        
        <div id="logosDiv">
            <img src="resources/TecSalud-Bioinformatics-TecMty-Logo.png">
        </div>
        
    </body>
</html>
