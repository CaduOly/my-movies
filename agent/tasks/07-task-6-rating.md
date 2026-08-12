# Task 6: Avaliação (Estrelas + Comentário)

**Entrega:** `delivery/rating`  
**Branch:** `feature/rating-ui`  
**Estimativa:** 3 pontos  
**Prioridade:** 🟢 BONUS (após frontend)  
**Depende:** Tasks 0-3 (núcleo) + Task 5 (detalhe)  
**Status:** Não iniciado

---

## Objetivo

Adicionar funcionalidade de avaliação:
- **Rating (0-5 estrelas)** com UI interativa
- **Comentário** de texto livre
- **Persistência** nas colunas `rating` e `comment` (já em `item_media` desde Task 0)
- **Validação server-side** (rating 0-5, comment < 1000 chars)
- **Teste de validação** (rating fora de faixa rejeitado)

---

## Escopo

### 1. Atualizar `MediaItem` (se necessário)

Já existem campos `rating` e `comment` em `MediaItem` (POJO). Nada a fazer aqui.

### 2. Atualizar Validator (Task 2)

Em `MediaItemValidator`, adicionar regras para rating:

```java
private void validateRating(Integer rating) throws ValidationException {
    if (rating != null) {
        if (rating < 0 || rating > 5) {
            throw new ValidationException("Avaliação deve estar entre 0 e 5");
        }
    }
}

private void validateComment(String comment) throws ValidationException {
    if (comment != null && comment.length() > 1000) {
        throw new ValidationException("Comentário não pode exceder 1000 caracteres");
    }
}
```

**Nota:** Já feito em Task 2. Esta task só estende.

### 3. Atualizar formulário (form.jsp)

Adicionar campos de rating e comentário:

```jsp
<div class="form-group">
    <label for="rating"><fmt:message key="item.rating" /></label>
    <div class="rating-input">
        <div class="stars">
            <c:forEach var="i" begin="1" end="5">
                <input type="radio" name="rating" id="star${i}" value="${i}" 
                       ${item.rating == i ? 'checked' : ''} />
                <label for="star${i}" class="star">★</label>
            </c:forEach>
        </div>
        <span id="rating-display"></span>
    </div>
</div>

<div class="form-group">
    <label for="comment"><fmt:message key="item.comment" /> (máx. 1000 chars)</label>
    <textarea id="comment" name="comment" rows="4" maxlength="1000">
        <c:out value='${item.comment}' />
    </textarea>
    <small id="char-count">0 / 1000</small>
</div>
```

### 4. CSS para Avaliação com Estrelas

Adicionar a `style.css`:

```css
/* Rating Stars */
.rating-input {
    display: flex;
    align-items: center;
    gap: 15px;
}

.stars {
    display: flex;
    gap: 5px;
    font-size: 28px;
}

.stars input {
    display: none;
}

.stars label {
    color: #E5E7EB;
    cursor: pointer;
    transition: color 0.2s;
}

.stars input:checked ~ label,
.stars label:hover {
    color: #F97316;
}

#rating-display {
    color: #6B7280;
    font-size: 14px;
}

/* Comment */
textarea {
    width: 100%;
    padding: 10px;
    border: 1px solid #E5E7EB;
    border-radius: 4px;
    font-family: inherit;
    font-size: 16px;
    resize: vertical;
}

textarea:focus {
    outline: none;
    border-color: #F97316;
    box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.1);
}

#char-count {
    display: block;
    margin-top: 5px;
    color: #6B7280;
    font-size: 12px;
}
```

### 5. JavaScript para Interação

#### `src/main/webapp/js/rating.js`

```javascript
/**
 * Interação com rating (estrelas).
 */

document.addEventListener('DOMContentLoaded', function() {
    // Rating stars
    const stars = document.querySelectorAll('.stars input[type="radio"]');
    const ratingDisplay = document.getElementById('rating-display');

    stars.forEach(star => {
        star.addEventListener('change', function() {
            const rating = this.value;
            ratingDisplay.textContent = `${rating}/5`;
        });
    });

    // Comment char count
    const comment = document.getElementById('comment');
    const charCount = document.getElementById('char-count');

    if (comment && charCount) {
        comment.addEventListener('input', function() {
            charCount.textContent = `${this.value.length} / 1000`;
        });
    }
});
```

### 6. Detalhe com Avaliação

Em `detail.jsp`, mostrar rating + comentário:

```jsp
<c:if test="${not empty item.rating}">
    <dt><fmt:message key="item.rating" /></dt>
    <dd>
        <span class="stars-display">
            <c:forEach var="i" begin="1" end="${item.rating}">★</c:forEach>
            <c:forEach var="i" begin="${item.rating + 1}" end="5">☆</c:forEach>
        </span>
        <c:out value="${item.rating}" /> / 5
    </dd>
</c:if>

<c:if test="${not empty item.comment}">
    <dt><fmt:message key="item.comment" /></dt>
    <dd><c:out value="${item.comment}" /></dd>
</c:if>
```

### 7. Testes

Atualizar `MediaItemValidatorTest`:

```java
@Test
@DisplayName("deve validar rating entre 0-5")
void testRatingValidRange() {
    MediaItem item = new MediaItem("Title", MediaType.MOVIE);
    item.setRating(3);
    assertDoesNotThrow(() -> validator.validate(item));
}

@Test
@DisplayName("deve rejeitar rating < 0")
void testRatingNegative() {
    MediaItem item = new MediaItem("Title", MediaType.MOVIE);
    item.setRating(-1);
    assertThrows(ValidationException.class, () -> validator.validate(item));
}

@Test
@DisplayName("deve rejeitar rating > 5")
void testRatingTooHigh() {
    MediaItem item = new MediaItem("Title", MediaType.MOVIE);
    item.setRating(6);
    assertThrows(ValidationException.class, () -> validator.validate(item));
}

@Test
@DisplayName("deve rejeitar comentário > 1000 chars")
void testCommentTooLong() {
    MediaItem item = new MediaItem("Title", MediaType.MOVIE);
    item.setComment("a".repeat(1001));
    assertThrows(ValidationException.class, () -> validator.validate(item));
}
```

---

## Travas

### 🔴 TRAVA 1: Rating Validado Server-Side
- Sempre revalidar no Service, mesmo se JS valida
- Campo pode ser burleado por curl/postman

### 🔴 TRAVA 2: Comentário Escapado em XSS
- `<c:out value="${item.comment}" />` em detail + list
- Nunca `${item.comment}` direto

### 🔴 TRAVA 3: Char Count Dinâmico
- JS atualiza "X / 1000" conforme usuário digita
- Feedback visual imediato

---

## Critérios de Aceite

- [ ] UI de estrelas (0-5, interativo)
- [ ] Campo comentário com char count
- [ ] Rating validado (0-5, server-side)
- [ ] Comentário validado (< 1000 chars)
- [ ] Persiste em DB (update)
- [ ] Exibe em detalhe com escape XSS
- [ ] Testes de validação
- [ ] `mvn clean verify` passa

---

## Próximos Passos (Após Todas as Tasks)

1. **Merge para release/pit-catalog**
   - PR de `delivery/core` → `release`
   - PRs de `delivery/tmdb`, `delivery/frontend-home`, `delivery/rating` → `release`

2. **Documentação Final**
   - Gerar Javadoc
   - PDF com diagramas (UML, DER)
   - Manual do usuário

3. **PR Final**
   - `release/pit-catalog` → `main` (tag v1.0)

---

**Versão 1.0 | 2026-08-12**
