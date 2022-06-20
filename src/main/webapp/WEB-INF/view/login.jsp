<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<html>
<head>
    <jsp:include page="fragments/metadata.jsp"/>
    <title><spring:message code="app.title"/></title>
</head>
<body>
<jsp:include page="fragments/bodyHeader.jsp"/>
<div class="jumbotron py-0">
    <div class="container">
        <c:if test="${param.error}">
            <div class="error">${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}</div>
        </c:if>
        <c:if test="${param.logout}">
            <div class="message"><spring:message code="login.success"/></div>
        </c:if>
        <c:if test="${param.register}">
            <div class="message"><spring:message code="profile.registered"/></div>
        </c:if>
        <security:authorize access="isAnonymous()">
            <div class="pt-2">
                <a class="btn btn-lg btn-secondary mt-2" href="register">
                        <spring:message code="profile.register"/>&raquo;
                </a>
                <button type="submit" class="btn btn-lg btn-primary mt-2" onclick="login('user@yandex.ru', 'password')">
                    <spring:message code="app.login"/> User
                </button>
                <button type="submit" class="btn btn-lg btn-primary mt-2" onclick="login('admin@gmail.com', 'admin')">
                    <spring:message code="app.login"/> Admin
                </button>
            </div>
        </security:authorize>
        <div class="lead py-4">
            <h5><spring:message code="app.stackTitle"/></h5>
            <a href="https://spring.io/projects/spring-boot">Spring Boot</a>,
            <a href="http://projects.spring.io/spring-data-jpa">Spring Data JPA</a>,
            <a href="https://spring.io/projects/spring-data-rest">Spring Data REST</a>,
            <a href="http://projects.spring.io/spring-security">Spring Security</a>,
            <a href="https://spring.io/projects/spring-restdocs">Spring REST Docs</a>,
            <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/web.html">Spring Web MVC</a>,
            <a href="https://projectlombok.org">Lombok</a>,
            <a href="https://mapstruct.org">MapStruct</a>,
            <a href="http://hibernate.org/orm">Hibernate ORM</a>,
            <a href="http://hibernate.org/validator">Hibernate Validator</a>,
            <a href="https://github.com/FasterXML/jackson">Json Jackson</a>,
            <a href="https://www.h2database.com">H2</a>,
            <a href="http://www.postgresql.org">PostgreSQL</a>,
            <a href="https://github.com/ben-manes/caffeine/wiki">Caffeine</a>,
            <a href="https://www.liquibase.org">Liquibase</a>,
            <a href="http://www.slf4j.org">SLF4J</a>,
            <a href="https://junit.org/junit5">JUnit 5</a>,
            <a href="http://hamcrest.org/JavaHamcrest">Hamcrest</a>,
            <a href="https://assertj.github.io/doc">AssertJ</a>,
            <a href="http://ru.wikipedia.org/wiki/JSP">JSP</a>,
            <a href="http://en.wikipedia.org/wiki/JavaServer_Pages_Standard_Tag_Library">JSTL</a>,
            <a href="http://tomcat.apache.org">Apache Tomcat</a>,
            <a href="http://www.webjars.org">WebJars</a>,
            <a href="http://datatables.net">DataTables</a>,
            <a href="http://jquery.com">jQuery</a>,
            <a href="https://plugins.jquery.com">jQuery plugins</a>,
            <a href="http://getbootstrap.com">Bootstrap</a>.
        </div>
    </div>
</div>
<div class="container">
    <div class="lead"><spring:message code="app.description"/></div>
    <a class="btn btn-lg btn-success my-4" href="swagger-ui/index.html" target="_blank">
        <spring:message code="app.oas"/></a>
</div>
<br/>
<jsp:include page="fragments/footer.jsp"/>
<script type="text/javascript">
    <c:if test="${not empty param.username}">
    setCredentials("${param.username}", "");
    </c:if>

    function login(username, password) {
        setCredentials(username, password);
        $("#login_form").submit();
    }

    function setCredentials(username, password) {
        $('input[name="username"]').val(username);
        $('input[name="password"]').val(password);
    }
</script>
</body>
</html>