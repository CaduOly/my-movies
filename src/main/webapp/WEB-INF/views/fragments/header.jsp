<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="${empty param.lang ? 'pt_BR' : param.lang}" />
<fmt:setBundle basename="messages" />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><fmt:message key="app.title" /></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #121212; color: #fff; }
        .sidebar { min-height: 100vh; background-color: #1e1e1e; padding: 20px; }
        .sidebar a { color: #aaa; text-decoration: none; display: block; margin-bottom: 15px; }
        .sidebar a:hover { color: #fff; }
        .card { background-color: #1e1e1e; border: none; }
        .card-title { color: #fff; }
        .table { color: #fff; }
        .table th, .table td { border-color: #333; color: #fff; }
    </style>
</head>
<body>
<div class="container-fluid">
    <div class="row">
