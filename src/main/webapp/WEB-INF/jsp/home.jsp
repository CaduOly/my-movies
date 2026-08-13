<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout pageTitle="Início">
    
    

    <!-- Busca -->
    <jsp:include page="_search-bar.jsp" />

    <!-- Grid de Catálogo -->
    <c:choose>
        <c:when test="${not empty searchTerm}">
            <h2>Resultados para: <c:out value="${searchTerm}" /></h2>
        </c:when>
        <c:otherwise>
            <h2>Catálogo de Filmes e Séries</h2>
        </c:otherwise>
    </c:choose>
    
    <div class="media-grid" style="display:grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap:20px; margin-top:20px;">
        <c:forEach var="item" items="${items}">
            <a href="<c:url value='/app/detail?id=${item.id}' />" class="media-card" style="display:block; text-decoration:none; color:inherit; background:var(--bg-secondary); border-radius:8px; overflow:hidden; box-shadow:0 4px 6px -1px rgba(0,0,0,0.1); transition:transform 0.2s;">
                <div class="media-poster" style="height:300px; background:#E2E8F0; display:flex; align-items:center; justify-content:center;">
                    <c:choose>
                        <c:when test="${not empty item.posterUrl}">
                            <img src="<c:out value='${item.posterUrl}'/>" alt="<c:out value='${item.title}'/>" style="width:100%; height:100%; object-fit:cover;" />
                        </c:when>
                        <c:otherwise>
                            <span style="color:#64748B;">Sem Capa</span>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="media-info" style="padding:15px;">
                    <h3 style="margin:0 0 5px 0; font-size:1.1rem; color:var(--text-h1);"><c:out value="${item.title}" /></h3>
                    <p style="margin:0; font-size:0.9rem; color:var(--text-muted);">${item.releaseYear} &bull; <fmt:message key="type.${item.mediaType.toString().toLowerCase()}" /></p>
                </div>
            </a>
        </c:forEach>
    </div>

    <!-- Fim -->
    <style>
        .media-card:hover { transform: translateY(-5px); box-shadow: 0 10px 15px -3px rgba(0,0,0,0.1); }
    </style>
</t:layout>
