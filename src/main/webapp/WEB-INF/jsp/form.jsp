<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:import url="layout.jsp">
    <jsp:param name="pageTitle" value="${isEdit ? 'Editar Mídia' : 'Novo Item'}" />
    
    <h2><c:out value="${isEdit ? 'Editar Mídia' : 'Novo Item'}" /></h2>

    <form method="POST" action="<c:url value='${isEdit ? \"/app/update\" : \"/app/save\"}' />" class="item-form">
        
        <c:if test="${isEdit}">
            <input type="hidden" name="id" value="<c:out value='${item.id}' />" />
        </c:if>

        <div class="form-group">
            <label for="title"><fmt:message key="item.title" /> *</label>
            <input type="text" id="title" name="title" required 
                   value="<c:out value='${item.title}' />" />
        </div>

        <div class="form-group">
            <label for="mediaType"><fmt:message key="item.mediaType" /> *</label>
            <select id="mediaType" name="mediaType" required>
                <option value="">Selecione...</option>
                <option value="MOVIE" ${item.mediaType == 'MOVIE' ? 'selected' : ''}>
                    <fmt:message key="type.movie" />
                </option>
                <option value="SERIES" ${item.mediaType == 'SERIES' ? 'selected' : ''}>
                    <fmt:message key="type.series" />
                </option>
                <option value="BOOK" ${item.mediaType == 'BOOK' ? 'selected' : ''}>
                    <fmt:message key="type.book" />
                </option>
            </select>
        </div>

        <div class="form-group">
            <label for="authorDirector"><fmt:message key="item.authorDirector" /></label>
            <input type="text" id="authorDirector" name="authorDirector" 
                   value="<c:out value='${item.authorDirector}' />" />
        </div>

        <div class="form-group">
            <label for="releaseYear"><fmt:message key="item.releaseYear" /></label>
            <input type="number" id="releaseYear" name="releaseYear" min="1800" max="2100"
                   value="<c:out value='${item.releaseYear}' />" />
        </div>

        <div class="form-group">
            <label for="genre"><fmt:message key="item.genre" /></label>
            <input type="text" id="genre" name="genre" 
                   value="<c:out value='${item.genre}' />" />
        </div>

        <div class="form-group">
            <label for="synopsis"><fmt:message key="item.synopsis" /></label>
            <textarea id="synopsis" name="synopsis" rows="4"><c:out value='${item.synopsis}' /></textarea>
        </div>

        <div class="form-group">
            <label for="posterUrl"><fmt:message key="item.posterUrl" /></label>
            <input type="url" id="posterUrl" name="posterUrl" 
                   value="<c:out value='${item.posterUrl}' />" />
        </div>

        <div class="form-actions">
            <button type="submit" class="btn btn-primary">
                <fmt:message key="app.save" />
            </button>
            <a href="<c:url value='/app/list' />" class="btn btn-secondary">
                <fmt:message key="app.cancel" />
            </a>
        </div>
    </form>
</c:import>
