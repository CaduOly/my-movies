<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout pageTitleKey="app.library">
    <h2><fmt:message key="app.list" /></h2>

    <!-- Busca -->
    <jsp:include page="_search-bar.jsp" />

    <!-- Tabela de Itens -->
    <jsp:include page="_items-table.jsp" />
</t:layout>
