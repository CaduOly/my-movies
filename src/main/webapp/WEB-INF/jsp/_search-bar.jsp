<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<form method="GET" action="<c:url value='/app/search' />" class="search-form" style="margin-bottom:30px; display:flex; align-items:center;">
    <input type="text" name="term" value="<c:out value='${searchTerm}'/>" placeholder="<fmt:message key='app.search.placeholder' />" style="padding:12px; width:100%; max-width:400px; border:1px solid #CBD5E1; border-radius:4px; margin-right:10px;" />
    <button type="submit" class="btn btn-primary" style="padding:12px 24px;"><fmt:message key="app.search" /></button>
</form>
