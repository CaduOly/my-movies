<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/sidebar.jsp" />

<div class="d-flex justify-content-between align-items-center mb-3">
    <h2><fmt:message key="nav.manage" /></h2>
    <a href="${pageContext.request.contextPath}/items?action=new" class="btn btn-success"><fmt:message key="nav.add" /></a>
</div>

<form action="${pageContext.request.contextPath}/items" method="GET" class="mb-4">
    <input type="hidden" name="action" value="search" />
    <div class="input-group">
        <input type="text" name="term" class="form-control bg-dark text-light border-secondary" placeholder="<fmt:message key="action.search" />..." />
        <button class="btn btn-primary" type="submit"><fmt:message key="action.search" /></button>
    </div>
</form>

<table class="table table-dark table-striped">
    <thead>
        <tr>
            <th>ID</th>
            <th><fmt:message key="label.title" /></th>
            <th><fmt:message key="label.type" /></th>
            <th><fmt:message key="label.year" /></th>
            <th><fmt:message key="label.author" /></th>
            <th><fmt:message key="label.actions" /></th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="item" items="${items}">
            <tr>
                <td><c:out value="${item.id}" /></td>
                <td><c:out value="${item.title}" /></td>
                <td><c:out value="${item.type}" /></td>
                <td><c:out value="${item.releaseYear}" /></td>
                <td><c:out value="${item.authorDirector}" /></td>
                <td>
                    <a href="${pageContext.request.contextPath}/items?action=edit&id=${item.id}" class="btn btn-warning btn-sm"><fmt:message key="action.edit" /></a>
                    <form action="${pageContext.request.contextPath}/items" method="POST" style="display:inline;" onsubmit="return confirm('Are you sure?');">
                        <input type="hidden" name="action" value="delete" />
                        <input type="hidden" name="id" value="${item.id}" />
                        <button type="submit" class="btn btn-danger btn-sm"><fmt:message key="action.delete" /></button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
