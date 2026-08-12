# Task 5: Frontend Estendido - Home (Grid/Carrossel + Detalhe)

**Entrega:** `delivery/frontend-home`  
**Branch:** `feature/home-grid`  
**Estimativa:** 5 pontos  
**Prioridade:** 🟢 BONUS (após núcleo)  
**Depende:** Tasks 0-3 (núcleo) + Task 4 (TMDB para pôsteres)  
**Status:** Não iniciado

---

## Objetivo

Estender a home com:
- **Grid de capas** (pôsteres) em vez de tabela
- **Carrossel** (optional) com itens destacados
- **Detalhe expandido** em modal ou página separada
- **100% JSP/JSTL** (sem framework front)
- JavaScript vanilla leve (só para interação)

---

## Escopo

### 1. Home com Grid de Capas

#### `src/main/webapp/index.jsp` ou `/app/home`

```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:import url="layout.jsp">
    <jsp:param name="pageTitle" value="Home" />
    
    <h1><fmt:message key="app.home" /></h1>

    <!-- Carrossel (opcional) -->
    <div id="carousel" class="carousel">
        <div class="carousel-slides">
            <c:forEach var="item" items="${carouselItems}" varStatus="status">
                <div class="carousel-slide ${status.index == 0 ? 'active' : ''}">
                    <img src="<c:out value='${item.posterUrl}' />" alt="<c:out value='${item.title}' />" />
                    <h3><c:out value="${item.title}" /></h3>
                </div>
            </c:forEach>
        </div>
        <button class="carousel-prev" onclick="prevSlide()">❮</button>
        <button class="carousel-next" onclick="nextSlide()">❯</button>
    </div>

    <!-- Grid de Capas -->
    <section class="grid-section">
        <h2><fmt:message key="app.library" /></h2>
        <div class="grid">
            <c:forEach var="item" items="${items}">
                <div class="grid-item">
                    <c:choose>
                        <c:when test="${not empty item.posterUrl}">
                            <img src="<c:out value='${item.posterUrl}' />" 
                                 alt="<c:out value='${item.title}' />" 
                                 class="poster" />
                        </c:when>
                        <c:otherwise>
                            <div class="poster-placeholder">
                                <fmt:message key="no.poster" />
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <div class="grid-info">
                        <h4><c:out value="${item.title}" /></h4>
                        <p class="year"><c:out value="${item.releaseYear}" /></p>
                        <a href="<c:url value='/app/detail?id=${item.id}' />" class="btn btn-sm btn-primary">
                            <fmt:message key="app.view" />
                        </a>
                    </div>
                </div>
            </c:forEach>
        </div>
    </section>
</c:import>

<script src="<c:url value='/js/carousel.js' />"></script>
```

### 2. JavaScript Vanilla Leve

#### `src/main/webapp/js/carousel.js`

```javascript
/**
 * Carrossel simples (vanilla JS, sem jQuery/Bootstrap).
 */

let currentSlide = 0;
const slides = document.querySelectorAll('.carousel-slide');

function showSlide(n) {
    slides.forEach(slide => slide.classList.remove('active'));
    if (slides[n]) {
        slides[n].classList.add('active');
    }
}

function nextSlide() {
    currentSlide = (currentSlide + 1) % slides.length;
    showSlide(currentSlide);
}

function prevSlide() {
    currentSlide = (currentSlide - 1 + slides.length) % slides.length;
    showSlide(currentSlide);
}

// Auto-advance carousel a cada 5s (opcional)
setInterval(nextSlide, 5000);
```

### 3. CSS para Grid + Carrossel

#### Adicionar a `style.css`

```css
/* Carousel */
.carousel {
    position: relative;
    width: 100%;
    height: 400px;
    overflow: hidden;
    border-radius: 8px;
    margin-bottom: 40px;
    background: #1F2937;
}

.carousel-slides {
    position: relative;
    width: 100%;
    height: 100%;
}

.carousel-slide {
    position: absolute;
    width: 100%;
    height: 100%;
    opacity: 0;
    transition: opacity 0.5s ease-in-out;
}

.carousel-slide.active {
    opacity: 1;
}

.carousel-slide img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.carousel-slide h3 {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    background: linear-gradient(transparent, rgba(0,0,0,0.8));
    color: white;
    padding: 20px;
    margin: 0;
    font-size: 24px;
    font-weight: 700;
}

.carousel-prev,
.carousel-next {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    background: rgba(0,0,0,0.5);
    color: white;
    border: none;
    padding: 10px 15px;
    font-size: 20px;
    cursor: pointer;
    z-index: 10;
    transition: background 0.2s;
}

.carousel-prev:hover,
.carousel-next:hover {
    background: rgba(0,0,0,0.8);
}

.carousel-prev {
    left: 0;
}

.carousel-next {
    right: 0;
}

/* Grid */
.grid-section {
    margin-top: 40px;
}

.grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 20px;
    margin-bottom: 40px;
}

.grid-item {
    background: #F9FAFB;
    border-radius: 8px;
    overflow: hidden;
    transition: transform 0.2s, box-shadow 0.2s;
    cursor: pointer;
}

.grid-item:hover {
    transform: translateY(-4px);
    box-shadow: 0 10px 25px rgba(0,0,0,0.1);
}

.poster {
    width: 100%;
    height: 300px;
    object-fit: cover;
    background: #E5E7EB;
}

.poster-placeholder {
    width: 100%;
    height: 300px;
    background: #E5E7EB;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #6B7280;
    font-size: 14px;
    text-align: center;
    padding: 20px;
}

.grid-info {
    padding: 15px;
}

.grid-info h4 {
    margin: 0 0 8px;
    font-size: 16px;
    color: #111827;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.grid-info .year {
    margin: 0 0 12px;
    color: #6B7280;
    font-size: 14px;
}

.grid-info .btn {
    width: 100%;
    text-align: center;
}

/* Detalhe Modal (opcional) */
.modal {
    display: none;
    position: fixed;
    z-index: 1000;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0,0,0,0.7);
}

.modal-content {
    background-color: white;
    margin: 5% auto;
    padding: 30px;
    border-radius: 8px;
    width: 80%;
    max-width: 600px;
    max-height: 80vh;
    overflow-y: auto;
}

.modal-close {
    color: #6B7280;
    float: right;
    font-size: 28px;
    font-weight: bold;
    cursor: pointer;
}

.modal-close:hover {
    color: #111827;
}

/* Responsive */
@media (max-width: 768px) {
    .grid {
        grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
        gap: 15px;
    }

    .carousel {
        height: 250px;
    }
}
```

### 4. Servlet: Popular Grid/Carrossel

No `MediaController`, adicionar:

```java
private void doList(HttpServletRequest req, HttpServletResponse resp) 
        throws ServiceException, ServletException, IOException {
    List<MediaItem> items = service.listAllItems();
    
    // Carrossel: primeiros 5 items com pôster
    List<MediaItem> carouselItems = items.stream()
        .filter(item -> item.getPosterUrl() != null)
        .limit(5)
        .collect(Collectors.toList());
    
    req.setAttribute("items", items);
    req.setAttribute("carouselItems", carouselItems);
    req.getRequestDispatcher("/WEB-INF/jsp/home.jsp").forward(req, resp);
}
```

---

## Travas

### 🔴 TRAVA 1: Sem Framework Front
- ❌ Bootstrap, Tailwind, Material UI
- ✅ CSS puro, JS vanilla

### 🔴 TRAVA 2: Pôsteres Opcionais
- Se item sem posterUrl: placeholder ou link default
- Nunca quebra layout

### 🔴 TRAVA 3: Grid Responsivo
- Desktop: múltiplas colunas
- Mobile: coluna única
- Nenhuma quebra

---

## Critérios de Aceite

- [ ] Home com grid de capas (pôsteres)
- [ ] Carrossel automático (opcional, mas nice-to-have)
- [ ] Responsive (desktop + mobile)
- [ ] Placeholder se sem pôster
- [ ] JS vanilla leve (< 2KB)
- [ ] Sem framework front
- [ ] 100% JSP/JSTL

---

**Versão 1.0 | 2026-08-12**
