<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://restaurantvoting.redfox.com/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<html>
<head>
    <jsp:include page="fragments/metadata.jsp"/>
    <title><fmt:message key="user.title"/></title>
</head>
<body>
<section>
    <h2><fmt:message key="user.title"/></h2>
    <jsp:include page="fragments/menu.jsp"/>
    <br>
    <table>
        <thead>
        <tr>
            <th><fmt:message key="user.name"/></th>
            <th><fmt:message key="user.email"/></th>
            <th><fmt:message key="user.roles"/></th>
            <th><fmt:message key="user.active"/></th>
            <th><fmt:message key="user.registered"/></th>
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