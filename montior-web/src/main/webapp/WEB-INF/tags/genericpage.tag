<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag description="Overall Page template" pageEncoding="UTF-8" %>
<jsp:useBean id="paths" class="com.jay.montior.common.PathsMap" scope="application"/>
<%@attribute name="script" fragment="true" %>
<c:set var="root" value="${pageContext.request.contextPath}"/>
<c:set var="restRoot" value="${pageContext.request.contextPath}/${paths['Rest']}"/>

<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="${root}/img/favicon.png">
    <title>montior</title>
    <link href="${root}/css/main.css" rel="stylesheet">
    <link href="${root}/css/font-awesome.min.css" rel="stylesheet">
    <script src="${root}/js/angular.min.js"></script>
    <script src="${root}/js/angular-cookies.js"></script>
    <jsp:invoke fragment="script"/>
    <script>
        var root = "${root}";
        var rest = "${paths['Rest']}";
    </script>
</head>

<body ng-app="montior">

<div id="body">
    <jsp:doBody/>
</div>

</body>
</html>
