<%-- 
    Document   : plotGenes
    Created on : 11 jul 2021, 21:34:25
    Author     : INTEL
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
        <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
        <script type="text/javascript" src="scatterTest.js"></script>
        <link rel="stylesheet" href="scatterTest.css">
        <title>JSP Page</title>
    </head>
    
    <body>
        <form>
            <label for="database">Database for figures:</label>
            <select  name="database" id="database">
              <optgroup label="Common">
                <option value="min_G" selected>Robust Minimum G Realization</option>
                <option value="ref">Reference Realization (1 of 20)</option>
                <option value="min_pearson" >Robust Minimum Pearson Realization</option>
                <option value="min_spearman">Robust Minimum Spearman Realization</option>
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
            <br>
            <input type="text" id="xgene" name="xgene" value="ATP4A">
            <br>
            <input type="text" id="ygene" name="ygene" value="ATP4B">
            <br>
            <button type="submit" id="submit">Submit</button>
        </form>
        
        <div id="dashboard" class="dashboardDiv">
            <div id="legend_div" class="legendDiv"></div>
            <div id="scatter_div" class="scatterDiv"></div>
            <div id="contingency_div" class="contingencyDiv"></div>
        </div>

    </body>
    
</html>
