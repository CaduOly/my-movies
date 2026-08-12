<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:import url="layout.jsp">
    <jsp:param name="pageTitle" value="Resultados da Busca" />
    
    <h2>Resultados da Busca</h2>
    <p><fmt:message key="app.search" />: <c:out value="${searchTerm}" /></p>

    <c:choose>
        <c:when test="${empty items}">
            <p>Sem resultados.</p>
        </c:when>
        <c:otherwise>
            <p>Encontrados <c:out value="${items.size()}" /> resultado(s).</p>
            <jsp:include page="list.jsp" />
        </c:otherwise>
    </c:choose>

    <a href="<c:url value='/app/list' />" class="btn btn-secondary">
        <fmt:message key="app.back" />
    </a>
</c:import>
