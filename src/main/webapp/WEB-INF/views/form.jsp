<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/sidebar.jsp" />

<h2>
    <c:choose>
        <c:when test="${not empty item.id}"><fmt:message key="action.edit" /></c:when>
        <c:otherwise><fmt:message key="nav.add" /></c:otherwise>
    </c:choose>
</h2>

<c:if test="${not empty error}">
    <div class="alert alert-danger"><c:out value="${error}" /></div>
</c:if>

<form action="${pageContext.request.contextPath}/items" method="POST">
    <input type="hidden" name="action" value="save" />
    <input type="hidden" name="id" value="${item.id}" />

    <div class="mb-3">
        <label for="title" class="form-label"><fmt:message key="label.title" /></label>
        <input type="text" class="form-control bg-dark text-light border-secondary" id="title" name="title" value="<c:out value="${item.title}" />" required />
    </div>

    <div class="mb-3">
        <label for="type" class="form-label"><fmt:message key="label.type" /></label>
        <select class="form-control bg-dark text-light border-secondary" id="type" name="type" required>
            <option value="MOVIE" ${item.type == 'MOVIE' ? 'selected' : ''}>MOVIE</option>
            <option value="SERIES" ${item.type == 'SERIES' ? 'selected' : ''}>SERIES</option>
            <option value="BOOK" ${item.type == 'BOOK' ? 'selected' : ''}>BOOK</option>
        </select>
    </div>

    <div class="mb-3">
        <label for="releaseYear" class="form-label"><fmt:message key="label.year" /></label>
        <input type="number" class="form-control bg-dark text-light border-secondary" id="releaseYear" name="releaseYear" value="<c:out value="${item.releaseYear}" />" />
    </div>

    <div class="mb-3">
        <label for="authorDirector" class="form-label"><fmt:message key="label.author" /></label>
        <input type="text" class="form-control bg-dark text-light border-secondary" id="authorDirector" name="authorDirector" value="<c:out value="${item.authorDirector}" />" />
    </div>

    <div class="mb-3">
        <label for="genre" class="form-label"><fmt:message key="label.genre" /></label>
        <input type="text" class="form-control bg-dark text-light border-secondary" id="genre" name="genre" value="<c:out value="${item.genre}" />" />
    </div>

    <div class="mb-3">
        <label for="synopsis" class="form-label"><fmt:message key="label.synopsis" /></label>
        <textarea class="form-control bg-dark text-light border-secondary" id="synopsis" name="synopsis" rows="5"><c:out value="${item.synopsis}" /></textarea>
    </div>

    <button type="submit" class="btn btn-primary"><fmt:message key="action.save" /></button>
    <a href="${pageContext.request.contextPath}/items?action=manage" class="btn btn-secondary">Cancel</a>
</form>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
