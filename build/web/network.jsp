<%-- 
    Document   : network
    Created on : 27 sep 2021, 19:03:32
    Author     : INTEL
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Network</title>
        <script type="text/javascript" src="clustergrammer-master/lib/js/d3.js"></script>
        <script type="text/javascript" src="clustergrammer-master/lib/js/jquery-1.11.2.min.js"></script>
        <script type="text/javascript" src="clustergrammer-master/lib/js/underscore-min.js"></script>
        <script type="text/javascript" src="clustergrammer-master/lib/js/bootstrap.min.js"></script>
        <script type="text/javascript" src='clustergrammer-master/clustergrammer.js'></script>
        <script type="text/javascript" src="network.js"></script>
        <link rel="stylesheet" href="clustergrammer-master/lib/css/font-awesome.min.css">
        <link rel="stylesheet" href="clustergrammer-master/lib/css/bootstrap.css">
        <link rel="stylesheet" href="clustergrammer-master/css/custom.css">
        <link rel="stylesheet" href="network.css">
    </head>
    
    <body>
        
        <div style="display: flex; align-items: center; justify-content: space-between">
            <h1 style="color: #1166AA;">CoGTEx</h1>
            <h2 id="parameters">
                <span> Co-expression network heatmap | </span>
                <c:forEach var="parameter" items="${param}">
                    <c:if test="${parameter.key != 'Gene set'}">
                        <span> ${parameter.key}: ${parameter.value} | </span>
                    </c:if>
                    <input type="hidden" id="${parameter.key}" value="${parameter.value}">
                </c:forEach>
            </h2>
        </div>
        
        <div id="heatmapDiv" style="width: 750px; height: 750px;"></div>
        
    </body>
</html>
