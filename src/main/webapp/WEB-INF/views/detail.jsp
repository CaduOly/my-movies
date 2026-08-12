<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/sidebar.jsp" />

<div class="card bg-dark text-light border-secondary">
    <div class="card-body">
        <h2 class="card-title"><c:out value="${item.title}" /></h2>
        <h5 class="card-subtitle mb-3 text-muted"><c:out value="${item.type}" /> &bull; <c:out value="${item.releaseYear}" /> &bull; <c:out value="${item.genre}" /></h5>
        <h6 class="card-subtitle mb-3 text-muted"><fmt:message key="label.author" />: <c:out value="${item.authorDirector}" /></h6>
        
        <h4><fmt:message key="label.synopsis" /></h4>
        <p class="card-text" style="white-space: pre-wrap;"><c:out value="${item.synopsis}" /></p>

        <a href="${pageContext.request.contextPath}/items?action=manage" class="btn btn-secondary mt-3">Back to Manage</a>
        <a href="${pageContext.request.contextPath}/home" class="btn btn-secondary mt-3">Back to Home</a>
    </div>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
