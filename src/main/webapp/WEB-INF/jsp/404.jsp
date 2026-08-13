<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout pageTitle="404 - Not Found">
    <div style="text-align: center; padding: 50px 20px;">
        <h1 style="font-size: 72px; color: #F97316; margin-bottom: 20px;">404</h1>
        <h2 style="font-size: 24px; color: #334155; margin-bottom: 30px;">Ops! Página não encontrada.</h2>
        <p style="color: #64748B; margin-bottom: 40px;">Parece que a página que você está procurando não existe ou foi movida.</p>
        <a href="<c:url value='/app/home' />" class="btn btn-primary" style="padding: 12px 24px; text-decoration: none; display: inline-block;">
            Voltar para o Início
        </a>
    </div>
</t:layout>
