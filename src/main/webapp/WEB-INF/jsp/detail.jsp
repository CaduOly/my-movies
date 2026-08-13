<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout pageTitle="${item.title}">
    <div class="detail-actions" style="margin-bottom: 20px;">
        <a href="<c:url value='/app/list' />" class="action-link" style="font-weight:bold; font-size:1.1rem;">
            &lt; <fmt:message key="app.back" />
        </a>
    </div>
    
    <hr style="border: 1px solid #E2E8F0; margin-bottom: 30px;" />

    <div class="detail-layout" style="display:flex; gap:30px; flex-wrap:wrap; margin-bottom:40px;">
        <!-- Lado Esquerdo: Poster e Avaliação -->
        <div class="detail-left" style="flex: 0 0 300px;">
            <div class="poster" style="margin-bottom:20px; border-radius:8px; overflow:hidden; box-shadow:0 4px 6px -1px rgba(0,0,0,0.1);">
                <c:choose>
                    <c:when test="${not empty item.posterUrl}">
                        <img src="<c:out value='${item.posterUrl}' />" alt="<c:out value='${item.title}' />" style="width:100%; display:block;" />
                    </c:when>
                    <c:otherwise>
                        <div style="width:100%; height:450px; background:#E2E8F0; display:flex; align-items:center; justify-content:center; color:#64748B;">Sem Capa</div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Formulário de Avaliação -->
            <div class="rating-section" style="background:var(--bg-secondary); padding:20px; border-radius:8px; box-shadow:0 1px 3px rgba(0,0,0,0.1);">
                <h3 style="margin-top:0;">Avaliar</h3>
                <form method="POST" action="<c:url value='/app/rate' />">
                    <input type="hidden" name="id" value="${item.id}" />
                    
                    <div class="star-rating" style="display:flex; flex-direction:row-reverse; justify-content:flex-end; font-size:2rem; margin-bottom:15px;">
                        <input type="radio" id="star5" name="rating" value="5" ${item.rating == 5 ? 'checked' : ''} style="display:none;" />
                        <label for="star5" style="color: ${item.rating >= 5 ? '#F59E0B' : '#CBD5E1'}; cursor:pointer;">&#9733;</label>
                        
                        <input type="radio" id="star4" name="rating" value="4" ${item.rating == 4 ? 'checked' : ''} style="display:none;" />
                        <label for="star4" style="color: ${item.rating >= 4 ? '#F59E0B' : '#CBD5E1'}; cursor:pointer;">&#9733;</label>
                        
                        <input type="radio" id="star3" name="rating" value="3" ${item.rating == 3 ? 'checked' : ''} style="display:none;" />
                        <label for="star3" style="color: ${item.rating >= 3 ? '#F59E0B' : '#CBD5E1'}; cursor:pointer;">&#9733;</label>
                        
                        <input type="radio" id="star2" name="rating" value="2" ${item.rating == 2 ? 'checked' : ''} style="display:none;" />
                        <label for="star2" style="color: ${item.rating >= 2 ? '#F59E0B' : '#CBD5E1'}; cursor:pointer;">&#9733;</label>
                        
                        <input type="radio" id="star1" name="rating" value="1" ${item.rating == 1 ? 'checked' : ''} style="display:none;" />
                        <label for="star1" style="color: ${item.rating >= 1 ? '#F59E0B' : '#CBD5E1'}; cursor:pointer;">&#9733;</label>
                    </div>

                    <div style="margin-bottom:15px;">
                        <textarea id="comment" name="comment" rows="3" placeholder="Deixe um comentário..." style="width:100%; border:1px solid #CBD5E1; border-radius:4px; padding:10px; font-family:inherit; resize:vertical;" maxlength="1000" onkeyup="document.getElementById('charCount').textContent = this.value.length + '/1000'"><c:out value="${item.comment}" /></textarea>
                        <div style="text-align:right; font-size:0.8rem; color:#64748B;" id="charCount">${empty item.comment ? 0 : item.comment.length()}/1000</div>
                    </div>
                    
                    <button type="submit" class="btn btn-primary" style="width:100%;">Salvar Avaliação</button>
                </form>
            </div>
        </div>

        <!-- Lado Direito: Detalhes -->
        <div class="detail-right" style="flex: 1; min-width:300px;">
            <h1 style="font-size: 2.5rem; color:var(--text-h1); margin-top:0; margin-bottom:10px;"><c:out value="${item.title}" /></h1>
            <hr style="border: 1px solid #F1F5F9; margin-bottom: 20px;" />
            
            <p style="font-size: 1.1rem; margin-bottom: 20px; color:var(--text-body);">
                <strong>Diretor:</strong> <c:out value="${empty item.authorDirector ? '-' : item.authorDirector}" /> &bull; 
                <strong>Ano:</strong> <c:out value="${empty item.releaseYear ? '-' : item.releaseYear}" /> &bull; 
                <strong>Gênero:</strong> <c:out value="${empty item.genre ? '-' : item.genre}" /> &bull;
                <strong>Tipo:</strong> <fmt:message key="type.${item.mediaType.toString().toLowerCase()}" />
            </p>
            
            <hr style="border: 1px solid #F1F5F9; margin-bottom: 20px;" />
            
            <h3 style="color:var(--text-h1);">Sinopse</h3>
            <p style="line-height: 1.8; color:var(--text-body); text-align:justify;">
                <c:choose>
                    <c:when test="${not empty item.synopsis}">
                        <c:out value="${item.synopsis}" />
                    </c:when>
                    <c:otherwise>
                        <em>Nenhuma sinopse disponível.</em>
                    </c:otherwise>
                </c:choose>
            </p>
        </div>
    </div>

    <div style="display:flex; justify-content:flex-end;">
        <a href="<c:url value='/app/edit?id=${item.id}' />" class="btn btn-secondary" style="padding:10px 20px;">
            <fmt:message key="app.edit" />
        </a>
    </div>

    <script>
    document.addEventListener('DOMContentLoaded', function() {
        const stars = document.querySelectorAll('.star-rating label');
        const radios = document.querySelectorAll('.star-rating input[type="radio"]');

        function updateStars(ratingValue) {
            stars.forEach((label, index) => {
                // Because of row-reverse, index 0 is star5, index 4 is star1.
                // Value goes from 5 down to 1.
                const valueOfStar = 5 - index;
                label.style.color = valueOfStar <= ratingValue ? '#F59E0B' : '#CBD5E1';
            });
        }

        stars.forEach(label => {
            label.addEventListener('click', function() {
                const radioId = this.getAttribute('for');
                const radio = document.getElementById(radioId);
                radio.checked = true;
                updateStars(radio.value);
            });
        });
    });
    </script>
</t:layout>
