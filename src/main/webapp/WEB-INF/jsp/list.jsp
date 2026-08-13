<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout pageTitle="Biblioteca">
    <h2><fmt:message key="app.list" /></h2>

    <!-- Busca -->
    <form method="GET" action="<c:url value='/app/search' />" class="search-form">
        <input type="text" name="term" placeholder="<fmt:message key='app.search.placeholder' />" />
        <button type="submit"><fmt:message key="app.search" /></button>
    </form>

    <!-- Tabela de Itens -->
    <jsp:include page="_items-table.jsp" />
</t:layout>
