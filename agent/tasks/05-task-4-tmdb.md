# Task 4: TMDB Autofill (Extensão)

**Entrega:** `delivery/tmdb`  
**Branch:** `feature/tmdb-search`  
**Estimativa:** 8 pontos  
**Prioridade:** 🟢 BONUS (após núcleo)  
**Depende:** Tasks 0-3 (núcleo completo)  
**Escopo:** Filmes/séries apenas (não livros)  
**Status:** Não iniciado

---

## Objetivo

Implementar **TmdbMetadataProvider** que busca metadados reais (TMDB API):
- Pôster, gênero, sinopse, créditos
- Autofill no formulário ao buscar por título
- Fallback gracioso se API indisponível
- Key fora do código (variável de ambiente)
- Atribuição TMDB obrigatória
- Timeout + retry

---

## Escopo

### 1. Provider TMDB

#### `com.seu.catalog.service.TmdbMetadataProvider`

```java
package com.seu.catalog.service;

import com.seu.catalog.model.MediaItem;
import com.seu.catalog.model.MediaType;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Logger;

/**
 * Provider que busca metadados em TMDB (The Movie Database).
 * Endpoints: /search/movie, /movie/{id}, /genre/movie/list
 * Timeout: 5s. Fallback: null (não quebra a app).
 */
public class TmdbMetadataProvider implements MovieMetadataProvider {
    private static final Logger LOG = Logger.getLogger(TmdbMetadataProvider.class.getName());
    private static final String BASE_URL = "https://api.themoviedb.org/3";
    private static final String API_KEY = System.getenv("TMDB_API_KEY");
    private static final int TIMEOUT_MS = 5000;
    private static final String POSTER_BASE = "https://image.tmdb.org/t/p/w500";

    public TmdbMetadataProvider() {
        if (API_KEY == null || API_KEY.isEmpty()) {
            LOG.warning("TMDB_API_KEY não configurada; provider em modo fallback");
        }
    }

    @Override
    public MediaItem searchByTitle(String term) {
        if (API_KEY == null) return null;

        try {
            String url = BASE_URL + "/search/movie?api_key=" + API_KEY + "&query=" + URLEncoder.encode(term, "UTF-8");
            // Implementar: fetch JSON, parse primeiro resultado, chamar findById
        } catch (Exception e) {
            LOG.warning("Erro ao buscar em TMDB: " + e.getMessage());
        }

        return null;
    }

    @Override
    public MediaItem findById(String externalId) {
        if (API_KEY == null) return null;

        try {
            String url = BASE_URL + "/movie/" + externalId + "?api_key=" + API_KEY + "&append_to_response=credits";
            // Implementar: fetch JSON, montar MediaItem com pôster, gênero, sinopse, diretor
        } catch (Exception e) {
            LOG.warning("Erro ao buscar filme por id: " + e.getMessage());
        }

        return null;
    }
}
```

**Checklist:**
- [ ] Implementa `MovieMetadataProvider`
- [ ] Busca em `/search/movie` e `/movie/{id}`
- [ ] API key fora do código (env var)
- [ ] Timeout 5s + fallback null
- [ ] Pôster, gênero, sinopse, diretor
- [ ] Dependency JSON (adicionar ao pom.xml)

### 2. Atualizar AppBootstrap para Injetar TMDB

```java
// Em Task 0, AppBootstrap injeta:
var metadataProvider = System.getenv("TMDB_API_KEY") != null
    ? new TmdbMetadataProvider()
    : new FakeMovieMetadataProvider();

var service = new CatalogService(dao, metadataProvider);
sce.getServletContext().setAttribute("catalogService", service);
```

### 3. Testes

```java
@Test
@DisplayName("deve buscar filme em TMDB")
void testSearchByTitle() {
    var provider = new TmdbMetadataProvider();
    var item = provider.searchByTitle("Inception");

    assertNotNull(item);
    assertEquals("Inception", item.getTitle());
    assertNotNull(item.getPosterUrl());
}

@Test
@DisplayName("deve retornar null se API key não configurada")
void testNoApiKey() {
    // Simular API_KEY = null
    // Provider deve retornar null, app continua funcionando
}

@Test
@DisplayName("deve timeout se TMDB indisponível (5s)")
void testTimeout() {
    // Usar timeout connection
    // Provider deve retornar null, não quebrar
}
```

---

## Travas

### 🔴 TRAVA 1: API Key Fora do Código
- ❌ `API_KEY = "sk_..."` no código
- ✅ `System.getenv("TMDB_API_KEY")`

### 🔴 TRAVA 2: Fallback sem Quebra
- Se API indisponível → retorna null
- App continua funcionando com dados locais

### 🔴 TRAVA 3: Atribuição TMDB Visível
- [ ] Footer ou about menciona "Powered by TMDB"
- [ ] Link para `themoviedb.org`

---

## Critérios de Aceite

- [ ] TmdbMetadataProvider busca filmes/séries (não livros)
- [ ] Retorna pôster, gênero, sinopse, diretor
- [ ] API key via env var (fallback: null)
- [ ] Timeout 5s, nenhuma quebra
- [ ] Testes (busca + fallback + timeout)
- [ ] AppBootstrap injeta correto provider
- [ ] Atribuição TMDB em UI (footer)
- [ ] `mvn clean verify` passa

---

**Versão 1.0 | 2026-08-12**
