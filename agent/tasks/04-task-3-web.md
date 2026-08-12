# Task 3: Web (Controllers, JSP, Layout, Anti-XSS)

**Entrega:** `delivery/core`  
**Branches:** `feature/web-crud`, `feature/web-search`, `feature/layout`  
**Estimativa:** 8 pontos  
**Prioridade:** 🔴 BLOQUEADOR  
**Depende:** `feature/contracts` + `feature/infra` + `feature/service-crud` (merged)  
**Cobre:** SP3 completo + segurança de saída  
**Status:** Não iniciado

---

## Objetivo

Implementar **interface web segura** que:
- Servlets orquestracos (zero SQL, zero regra de negócio)
- JSP com `<c:out>` (escape XSS em toda saída)
- Formulário reaproveitável (novo/edição)
- Layout fixo (menu lateral, conteúdo à direita)
- i18n (`<fmt:message>`)

> **Segurança é visível aqui**: todo dado do usuário sai escapado.

---

## Escopo

### 1. Servlets (Controllers)

Criar um servlet para coordenar fluxo; padrão Front Controller com switch/case ou múltiplos servlets.

#### Opção A: Um Servlet com Switch (Recomendado para simplicidade)

```java
package com.seu.catalog.servlet;

import com.seu.catalog.infra.ConnectionFactory;
import com.seu.catalog.service.*;
import com.seu.catalog.exception.*;
import com.seu.catalog.model.*;
import com.seu.catalog.dao.MySqlMediaItemDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Servlet controlador principal da aplicação.
 * Recebe requisição HTTP, coordena Service, escolhe view.
 * Responsabilidades: orquestração APENAS.
 */
@WebServlet(urlPatterns = {"/", "/app/*"})
public class MediaController extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(MediaController.class.getName());
    
    private CatalogService service;

    @Override
    public void init() throws ServletException {
        super.init();
        // Injeção manual: DAO + fake provider (Task 4 injetará TMDB)
        var dao = new MySqlMediaItemDAO();
        var provider = new FakeMovieMetadataProvider();
        this.service = new CatalogService(dao, provider);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String action = extractAction(req);

        try {
            switch (action) {
                case "list":
                case "":
                    doList(req, resp);
                    break;
                case "detail":
                    doDetail(req, resp);
                    break;
                case "new":
                    doNewForm(req, resp);
                    break;
                case "edit":
                    doEditForm(req, resp);
                    break;
                case "search":
                    doSearch(req, resp);
                    break;
                default:
                    resp.sendError(404);
            }
        } catch (ServiceException e) {
            LOG.warning("Erro no serviço: " + e.getMessage());
            req.setAttribute("error", "Erro ao processar requisição");
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String action = extractAction(req);

        try {
            switch (action) {
                case "save":
                    doSave(req, resp);
                    break;
                case "update":
                    doUpdate(req, resp);
                    break;
                case "delete":
                    doDelete(req, resp);
                    break;
                default:
                    resp.sendError(404);
            }
        } catch (ServiceException | ValidationException e) {
            LOG.warning("Erro: " + e.getMessage());
            // Volta ao formulário com mensagem de erro
            req.setAttribute("error", e.getMessage());
            doNewForm(req, resp);
        }
    }

    // ==================== GET Actions ====================

    /**
     * GET /app/list ou / : lista todos os itens
     */
    private void doList(HttpServletRequest req, HttpServletResponse resp) 
            throws ServiceException, ServletException, IOException {
        List<MediaItem> items = service.listAllItems();
        req.setAttribute("items", items);
        req.getRequestDispatcher("/WEB-INF/jsp/list.jsp").forward(req, resp);
    }

    /**
     * GET /app/detail?id=1 : mostra detalhe de um item
     */
    private void doDetail(HttpServletRequest req, HttpServletResponse resp) 
            throws ServiceException, ServletException, IOException {
        Integer id = parseId(req.getParameter("id"));
        if (id == null) {
            resp.sendError(400);
            return;
        }

        MediaItem item = service.getItemById(id);
        if (item == null) {
            resp.sendError(404);
            return;
        }

        req.setAttribute("item", item);
        req.getRequestDispatcher("/WEB-INF/jsp/detail.jsp").forward(req, resp);
    }

    /**
     * GET /app/new : mostra formulário para novo item
     */
    private void doNewForm(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        req.setAttribute("item", null);
        req.setAttribute("isEdit", false);
        req.getRequestDispatcher("/WEB-INF/jsp/form.jsp").forward(req, resp);
    }

    /**
     * GET /app/edit?id=1 : mostra formulário de edição
     */
    private void doEditForm(HttpServletRequest req, HttpServletResponse resp) 
            throws ServiceException, ServletException, IOException {
        Integer id = parseId(req.getParameter("id"));
        if (id == null) {
            resp.sendError(400);
            return;
        }

        MediaItem item = service.getItemById(id);
        if (item == null) {
            resp.sendError(404);
            return;
        }

        req.setAttribute("item", item);
        req.setAttribute("isEdit", true);
        req.getRequestDispatcher("/WEB-INF/jsp/form.jsp").forward(req, resp);
    }

    /**
     * GET /app/search?term=... : busca por termo
     */
    private void doSearch(HttpServletRequest req, HttpServletResponse resp) 
            throws ServiceException, ServletException, IOException {
        String term = req.getParameter("term");
        if (term == null || term.trim().isEmpty()) {
            resp.sendError(400);
            return;
        }

        List<MediaItem> results = service.searchItems(term);
        req.setAttribute("items", results);
        req.setAttribute("searchTerm", term);
        req.getRequestDispatcher("/WEB-INF/jsp/search-results.jsp").forward(req, resp);
    }

    // ==================== POST Actions ====================

    /**
     * POST /app/save : cria novo item
     */
    private void doSave(HttpServletRequest req, HttpServletResponse resp) 
            throws ValidationException, ServiceException, IOException {
        MediaItem item = extractMediaItemFromRequest(req, false);
        service.createItem(item);
        resp.sendRedirect(req.getContextPath() + "/app/list");
    }

    /**
     * POST /app/update : atualiza item existente
     */
    private void doUpdate(HttpServletRequest req, HttpServletResponse resp) 
            throws ValidationException, ServiceException, IOException {
        MediaItem item = extractMediaItemFromRequest(req, true);
        service.updateItem(item);
        resp.sendRedirect(req.getContextPath() + "/app/list");
    }

    /**
     * POST /app/delete : deleta item
     */
    private void doDelete(HttpServletRequest req, HttpServletResponse resp) 
            throws ServiceException, IOException {
        Integer id = parseId(req.getParameter("id"));
        if (id == null) {
            resp.sendError(400);
            return;
        }

        service.deleteItem(id);
        resp.sendRedirect(req.getContextPath() + "/app/list");
    }

    // ==================== Helpers ====================

    private String extractAction(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            return "list";
        }
        return pathInfo.replaceFirst("^/", "").split("\\?")[0];
    }

    private Integer parseId(String idStr) {
        if (idStr == null) return null;
        try {
            return Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private MediaItem extractMediaItemFromRequest(HttpServletRequest req, boolean isEdit) {
        MediaItem item = new MediaItem(
            req.getParameter("title"),
            MediaType.valueOf(req.getParameter("mediaType"))
        );

        if (isEdit) {
            item.setId(parseId(req.getParameter("id")));
        }

        String releaseYear = req.getParameter("releaseYear");
        if (releaseYear != null && !releaseYear.isEmpty()) {
            try {
                item.setReleaseYear(Integer.parseInt(releaseYear));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Ano inválido", e);
            }
        }

        item.setAuthorDirector(req.getParameter("authorDirector"));
        item.setGenre(req.getParameter("genre"));
        item.setSynopsis(req.getParameter("synopsis"));
        item.setPosterUrl(req.getParameter("posterUrl"));
        item.setExternalId(req.getParameter("externalId"));

        String rating = req.getParameter("rating");
        if (rating != null && !rating.isEmpty()) {
            try {
                item.setRating(Integer.parseInt(rating));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Avaliação inválida", e);
            }
        }

        item.setComment(req.getParameter("comment"));

        return item;
    }
}
```

**Checklist Servlet:**
- [ ] Anotação `@WebServlet` com URL patterns
- [ ] `init()` injeta DAO + Provider + cria Service
- [ ] Métodos GET/POST definem actions
- [ ] Zero SQL (tudo delegado ao Service)
- [ ] Nunca relança `DAOException` (captura em `doPost`)
- [ ] Extrai ação do URL corretamente
- [ ] Javadoc PT-BR
- [ ] Sem lógica de validação/negócio

---

### 2. JSP Views

#### `src/main/webapp/WEB-INF/jsp/layout.jsp` (template base)

```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title><fmt:message key="app.title" /> | ${pageTitle}</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css' />" />
</head>
<body>
    <div class="container">
        <!-- Menu Lateral -->
        <aside class="sidebar">
            <h1><fmt:message key="app.title" /></h1>
            <nav>
                <ul>
                    <li><a href="<c:url value='/app/list' />" class="nav-link"><fmt:message key="app.home" /></a></li>
                    <li><a href="<c:url value='/app/list' />" class="nav-link"><fmt:message key="app.manage" /></a></li>
                    <li><a href="<c:url value='/app/new' />" class="nav-link"><fmt:message key="app.add" /></a></li>
                </ul>
            </nav>
        </aside>

        <!-- Conteúdo Principal -->
        <main class="content">
            <c:if test="${not empty error}">
                <div class="alert alert-error">
                    <c:out value="${error}" />
                </div>
            </c:if>

            <c:if test="${not empty success}">
                <div class="alert alert-success">
                    <c:out value="${success}" />
                </div>
            </c:if>

            <!-- Conteúdo específico da página -->
            <jsp:doBody />
        </main>
    </div>
</body>
</html>
```

#### `src/main/webapp/WEB-INF/jsp/list.jsp`

```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:import url="layout.jsp">
    <jsp:param name="pageTitle" value="Biblioteca" />
    
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
</c:import>
```

**Pontos críticos:**
- [ ] `<c:out value="${item.title}" />` escapa XSS
- [ ] NUNCA `${item.title}` (sem escape)
- [ ] NUNCA `<%= item.getTitle() %>` (scriptlet)
- [ ] `<fmt:message key="..." />` para i18n

#### `src/main/webapp/WEB-INF/jsp/form.jsp` (novo + edição)

```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:import url="layout.jsp">
    <jsp:param name="pageTitle" value="${isEdit ? 'Editar Mídia' : 'Novo Item'}" />
    
    <h2><c:out value="${isEdit ? 'Editar Mídia' : 'Novo Item'}" /></h2>

    <form method="POST" action="<c:url value='${isEdit ? \"/app/update\" : \"/app/save\"}' />" class="item-form">
        
        <c:if test="${isEdit}">
            <input type="hidden" name="id" value="<c:out value='${item.id}' />" />
        </c:if>

        <div class="form-group">
            <label for="title"><fmt:message key="item.title" /> *</label>
            <input type="text" id="title" name="title" required 
                   value="<c:out value='${item.title}' />" />
        </div>

        <div class="form-group">
            <label for="mediaType"><fmt:message key="item.mediaType" /> *</label>
            <select id="mediaType" name="mediaType" required>
                <option value="">Selecione...</option>
                <option value="MOVIE" ${item.mediaType == 'MOVIE' ? 'selected' : ''}>
                    <fmt:message key="type.movie" />
                </option>
                <option value="SERIES" ${item.mediaType == 'SERIES' ? 'selected' : ''}>
                    <fmt:message key="type.series" />
                </option>
                <option value="BOOK" ${item.mediaType == 'BOOK' ? 'selected' : ''}>
                    <fmt:message key="type.book" />
                </option>
            </select>
        </div>

        <div class="form-group">
            <label for="authorDirector"><fmt:message key="item.authorDirector" /></label>
            <input type="text" id="authorDirector" name="authorDirector" 
                   value="<c:out value='${item.authorDirector}' />" />
        </div>

        <div class="form-group">
            <label for="releaseYear"><fmt:message key="item.releaseYear" /></label>
            <input type="number" id="releaseYear" name="releaseYear" min="1800" max="2100"
                   value="<c:out value='${item.releaseYear}' />" />
        </div>

        <div class="form-group">
            <label for="genre"><fmt:message key="item.genre" /></label>
            <input type="text" id="genre" name="genre" 
                   value="<c:out value='${item.genre}' />" />
        </div>

        <div class="form-group">
            <label for="synopsis"><fmt:message key="item.synopsis" /></label>
            <textarea id="synopsis" name="synopsis" rows="4"><c:out value='${item.synopsis}' /></textarea>
        </div>

        <div class="form-group">
            <label for="posterUrl"><fmt:message key="item.posterUrl" /></label>
            <input type="url" id="posterUrl" name="posterUrl" 
                   value="<c:out value='${item.posterUrl}' />" />
        </div>

        <div class="form-actions">
            <button type="submit" class="btn btn-primary">
                <fmt:message key="app.save" />
            </button>
            <a href="<c:url value='/app/list' />" class="btn btn-secondary">
                <fmt:message key="app.cancel" />
            </a>
        </div>
    </form>
</c:import>
```

#### `src/main/webapp/WEB-INF/jsp/detail.jsp`

```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:import url="layout.jsp">
    <jsp:param name="pageTitle" value="${item.title}" />
    
    <div class="detail-header">
        <h2><c:out value="${item.title}" /></h2>
        <a href="<c:url value='/app/edit?id=${item.id}' />" class="btn btn-secondary">
            <fmt:message key="app.edit" />
        </a>
    </div>

    <div class="detail-content">
        <c:if test="${not empty item.posterUrl}">
            <div class="poster">
                <img src="<c:out value='${item.posterUrl}' />" alt="<c:out value='${item.title}' />" />
            </div>
        </c:if>

        <dl>
            <dt><fmt:message key="item.authorDirector" /></dt>
            <dd><c:out value="${item.authorDirector}" /></dd>

            <dt><fmt:message key="item.releaseYear" /></dt>
            <dd><c:out value="${item.releaseYear}" /></dd>

            <dt><fmt:message key="item.genre" /></dt>
            <dd><c:out value="${item.genre}" /></dd>

            <dt><fmt:message key="item.mediaType" /></dt>
            <dd><fmt:message key="type.${item.mediaType.toString().toLowerCase()}" /></dd>

            <dt><fmt:message key="item.synopsis" /></dt>
            <dd><c:out value="${item.synopsis}" /></dd>
        </dl>
    </div>

    <div class="detail-actions">
        <a href="<c:url value='/app/list' />" class="btn btn-secondary">
            <fmt:message key="app.back" />
        </a>
    </div>
</c:import>
```

#### `src/main/webapp/WEB-INF/jsp/search-results.jsp`

```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:import url="layout.jsp">
    <jsp:param name="pageTitle" value="Resultados da Busca" />
    
    <h2>Resultados da Busca</h2>
    <p><fmt:message key="search.term" />: <c:out value="${searchTerm}" /></p>

    <c:choose>
        <c:when test="${empty items}">
            <p><fmt:message key="search.no.results" /></p>
        </c:when>
        <c:otherwise>
            <p>Encontrados <c:out value="${items.size()}" /> resultado(s).</p>
            <jsp:include page="list.jsp" />
        </c:otherwise>
    </c:choose>

    <a href="<c:url value='/app/list' />" class="btn btn-secondary">
        <fmt:message key="app.back" />
    </a>
</c:import>
```

**Checklist JSP:**
- [ ] `<c:out value="..." />` em toda saída de usuário
- [ ] Sem scriptlets (`<% %>`)
- [ ] `<fmt:message key="..." />` para i18n
- [ ] `form.jsp` reutilizável (novo + edição com `${isEdit}`)
- [ ] Links com `<c:url value="..." />` (relativização de path)

---

### 3. CSS Base

#### `src/main/webapp/css/style.css`

```css
/* Reset */
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

/* Layout */
body {
    font-family: -apple-system, "Segoe UI", Roboto, sans-serif;
    background: #FFFFFF;
    color: #111827;
}

.container {
    display: flex;
    min-height: 100vh;
}

/* Sidebar */
.sidebar {
    width: 250px;
    background: #FFFFFF;
    border-right: 1px solid #E5E7EB;
    padding: 20px;
    position: fixed;
    height: 100vh;
    overflow-y: auto;
}

.sidebar h1 {
    font-size: 28px;
    font-weight: 700;
    margin-bottom: 30px;
    color: #111827;
}

.sidebar nav ul {
    list-style: none;
}

.sidebar nav li {
    margin-bottom: 15px;
}

.sidebar .nav-link {
    display: block;
    padding: 10px 15px;
    color: #6B7280;
    text-decoration: none;
    border-radius: 4px;
    transition: all 0.2s;
}

.sidebar .nav-link:hover,
.sidebar .nav-link.active {
    background: #F3F4F6;
    color: #F97316;
}

/* Content */
.content {
    flex: 1;
    margin-left: 250px;
    padding: 40px;
}

/* Typography */
h1 { font-size: 28px; font-weight: 700; }
h2 { font-size: 22px; font-weight: 600; margin-bottom: 20px; }
h3 { font-size: 18px; font-weight: 600; }
p { font-size: 16px; line-height: 1.5; margin-bottom: 15px; }

/* Forms */
.form-group {
    margin-bottom: 20px;
}

.form-group label {
    display: block;
    font-weight: 600;
    margin-bottom: 5px;
}

.form-group input,
.form-group textarea,
.form-group select {
    width: 100%;
    padding: 10px;
    border: 1px solid #E5E7EB;
    border-radius: 4px;
    font-family: inherit;
    font-size: 16px;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
    outline: none;
    border-color: #F97316;
    box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.1);
}

/* Buttons */
.btn {
    display: inline-block;
    padding: 10px 20px;
    border: none;
    border-radius: 4px;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    text-decoration: none;
    transition: all 0.2s;
}

.btn-primary {
    background: #F97316;
    color: white;
}

.btn-primary:hover {
    background: #EA580C;
}

.btn-secondary {
    background: white;
    color: #111827;
    border: 1px solid #E5E7EB;
}

.btn-secondary:hover {
    background: #F3F4F6;
}

.btn-sm {
    padding: 6px 12px;
    font-size: 14px;
}

.btn-danger {
    background: #EF4444;
    color: white;
}

.btn-danger:hover {
    background: #DC2626;
}

/* Tables */
.items-table {
    width: 100%;
    border-collapse: collapse;
    margin-bottom: 20px;
}

.items-table th {
    background: #F3F4F6;
    padding: 12px;
    text-align: left;
    font-weight: 600;
    border-bottom: 2px solid #E5E7EB;
}

.items-table td {
    padding: 12px;
    border-bottom: 1px solid #E5E7EB;
}

/* Alerts */
.alert {
    padding: 15px;
    border-radius: 4px;
    margin-bottom: 20px;
}

.alert-error {
    background: #FEE2E2;
    color: #991B1B;
    border: 1px solid #FECACA;
}

.alert-success {
    background: #DCFCE7;
    color: #15803D;
    border: 1px solid #BBF7D0;
}

/* Search */
.search-form {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
}

.search-form input {
    flex: 1;
    padding: 10px;
    border: 1px solid #E5E7EB;
    border-radius: 4px;
}

.search-form button {
    padding: 10px 20px;
    background: #F97316;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-weight: 600;
}

/* Detail Page */
.detail-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
}

.detail-content {
    display: grid;
    grid-template-columns: 1fr 2fr;
    gap: 30px;
    margin-bottom: 30px;
}

.detail-content .poster img {
    max-width: 100%;
    height: auto;
    border-radius: 4px;
}

.detail-content dl {
    display: grid;
    grid-template-columns: 150px 1fr;
    gap: 15px;
}

.detail-content dt {
    font-weight: 600;
    color: #111827;
}

.detail-content dd {
    color: #6B7280;
}

/* Responsive */
@media (max-width: 768px) {
    .sidebar {
        width: 100%;
        height: auto;
        position: relative;
        border-right: none;
        border-bottom: 1px solid #E5E7EB;
    }

    .content {
        margin-left: 0;
        padding: 20px;
    }

    .detail-content {
        grid-template-columns: 1fr;
    }
}
```

---

### 4. Testes Funcionais

#### `src/test/java/com/seu/catalog/servlet/MediaControllerTest.java`

```java
package com.seu.catalog.servlet;

import com.seu.catalog.dao.MediaItemDAO;
import com.seu.catalog.exception.ServiceException;
import com.seu.catalog.model.MediaItem;
import com.seu.catalog.model.MediaType;
import com.seu.catalog.service.CatalogService;
import com.seu.catalog.service.FakeMovieMetadataProvider;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes funcionais do Servlet.
 * Nota: Usa mock de request/response; testes E2E fariam via Selenium.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MediaController Functional Tests")
class MediaControllerTest {
    
    @Mock
    private MediaItemDAO daoMock;
    
    private CatalogService service;
    private MediaController servlet;

    @BeforeEach
    void setUp() {
        service = new CatalogService(daoMock, new FakeMovieMetadataProvider());
        servlet = new MediaController();
        servlet.service = service;
    }

    // ... testes básicos de fluxo
    // Nota: Testes reais de Servlet precisam de mais setup (ServletContext, etc.)
    // Esses são exemplos simplificados; testes E2E rodariam com Tomcat em pé.
}
```

**Checklist Testes:**
- [ ] Testes de fluxo (GET /app/list, POST /app/save, etc.)
- [ ] Testes de erro (404, 400)
- [ ] Testes de redirecionamento
- [ ] Testes de XSS (input com `<script>` sai escapado em `<c:out>`)

---

## Travas (Constraints Críticas)

### 🔴 TRAVA 1: ZERO Scriptlet em JSP
- ❌ `<% ... %>`, `<%= ... %>`
- ✅ `<c:forEach>`, `<c:choose>`, `<fmt:message>`

**Verificação:** grep para `<%` em todos os `.jsp`.

### 🔴 TRAVA 2: TODA Saída com `<c:out>`
- ❌ `${item.title}` (desescapado)
- ✅ `<c:out value="${item.title}" />`

**Por quê?** Se entrada contiver `<script>alert('xss')</script>`, sem `<c:out>` roda JavaScript. Com `<c:out>`, renderiza como texto.

### 🔴 TRAVA 3: Servlet = Orquestração Apenas
- ❌ SQL no Servlet
- ❌ Lógica de validação no Servlet
- ✅ Extrai dados → chama Service → escolhe view

### 🔴 TRAVA 4: URL com `<c:url>`
- ❌ `href="/app/list"`
- ✅ `href="<c:url value='/app/list' />"`

`<c:url>` relativa o path ao contexto da app.

### 🔴 TRAVA 5: i18n com `<fmt:message>`
- ❌ Strings hardcoded em JSP
- ✅ `<fmt:message key="app.title" />`

Mesmo em PT-BR, toda string deve vir do bundle para suportar EN no futuro.

---

## Critérios de Aceite

### Funcionalidade Web
- [ ] Servlet recebe HTTP, coordena Service, escolhe view
- [ ] GET /app/list: lista itens
- [ ] GET /app/detail?id=X: detalhe de item
- [ ] GET /app/new: formulário vazio
- [ ] GET /app/edit?id=X: formulário preenchido
- [ ] GET /app/search?term=X: busca
- [ ] POST /app/save: cria item
- [ ] POST /app/update: atualiza item
- [ ] POST /app/delete: deleta item

### Segurança
- [ ] Toda saída em `<c:out>` (teste com `<script>alert...</script>`)
- [ ] Sem scriptlet em nenhuma JSP
- [ ] Form POST com CSRF (futuro: adicionar token)

### Layout
- [ ] Menu lateral fixo (branco, borda cinza)
- [ ] Itens menu em laranja quando ativo
- [ ] Conteúdo à direita
- [ ] Cores conforme seção 7 do PRD
- [ ] Responsivo (mobile-friendly)

### i18n
- [ ] Todos os textos de UI via `<fmt:message>`
- [ ] `messages_pt_BR.properties` + `messages_en.properties`
- [ ] Troca de idioma funciona (futuro)

### Build & Testes
- [ ] `mvn clean verify` passa
- [ ] Testes funcionais (servlet)
- [ ] Javadoc PT-BR
- [ ] Sem warnings

---

## Estratégia de Teste

**Local:**
```bash
# 1. Start Docker
docker compose up -d

# 2. Build
mvn clean package

# 3. Acesse
curl -s http://localhost:8080/app/list | grep "Catálogo de Mídia"

# 4. Teste XSS (adicionar item com <script>)
# Verifique no HTML se sai escapado (<c:out faz isso)
```

**Teste de XSS manual:**
1. Adicione item com title = `<script>alert('xss')</script>`
2. Acesse list.jsp
3. Verifique no HTML-source que título saiu como string literal, não executável

---

## Checkpoints Durante a Implementação

1. **Servlet estrutura**
   - [ ] Controller criado, anotado `@WebServlet`
   - [ ] Métodos GET/POST compilam
   - [ ] Injeção de Service no init()

2. **JSP básicas**
   - [ ] layout.jsp (template base)
   - [ ] list.jsp (listagem com `<c:out>`)
   - [ ] form.jsp (novo + edição)

3. **Fluxo CRUD**
   - [ ] GET /app/list retorna lista
   - [ ] GET /app/new mostra form vazio
   - [ ] POST /app/save cria item + redireciona

4. **CSS**
   - [ ] style.css aplicado (cores, layout)
   - [ ] Sidebar + content visíveis

5. **i18n**
   - [ ] `<fmt:message key="..." />` funciona
   - [ ] Sem strings hardcoded

6. **XSS**
   - [ ] Adicione item com `<script>...`
   - [ ] Verifique que sai escapado em HTML

---

## Definition of Done

Uma PR `feature/web-*` ou `feature/layout` é mergeable se:

- [ ] `mvn clean verify` passa (compile + testes + Javadoc)
- [ ] Servlet: GET /app/list, /detail, /new, /edit, /search + POST /save, /update, /delete
- [ ] Todas JSP com `<c:out>` em saída de usuário
- [ ] Sem scriptlets em nenhuma JSP
- [ ] Layout: sidebar fixo (branco, borda cinza), conteúdo à direita
- [ ] Cores conforme PRD seção 7
- [ ] i18n: `<fmt:message>` em toda UI, sem hardcoding
- [ ] Teste XSS: entrada `<script>` renderiza como texto
- [ ] CSS: responsivo (mobile)
- [ ] Javadoc PT-BR (Servlet + helpers)
- [ ] Commits: `feat: add media controller`, `feat: add list view`, `feat: add form view`, `feat: add layout and styles`
- [ ] PR pequena (um escopo: Servlet OU views OU layout)
- [ ] Revisado contra seções 1, 2, 3, 7

---

## Próximos Passos

Após merge:
- ✅ Núcleo completo (Tasks 0-3)
- ✅ Task 4 (TMDB) pode começar (interfaces prontas)
- ✅ Task 5 (Frontend) pode começar
- ✅ Task 6 (Rating) pode começar

**Todas as extensões desbloqueadas.**

---

## Notas

- **CSRF Token:** Por agora, omitido (fora do escopo). Se quiser adicionar: use filter que injeta token no request/response.
- **Validação Client:** Usar atributos HTML (`required`, `type="number"`, `min/max`). Server sempre revalida.
- **Uploads:** Sem upload de arquivo (posterUrl é URL externa). Se quiser futuramente: adicionar `<input type="file">`.
- **REST API:** Servlet aqui é MVC tradicional (server-rendered JSP). Se Task 5+ quiser SPA, cria `/api/*` endpoints que retornam JSON.

---

**Versão 1.0 | 2026-08-12**
