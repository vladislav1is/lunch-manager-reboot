<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title><spring:message code="vote.title"/>&nbsp${restaurantName}</title>
    <jsp:include page="fragments/metadata.jsp"/>
    <jsp:include page="fragments/datatables.jsp"/>
</head>
<body>
<script type="text/javascript" defer>
    const restaurantId = ${restaurantId};
</script>
<script type="text/javascript" src="resources/js/restaurantvoting.common.js" defer></script>
<script type="text/javascript" src="resources/js/restaurantvoting.restaurant-vote.js" defer></script>
<jsp:include page="fragments/bodyHeader.jsp"/>
<div class="jumbotron pt-4">
    <div class="container">
        <h3 class="text-center"><spring:message code="vote.title"/>&nbsp${restaurantName}</h3>
        <table class="table table-striped" id="datatable">
            <thead>
            <tr>
                <th><spring:message code="restaurant.name"/></th>
                <th><spring:message code="restaurant.menu"/></th>
                <th><spring:message code="restaurant.address"/></th>
                <th><spring:message code="restaurant.visitors"/></th>
            </tr>
            </thead>
        </table>
        <div class="pt-2">
            <button class="btn btn-primary mr-1" type="submit" onclick="vote()">
                <span class="fa fa-plus"></span>
                <spring:message code="common.select"/>
            </button>
            <button class="btn btn-danger custom-btn mr-1" type="button" onclick="deleteVote()">
                <span class="fa fa-remove"></span>
                <spring:message code="common.cancel"/>
            </button>
            <div>
            </div>
        </div>
        <jsp:include page="fragments/footer.jsp"/>
</body>
<jsp:include page="fragments/i18n.jsp">
    <jsp:param name="page" value="restaurant"/>
</jsp:include>
</html>
