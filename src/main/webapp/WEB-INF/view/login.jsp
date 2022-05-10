<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <jsp:include page="fragments/metadata.jsp"/>
    <title>Login</title>
</head>
<body>
<h2>Login</h2>
<jsp:include page="fragments/menu.jsp"/>
<br>
<c:if test="${param.error}">
    <p class="warning">Bad credentials.</p>
</c:if>
<c:if test="${param.logout}">
    <p class="warning">You have successfully logout.</p>
</c:if>
<form action="login" method="post">
    <table class="login">
        <tr>
            <td>User:</td>
            <td>
                <input type="text" placeholder="Email" name="username">
            </td>
        </tr>
        <tr>
            <td>Password:</td>
            <td>
                <input type="password" placeholder="Password" name="password">
            </td>
        </tr>
        <tr>
            <td>
                <button type="submit">submit</button>
            </td>
        </tr>
    </table>
</form>
</body>
</html>

