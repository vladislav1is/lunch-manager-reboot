<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<html>
<head>
    <jsp:include page="fragments/metadata.jsp"/>
    <title><fmt:message key="login.title"/></title>
</head>
<body>
<h2><fmt:message key="login.title"/></h2>
<jsp:include page="fragments/menu.jsp"/>
<br>
<c:if test="${param.error}">
    <p class="warning"><fmt:message key="login.error"/></p>
</c:if>
<c:if test="${param.logout}">
    <p class="warning"><fmt:message key="login.success"/></p>
</c:if>
<form action="login" method="post">
    <table class="login">
        <tr>
            <td>User:</td>
            <td>
                <input type="text" placeholder="<fmt:message key="login.email"/>" name="username">
            </td>
        </tr>
        <tr>
            <td>Password:</td>
            <td>
                <input type="password" placeholder="<fmt:message key="login.password"/>" name="password">
            </td>
        </tr>
        <tr>
            <td>
                <button type="submit"><fmt:message key="app.submit"/></button>
            </td>
        </tr>
    </table>
</form>
</body>
</html>

