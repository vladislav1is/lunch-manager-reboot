<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<html>
<head>
    <jsp:include page="fragments/metadata.jsp"/>
    <title><fmt:message key="restaurant.title"/></title>
</head>
<body>
<section>
    <h2><fmt:message key="restaurant.title"/></h2>
    <jsp:include page="fragments/menu.jsp"/>
    <br>
    <table>
        <thead>
        <tr>
            <th><fmt:message key="restaurant.name"/></th>
            <th><fmt:message key="restaurant.address"/></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="restaurant" items="${restaurants}" varStatus="status">
            <c:set var="id" value="${restaurant.id}"/>
            <tr>
                <td>${restaurant.name}</td>
                <td>${restaurant.address}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</section>
</body>
</html>