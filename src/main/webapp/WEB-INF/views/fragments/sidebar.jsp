<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div class="col-md-2 sidebar">
    <h3><fmt:message key="app.title" /></h3>
    <hr>
    <a href="${pageContext.request.contextPath}/home"><fmt:message key="nav.home" /></a>
    <a href="${pageContext.request.contextPath}/items?action=manage"><fmt:message key="nav.manage" /></a>
    <a href="${pageContext.request.contextPath}/items?action=new"><fmt:message key="nav.add" /></a>
    <hr>
    <div>
        <a href="?lang=pt_BR" style="display:inline">PT</a> | 
        <a href="?lang=en" style="display:inline">EN</a>
    </div>
</div>
<div class="col-md-10 p-4">
