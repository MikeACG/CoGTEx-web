<%-- 
    Document   : plotComparison
    Created on : 20 dic 2021, 0:11:16
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
        <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
        <script type="text/javascript" src="plotComparison.js"></script>
        <link rel="stylesheet" href="http://code.jquery.com/ui/1.13.0/themes/redmond/jquery-ui.css"> 
        <link rel="stylesheet" href="plotComparison.css">
        <title> <%= request.getParameter("xEnsembl") %> Scatter Comparison </title>
    </head>
    <body>
        
        <h1 style="color: #1166AA;">CoGTEx</h1>
        <div id="parameters" style="display: none;">
            <c:forEach var="parameter" items="${param}">
                <input id="${parameter.key}" value="${parameter.value}">
            </c:forEach>
        </div>
        
        <div id="plotComparisonDiv" style="display: flex;"></div>
        
    </body>
</html>
