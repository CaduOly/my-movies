<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/sidebar.jsp" />

<h2><fmt:message key="nav.home" /></h2>
<div class="row row-cols-1 row-cols-md-4 g-4 mt-3">
    <c:forEach var="item" items="${items}">
        <div class="col">
            <div class="card h-100">
                <div class="card-body">
                    <h5 class="card-title"><c:out value="${item.title}" /></h5>
                    <p class="card-text"><c:out value="${item.releaseYear}" /> &bull; <c:out value="${item.type}" /></p>
                    <a href="${pageContext.request.contextPath}/items?action=detail&id=${item.id}" class="btn btn-primary btn-sm"><fmt:message key="action.detail" /></a>
                </div>
            </div>
        </div>
    </c:forEach>
    <c:if test="${empty items}">
        <p><fmt:message key="msg.empty" /></p>
    </c:if>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
