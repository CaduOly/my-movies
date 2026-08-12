<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:import url="layout.jsp">
    <jsp:param name="pageTitle" value="${item.title}" />
    
    <div class="detail-header">
        <h2><c:out value="${item.title}" /></h2>
        <a href="<c:url value='/app/edit?id=${item.id}' />" class="btn btn-secondary">
            <fmt:message key="app.edit" />
        </a>
    </div>

    <div class="detail-content">
        <c:if test="${not empty item.posterUrl}">
            <div class="poster">
                <img src="<c:out value='${item.posterUrl}' />" alt="<c:out value='${item.title}' />" />
            </div>
        </c:if>

        <dl>
            <dt><fmt:message key="item.authorDirector" /></dt>
            <dd><c:out value="${item.authorDirector}" /></dd>

            <dt><fmt:message key="item.releaseYear" /></dt>
            <dd><c:out value="${item.releaseYear}" /></dd>

            <dt><fmt:message key="item.genre" /></dt>
            <dd><c:out value="${item.genre}" /></dd>

            <dt><fmt:message key="item.mediaType" /></dt>
            <dd><fmt:message key="type.${item.mediaType.toString().toLowerCase()}" /></dd>

            <dt><fmt:message key="item.synopsis" /></dt>
            <dd><c:out value="${item.synopsis}" /></dd>
        </dl>
    </div>

    <div class="detail-actions">
        <a href="<c:url value='/app/list' />" class="btn btn-secondary">
            <fmt:message key="app.back" />
        </a>
    </div>
</c:import>
