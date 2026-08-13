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
    <c:choose>
        <c:when test="${empty items}">
            <p><fmt:message key="empty.list" /></p>
        </c:when>
        <c:otherwise>
            <table class="items-table">
                <thead>
                    <tr>
                        <th><fmt:message key="item.title" /></th>
                        <th><fmt:message key="item.authorDirector" /></th>
                        <th><fmt:message key="item.releaseYear" /></th>
                        <th><fmt:message key="item.genre" /></th>
                        <th><fmt:message key="item.mediaType" /></th>
                        <th>Ações</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${items}">
                        <tr>
                            <td><c:out value="${item.title}" /></td>
                            <td><c:out value="${item.authorDirector}" /></td>
                            <td><c:out value="${item.releaseYear}" /></td>
                            <td><c:out value="${item.genre}" /></td>
                            <td><c:out value="${item.mediaType}" /></td>
                            <td>
                                <a href="<c:url value='/app/detail?id=${item.id}' />" class="btn btn-sm"><fmt:message key="app.view" /></a>
                                <a href="<c:url value='/app/edit?id=${item.id}' />" class="btn btn-sm btn-secondary"><fmt:message key="app.edit" /></a>
                                <form method="POST" action="<c:url value='/app/delete' />" style="display:inline;">
                                    <input type="hidden" name="id" value="<c:out value='${item.id}' />" />
                                    <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Tem certeza?')">
                                        <fmt:message key="app.delete" />
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</t:layout>
