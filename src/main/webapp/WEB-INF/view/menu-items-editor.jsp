<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title><spring:message code="menu-item.editor"/>&nbsp${restaurantName}</title>
    <jsp:include page="fragments/metadata.jsp"/>
    <jsp:include page="fragments/datatables.jsp"/>
    <link rel="stylesheet" href="webjars/datetimepicker/2.5.20-1/jquery.datetimepicker.css">
    <script src="webjars/datetimepicker/2.5.20-1/build/jquery.datetimepicker.full.min.js" defer></script>
</head>
<body>
<script type="text/javascript" defer>
    const restaurantId = ${restaurantId};
</script>
<script src="resources/js/restaurantvoting.common.js" defer></script>
<script src="resources/js/restaurantvoting.menu-items-editor.js" defer></script>
<jsp:include page="fragments/bodyHeader.jsp"/>
<div class="jumbotron pt-4">
    <div class="container">
        <h3 class="text-center"><spring:message code="menu-item.title"/>&nbsp${restaurantName}</h3>
        <%-- https://getbootstrap.com/docs/4.0/components/card/ --%>
        <div class="card border-dark">
            <div class="card-body pb-0">
                <form id="filter">
                    <div class="row">
                        <div class="col-3">
                            <label for="startDate"><spring:message code="menu-item.startDate"/></label>
                            <input type="text" class="form-control" name="startDate" id="startDate" autocomplete="off">
                        </div>
                        <div class="col-3">
                            <label for="endDate"><spring:message code="menu-item.endDate"/></label>
                            <input type="text" class="form-control" name="endDate" id="endDate" autocomplete="off">
                        </div>
                    </div>
                </form>
            </div>
            <div class="card-footer text-right">
                <button class="btn btn-danger custom-btn" onclick="clearFilter()">
                    <span class="fa fa-remove"></span>
                    <spring:message code="common.cancel"/>
                </button>
                <button class="btn btn-primary" onclick="ctx.updateTable()">
                    <span class="fa fa-filter"></span>
                    <spring:message code="menu-item.filter"/>
                </button>
            </div>
        </div>
        <br/>
        <button class="btn btn-primary" onclick="add()">
            <span class="fa fa-plus"></span>
            <spring:message code="menu-item.add"/>
        </button>
        <table class="table table-striped" id="datatable">
            <thead>
            <tr>
                <th><spring:message code="menu-item.actualDate"/></th>
                <th><spring:message code="menu-item.name"/></th>
                <th><spring:message code="menu-item.active"/></th>
                <th><spring:message code="menu-item.price"/></th>
                <th></th>
                <th></th>
            </tr>
            </thead>
        </table>
    </div>
</div>

<div class="modal fade" tabindex="-1" id="editRow">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="modalTitle"></h4>
                <button type="button" class="close" data-dismiss="modal" onclick="closeNoty()">&times;</button>
            </div>
            <div class="modal-body">
                <form id="detailsForm">
                    <input type="hidden" id="id" name="id">
                    <div class="form-group">
                        <label for="actualDate" class="col-form-label"><spring:message
                                code="menu-item.actualDate"/></label>
                        <input type="text" class="form-control" id="actualDate" name="actualDate" autocomplete="off"
                               placeholder="<spring:message code="menu-item.actualDate.format"/>" required>
                    </div>
                    <div class="form-group">
                        <label for="name" class="col-form-label"><spring:message
                                code="menu-item.name"/></label>
                        <input type="text" class="form-control" id="name" name="name"
                               placeholder="<spring:message code="menu-item.name"/>" required>
                    </div>
                    <div class="form-group">
                        <label for="price" class="col-form-label"><spring:message code="menu-item.price"/></label>
                        <input type="number" class="form-control" id="price" name="price" placeholder="0" required>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal" onclick="closeNoty()">
                    <span class="fa fa-close"></span>
                    <spring:message code="common.cancel"/>
                </button>
                <button type="button" class="btn btn-primary" onclick="saveMenuItem()">
                    <span class="fa fa-check"></span>
                    <spring:message code="common.save"/>
                </button>
            </div>
        </div>
    </div>
</div>
<jsp:include page="fragments/footer.jsp"/>
</body>
<jsp:include page="fragments/i18n.jsp">
    <jsp:param name="page" value="menu-item"/>
</jsp:include>
</html>
