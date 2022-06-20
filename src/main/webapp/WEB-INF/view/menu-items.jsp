<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title><spring:message code="menu-item.title"/>&nbsp${restaurantName}</title>
    <jsp:include page="fragments/metadata.jsp"/>
    <jsp:include page="fragments/datatables.jsp"/>
</head>
<body>
<script type="text/javascript" defer>
    const restaurantId = ${restaurantId};
</script>
<script src="resources/js/restaurantvoting.common.js" defer></script>
<script src="resources/js/restaurantvoting.menu-items.js" defer></script>
<jsp:include page="fragments/bodyHeader.jsp"/>
<div class="jumbotron pt-4">
    <div class="container">
        <h3 class="text-center"><spring:message code="menu-item.title"/>&nbsp${restaurantName}</h3>
        <table class="table table-striped" id="datatable">
            <thead>
            <tr>
                <th><spring:message code="menu-item.name"/></th>
                <th><spring:message code="menu-item.price"/></th>
                <th><spring:message code="menu-item.actualDate"/></th>
            </tr>
            </thead>
        </table>
    </div>
</div>
<jsp:include page="fragments/footer.jsp"/>
</body>
<jsp:include page="fragments/i18n.jsp">
    <jsp:param name="page" value="menu-item"/>
</jsp:include>
</html>
