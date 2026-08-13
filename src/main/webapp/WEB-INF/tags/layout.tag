<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="pageTitle" required="true" type="java.lang.String" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:if test="${not empty param.lang}">
    <fmt:setLocale value="${param.lang}" scope="session" />
</c:if>

<!DOCTYPE html>
<html lang="${not empty sessionScope['javax.servlet.jsp.jstl.fmt.locale.session'] ? sessionScope['javax.servlet.jsp.jstl.fmt.locale.session'] : 'pt-BR'}">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title><fmt:message key="app.title" /> | <c:out value="${pageTitle}" /></title>
    <link rel="stylesheet" href="<c:url value='/css/style.css' />" />
</head>
<body>
    <div class="container">
        <!-- Menu Lateral -->
        <aside class="sidebar" style="display: flex; flex-direction: column;">
            <h1><fmt:message key="app.title" /></h1>
            <nav>
                <ul>
                    <li><a href="<c:url value='/app/home' />" class="nav-link"><fmt:message key="app.home" /></a></li>
                    <li><a href="<c:url value='/app/list' />" class="nav-link"><fmt:message key="app.manage" /></a></li>
                    <li><a href="<c:url value='/app/new' />" class="nav-link"><fmt:message key="app.add" /></a></li>
                    <li><a href="<c:url value='/app/about' />" class="nav-link"><fmt:message key="app.about" /></a></li>
                </ul>
            </nav>
            <div style="margin-top: auto; padding: 20px 0; text-align: center;">
                <a href="?lang=pt_BR" class="action-link" style="font-weight:bold;">PT</a> | 
                <a href="?lang=en" class="action-link" style="font-weight:bold;">EN</a>
            </div>
        </aside>

        <!-- Conteúdo Principal -->
        <main class="content">
            <c:if test="${not empty error}">
                <div class="alert alert-error">
                    <c:out value="${error}" />
                </div>
            </c:if>

            <c:if test="${not empty errorKey}">
                <div class="alert alert-error">
                    <fmt:message key="${errorKey}" />
                </div>
            </c:if>

            <c:if test="${not empty success}">
                <div class="alert alert-success">
                    <c:out value="${success}" />
                </div>
            </c:if>

            <!-- Conteúdo específico da página -->
            <jsp:doBody />
        </main>
    </div>
</body>
</html>
