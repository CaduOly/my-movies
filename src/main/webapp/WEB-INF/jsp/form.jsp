<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout pageTitle="${isEdit ? 'Editar Mídia' : 'Novo Item'}">
    <h2><c:out value="${isEdit ? 'Editar Mídia' : 'Novo Item'}" /></h2>

    <form method="POST" action="<c:url value='${isEdit ? \"/app/update\" : \"/app/save\"}' />" class="item-form">
        
        <c:if test="${isEdit}">
            <input type="hidden" name="id" value="<c:out value='${item.id}' />" />
        </c:if>

        <div class="form-group" style="position:relative;">
            <label for="title"><fmt:message key="item.title" /> *</label>
            <div style="display:flex; gap:10px;">
                <input type="text" id="title" name="title" value="<c:out value='${item.title}' />" required maxlength="200" style="flex:1;" />
                <button type="button" class="btn btn-secondary" onclick="searchTmdb()">Buscar TMDB</button>
            </div>
            <!-- Dropdown de Sugestões -->
            <div id="tmdbDropdown" style="display:none; position:absolute; top:100%; left:0; width:calc(100% - 130px); background:#ffffff; border:1px solid #ccc; border-radius:4px; box-shadow:0 4px 12px rgba(0,0,0,0.3); z-index:9999; margin-top:4px; overflow:hidden;">
                <!-- O conteúdo da lista será gerado pelo JS -->
            </div>
        </div>

        <div class="form-group">
            <label for="mediaType"><fmt:message key="item.mediaType" /> *</label>
            <select id="mediaType" name="mediaType" required>
                <option value=""><fmt:message key="app.select" /></option>
                <option value="MOVIE" ${item.mediaType == 'MOVIE' ? 'selected' : ''}>
                    <fmt:message key="type.movie" />
                </option>
                <option value="SERIES" ${item.mediaType == 'SERIES' ? 'selected' : ''}>
                    <fmt:message key="type.series" />
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

<script>
let currentTmdbData = [];

function searchTmdb() {
    var title = document.getElementById("title").value;
    if (!title) {
        alert("Digite um título primeiro!");
        return;
    }
    
    const dropdown = document.getElementById("tmdbDropdown");
    dropdown.style.display = "block";
    dropdown.innerHTML = "<div style='padding:15px; color:var(--text-body);'>Buscando...</div>";
    
    fetch("<c:url value='/app/tmdb-search' />?term=" + encodeURIComponent(title))
        .then(response => response.json())
        .then(data => {
            if (!Array.isArray(data) || data.length === 0) {
                dropdown.innerHTML = "<div style='padding:15px; color:var(--text-body);'>Nenhum resultado encontrado.</div>";
                setTimeout(() => { dropdown.style.display = "none"; }, 2500);
                return;
            }
            
            currentTmdbData = data;
            
            let html = "";
            for (let i = 0; i < data.length; i++) {
                const item = data[i];
                const posterSrc = item.posterUrl ? item.posterUrl : '';
                const posterHtml = posterSrc ? '<img src="' + posterSrc + '" style="width:40px; height:60px; object-fit:cover; border-radius:4px;" />' : '<div style="width:40px; height:60px; background:#eee; border-radius:4px;"></div>';
                const displayTitle = item.title || title;
                const displayYear = item.releaseYear ? ' (' + item.releaseYear + ')' : "";
                
                html += 
                    '<div onclick="acceptSuggestion(' + i + ')" style="display:flex; gap:12px; padding:10px; cursor:pointer; align-items:center; transition:background 0.2s; border-bottom:1px solid #eee;" onmouseover="this.style.background=\'#f0f0f0\'" onmouseout="this.style.background=\'transparent\'">' +
                        posterHtml +
                        '<div style="flex:1;">' +
                            '<div style="font-weight:bold; color:#333; font-size:1rem;">' + displayTitle + displayYear + '</div>' +
                            '<div style="font-size:0.85rem; color:#666; margin-top:4px;">Clique para preencher</div>' +
                        '</div>' +
                    '</div>';
            }
            dropdown.innerHTML = html;
        })
        .catch(err => {
            console.error(err);
            dropdown.innerHTML = "<div style='padding:15px; color:red;'>Erro ao consultar TMDB.</div>";
            setTimeout(() => { dropdown.style.display = "none"; }, 2500);
        });
}

function acceptSuggestion(index) {
    if (!currentTmdbData || !currentTmdbData[index]) return;
    
    const selected = currentTmdbData[index];
    if (selected.title) document.getElementById("title").value = selected.title;
    if (selected.releaseYear) document.getElementById("releaseYear").value = selected.releaseYear;
    if (selected.genre) document.getElementById("genre").value = selected.genre;
    if (selected.authorDirector) document.getElementById("authorDirector").value = selected.authorDirector;
    if (selected.synopsis) document.getElementById("synopsis").value = selected.synopsis;
    if (selected.posterUrl) document.getElementById("posterUrl").value = selected.posterUrl;
    document.getElementById("mediaType").value = selected.mediaType || "MOVIE";
    
    document.getElementById("tmdbDropdown").style.display = "none";
}

// Fechar o dropdown se clicar fora
document.addEventListener("click", function(event) {
    const dropdown = document.getElementById("tmdbDropdown");
    const titleInput = document.getElementById("title");
    const btn = document.querySelector("button[onclick='searchTmdb()']");
    if (dropdown.style.display === "block") {
        if (!dropdown.contains(event.target) && event.target !== titleInput && event.target !== btn) {
            dropdown.style.display = "none";
        }
    }
});
</script>

</t:layout>
