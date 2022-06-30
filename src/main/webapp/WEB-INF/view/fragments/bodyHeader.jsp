<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<header>
    <nav class="navbar navbar-expand-md navbar-dark bg-dark py-0">
        <div class="container">
            <a href="restaurants" title="<spring:message code="app.title"/>" class="navbar-brand">
                <img src="resources/images/icon-fox.png" width="50" alt="<spring:message code="app.title"/>">
                <spring:message code="app.title"/>
            </a>
            <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav"
                    aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ml-auto">
                    <li class="nav-item">
                        <security:authorize access="isAuthenticated()">
                            <form:form class="form-inline my-2" action="logout" method="post">
                                <div class="btn-group my-2">
                                    <button type="button" onclick="location.href='login';" class="btn btn-secondary">
                                        <spring:message code="app.main"/>
                                    </button>
                                    <button type="button" onclick="location.href='restaurants';" class="btn btn-secondary">
                                        <spring:message code="restaurant.title"/>
                                    </button>
                                    <security:authorize access="hasAnyRole('ADMIN', 'R_ADMIN')">
                                        <button type="button" onclick="location.href='restaurants/editor';"
                                                class="btn btn-secondary">
                                            <spring:message code="app.editor"/>
                                        </button>
                                    </security:authorize>
                                    <security:authorize access="hasRole('ADMIN')">
                                    <button type="button" onclick="location.href='users';" class="btn btn-secondary">
                                            <spring:message code="user.title"/>
                                        </button>
                                    </security:authorize>
                                    <button type="button" onclick="location.href='profile';" class="btn btn-secondary">
                                        <security:authentication property="principal.user.name"/>
                                    </button>
                                    <button class="btn btn-danger custom-btn" type="submit">
                                        <span class="fa fa-sign-out"></span>
                                    </button>
                                </div>
                            </form:form>
                        </security:authorize>
                        <security:authorize access="isAnonymous()">
                            <form:form class="form-inline my-2" id="login_form" action="login" method="post">
                                <div class="input-group my-2">
                                    <label>
                                        <input class="form-control mr-1 login width" type="text"
                                               placeholder="Email" name="username">
                                    </label>
                                    <label>
                                        <input class="form-control mr-1 login width" type="password"
                                               placeholder="Password" name="password">
                                    </label>
                                    <button class="btn btn-danger custom-btn" type="submit">
                                        <span class="fa fa-sign-in"></span>
                                    </button>
                                </div>
                            </form:form>
                        </security:authorize>
                    </li>
                    <li class="nav-item dropdown">
                        <a class="dropdown-toggle nav-link my-3 ml-2"
                           data-toggle="dropdown">${pageContext.response.locale}</a>
                        <div class="dropdown-menu">
                            <a class="dropdown-item"
                               href="${requestScope['javax.servlet.forward.request_uri']}?lang=en">English</a>
                            <a class="dropdown-item"
                               href="${requestScope['javax.servlet.forward.request_uri']}?lang=ru">Русский</a>
                        </div>
                    </li>
                </ul>
            </div>
        </div>
    </nav>
    <script type="text/javascript">
        let localeCode = "${pageContext.response.locale}";
    </script>
</header>