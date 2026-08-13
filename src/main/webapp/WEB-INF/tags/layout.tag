<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="pageTitle" required="true" type="java.lang.String" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %><!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title><fmt:message key="app.title" /> | ${pageTitle}</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css' />" />
</head>
<body>
    <div class="container">
        <!-- Menu Lateral -->
        <aside class="sidebar">
            <h1><fmt:message key="app.title" /></h1>
            <nav>
                <ul>
                    <li><a href="<c:url value='/app/list' />" class="nav-link"><fmt:message key="app.home" /></a></li>
                    <li><a href="<c:url value='/app/list' />" class="nav-link"><fmt:message key="app.manage" /></a></li>
                    <li><a href="<c:url value='/app/new' />" class="nav-link"><fmt:message key="app.add" /></a></li>
                </ul>
            </nav>
        </aside>

        <!-- Conteúdo Principal -->
        <main class="content">
            <c:if test="${not empty error}">
                <div class="alert alert-error">
                    <c:out value="${error}" />
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
