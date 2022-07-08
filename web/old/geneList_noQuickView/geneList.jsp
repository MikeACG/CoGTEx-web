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
        <script type="text/javascript" src="https://cdn.datatables.net/1.10.25/js/jquery.dataTables.min.js"></script>
        <script type="text/javascript" src="https://cdn.datatables.net/buttons/1.3.1/js/dataTables.buttons.min.js"></script>
        <script type="text/javascript" src="https://cdn.datatables.net/buttons/1.3.1/js/buttons.html5.min.js"></script>
        <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.1.3/jszip.min.js"></script>
        <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
        <script type="text/javascript" src="geneList.js"></script>
        <link rel="stylesheet" href="https://cdn.datatables.net/1.10.25/css/jquery.dataTables.min.css">
        <link rel="stylesheet" href="https://cdn.datatables.net/buttons/1.3.1/css/buttons.dataTables.min.css">
        <link rel="stylesheet" href="geneList.css">
        <title> <%= request.getParameter("Gene symbol") %> Gene List </title>
    </head>
    <body>
        
        <div style="display: flex; align-items: center; justify-content: space-between">
            <h1 style="color: #1166AA;">G-CoExpress</h1>
            <h2 id="parameters">
                <c:forEach var="parameter" items="${param}">
                    <span> ${parameter.key}: ${parameter.value} | </span>
                    <input type="hidden" id="${parameter.key}" value="${parameter.value}">
                </c:forEach>
            </h2>
        </div>
        
        <div id="listDiv">
            
            <label for="database">Database for figures:</label>
            <select  name="database" id="database">
              <optgroup label="Common">
                <option value="min_G">Robust Minimum G Realization</option>
                <option value="ref">Reference G Realization (1 of 20)</option>
                <option value="min_pearson" selected>Robust Minimum Pearson Realization</option>
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
            
            <table class="display" id="listTable"></table>
            
        </div>
        
    </body>
</html>
