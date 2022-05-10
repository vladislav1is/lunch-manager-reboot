<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <jsp:include page="fragments/metadata.jsp"/>
    <title>Restaurants</title>
</head>
<body>
<section>
    <h2>Restaurants</h2>
    <jsp:include page="fragments/menu.jsp"/>
    <br>
    <table>
        <thead>
        <tr>
            <th>Name</th>
            <th>Address</th>
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