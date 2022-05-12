<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<table class="menu">
    <tr>
        <th><a href="restaurants"><fmt:message key="restaurant.title"/></a></th>
        <th><a href="users"><fmt:message key="user.title"/></a></th>
        <th><a href="swagger-ui/index.html">OpenAPI</a></th>
        <th><a href="login"><fmt:message key="app.login"/></a></th>
        <th><a href="logout"><fmt:message key="app.logout"/></a></th>
    </tr>
</table>