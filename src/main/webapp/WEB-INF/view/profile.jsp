<%--@elvariable id="userTo" type="com.redfox.restaurantvoting.to.UserTo"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="restaurantvoting" tagdir="/WEB-INF/tags" %>
<html>
<head>
    <jsp:include page="fragments/metadata.jsp"/>
    <title><spring:message code="app.profile"/></title>
</head>
<body>
<jsp:include page="fragments/bodyHeader.jsp"/>
<div class="jumbotron pt-4">
    <div class="container">
        <div class="row">
            <div class="col-5 offset-3">
                <h3><spring:message code="profile.edit"/></h3>
                <form:form class="form-group" modelAttribute="userTo" method="post" action="profile"
                           charset="utf-8" accept-charset="UTF-8">

                    <form:hidden path="id"/>
                    <restaurantvoting:inputField labelCode="user.name" name="name"/>
                    <restaurantvoting:inputField labelCode="user.email" name="email"/>
                    <restaurantvoting:inputField labelCode="user.password" name="password" inputType="password"/>

                    <div class="text-right">
                        <a class="btn btn-secondary" href="#" onclick="window.history.back()">
                            <span class="fa fa-close"></span>
                            <spring:message code="common.cancel"/>
                        </a>
                        <button type="submit" class="btn btn-primary">
                            <span class="fa fa-check"></span>
                            <spring:message code="common.save"/>
                        </button>
                    </div>
                </form:form>
            </div>
        </div>
    </div>
</div>
<jsp:include page="fragments/footer.jsp"/>
</body>
</html>
