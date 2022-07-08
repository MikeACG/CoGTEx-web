<%-- 
    Document   : networkTest
    Created on : 27 sep 2021, 19:03:32
    Author     : INTEL
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <script type="text/javascript" src="clustergrammer-master/lib/js/d3.js"></script>
        <script type="text/javascript" src="clustergrammer-master/lib/js/jquery-1.11.2.min.js"></script>
        <script type="text/javascript" src="clustergrammer-master/lib/js/underscore-min.js"></script>
        <script type="text/javascript" src="clustergrammer-master/lib/js/bootstrap.min.js"></script>
        <script type="text/javascript" src='clustergrammer-master/clustergrammer.js'></script>
        <script type="text/javascript" src="networkTest.js"></script>
        <link rel="stylesheet" href="clustergrammer-master/lib/css/font-awesome.min.css">
        <link rel="stylesheet" href="clustergrammer-master/lib/css/bootstrap.css">
        <link rel="stylesheet" href="clustergrammer-master/css/custom.css">
    </head>
    <body>
        
        <input value="<%= request.getParameter("Gene set") %>" type="hidden" id="geneSet" name="Gene set">
        <div style="display: flex; align-items: center; justify-content: space-between">
            <h1 style="color: #1166AA;">G-CoExpress</h1>
            <h2 id="parameters">
            </h2>
        </div>
        
        <div id="container" style="width: 750px; height: 750px;"></div>
        <form id="genesForm">
            <label for="geneSet">Format genes from comma to newline separated:</label>
            <textarea id="genes" rows="20"></textarea>
            <button type="submit" id="formatGenes">Format</button>
        </form>
        
    </body>
</html>
