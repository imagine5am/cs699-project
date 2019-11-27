<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Welcome</title>
    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container">
    <div class="row">
        <form id="logoutForm" method="POST" action="${contextPath}/logout">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>

        <div class="col-md-8 col-xs-12">
            <h2>Hi, ${pageContext.request.userPrincipal.name}</h2>
        </div>
        <div class="col-md-4 col-xs-6">
            <h3 class="text-right"><a onclick="document.forms['logoutForm'].submit()">Logout</a></h3>
        </div>
    </div>
    <div>
        <c:if test="${organization != null}">
            <h3>API KEY:</h3>
            <pre> ${organization.apiKey} </pre>
        </c:if>
    </div>
</div>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script src="${contextPath}/resources/js/bootstrap.min.js"></script>
</body>
</html>