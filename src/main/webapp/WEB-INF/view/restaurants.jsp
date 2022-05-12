<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <title><spring:message code="restaurant.title"/></title>
    <jsp:include page="fragments/metadata.jsp"/>
</head>
<body>
<jsp:include page="fragments/bodyHeader.jsp"/>
<div class="jumbotron pt-4">
    <div class="container">
        <h3 class="text-center"><spring:message code="restaurant.title"/></h3>
        <table class="table table-striped mt-3">
            <thead>
            <tr>
                <th><spring:message code="restaurant.name"/></th>
                <th><spring:message code="restaurant.address"/></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="restaurant" items="${restaurants}">
                <tr>
                    <td>${restaurant.name}</td>
                    <td>${restaurant.address}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>
<jsp:include page="fragments/footer.jsp"/>
</body>
</html>