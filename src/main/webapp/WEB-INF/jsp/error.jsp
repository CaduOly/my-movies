<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout pageTitle="Erro">
    <div style="text-align: center; padding: 50px 20px;">
        <h1 style="font-size: 72px; color: #EF4444; margin-bottom: 20px;">500</h1>
        <h2 style="font-size: 24px; color: #334155; margin-bottom: 30px;">Ops! Ocorreu um erro interno.</h2>
        <p style="color: #64748B; margin-bottom: 40px;">Não se preocupe, o problema já foi registrado para nossa equipe.</p>
        <a href="<c:url value='/app/home' />" class="btn btn-primary" style="padding: 12px 24px; text-decoration: none; display: inline-block;">
            Voltar para o Início
        </a>
    </div>
</t:layout>
