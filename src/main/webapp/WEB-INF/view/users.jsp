<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://restaurantvoting.redfox.com/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <jsp:include page="fragments/metadata.jsp"/>
    <title><spring:message code="user.title"/></title>
    <link rel="stylesheet" href="webjars/datatables/1.10.25/css/dataTables.bootstrap4.min.css">
    <script src="webjars/datatables/1.10.25/js/jquery.dataTables.min.js" defer></script>
    <script src="webjars/datatables/1.10.25/js/dataTables.bootstrap4.min.js" defer></script>
</head>
<body>
<script src="resources/js/restaurantvoting.common.js" defer></script>
<script src="resources/js/restaurantvoting.users.js" defer></script>
<jsp:include page="fragments/bodyHeader.jsp"/>
<div class="jumbotron pt-4">
    <div class="container">
        <h3 class="text-center"><spring:message code="user.title"/></h3>
        <button class="btn btn-primary" onclick="add()">
            <span class="fa fa-plus"></span>
            <spring:message code="user.add"/>
        </button>
        <table class="table table-striped mt-3" id="datatable">
            <thead>
            <tr>
                <th><spring:message code="user.name"/></th>
                <th><spring:message code="user.email"/></th>
                <th><spring:message code="user.roles"/></th>
                <th><spring:message code="user.active"/></th>
                <th><spring:message code="user.registered"/></th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${users}" var="user">
                <tr>
                    <td><c:out value="${user.name}"/></td>
                    <td><a href="mailto:${user.email}">${user.email}</a></td>
                    <td>${user.roles}</td>
                    <td><input type="checkbox" <c:if test="${user.enabled}">checked</c:if> id="${user.id}"/></td>
                    <td>${fn:format(user.registered)}</td>
                    <td><a class="delete" id="${user.id}"><span class="fa text-dark fa-remove"></span></a></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>
<div class="modal fade" tabindex="-1" id="editRow">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title"><spring:message code="user.add"/></h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <form id="detailsForm">
                    <input type="hidden" id="id" name="id">
                    <div class="form-group">
                        <label for="name" class="col-form-label"><spring:message code="user.name"/></label>
                        <input type="text" class="form-control" id="name" name="name"
                               placeholder="<spring:message code="user.name"/>">
                    </div>
                    <div class="form-group">
                        <label for="email" class="col-form-label"><spring:message code="user.email"/></label>
                        <input type="email" class="form-control" id="email" name="email"
                               placeholder="<spring:message code="user.email"/>">
                    </div>
                    <div class="form-group">
                        <label for="password" class="col-form-label"><spring:message code="user.password"/></label>
                        <input type="password" class="form-control" id="password" name="password"
                               placeholder="<spring:message code="user.password"/>">
                    </div>
                    <div class="form-group">
                        <label for="role"><spring:message code="user.role"/></label>
                        <select class="form-control" id="role" name="role">
                            <option value="USER" selected>USER</option>
                            <option value="R_ADMIN">R_ADMIN</option>
                            <option value="ADMIN">ADMIN</option>
                        </select>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <span class="fa fa-close"></span>
                    <spring:message code="common.cancel"/>
                </button>
                <button type="button" class="btn btn-primary" onclick="save()">
                    <span class="fa fa-check"></span>
                    <spring:message code="common.save"/>
                </button>
            </div>
        </div>
    </div>
</div>
<jsp:include page="fragments/footer.jsp"/>
</body>
</html>