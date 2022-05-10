<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://restaurantvoting.redfox.com/functions" %>
<html>
<head>
    <jsp:include page="fragments/metadata.jsp"/>
    <title>Users</title>
</head>
<body>
<section>
    <h2>Users</h2>
    <jsp:include page="fragments/menu.jsp"/>
    <br>
    <table>
        <thead>
        <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Roles</th>
            <th>Active</th>
            <th>Registered</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="user" items="${users}" varStatus="status">
            <c:set var="id" value="${user.id}"/>
            <tr>
                <td>${user.name}</td>
                <td>${user.email}</td>
                <td>${user.roles}</td>
                <td>${user.enabled}</td>
                <td>${fn:format(user.registered)}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</section>
</body>
</html>