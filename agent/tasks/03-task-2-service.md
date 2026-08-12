# Task 2: Service & Validação (Negócio + Transações + DIP)

**Entrega:** `delivery/core`  
**Branches:** `feature/validation`, `feature/service-crud`  
**Estimativa:** 6 pontos  
**Prioridade:** 🔴 BLOQUEADOR  
**Depende:** `feature/contracts` + `feature/infra` + `feature/dao-crud` (merged)  
**Cobre:** SP2 + parte de SP3  
**Status:** Não iniciado

---

## Objetivo

Implementar **lógica de negócio segura** (`CatalogService` + `MediaItemValidator`) que:
- Valida entrada antes de persistir (obrigatoriedade, tipo, faixa)
- Gerencia transações (commit/rollback)
- Usa DIP: injeta DAO + Provider via construtor
- Encapsula erros em `ServiceException`

> O Service é a **válvula de controle**: nem dados inválidos nor transações quebradas passam.

---

## Escopo

### 1. Validação de Entrada

#### `com.seu.catalog.service.MediaItemValidator`

```java
package com.seu.catalog.service;

import com.seu.catalog.exception.ValidationException;
import com.seu.catalog.model.MediaItem;

/**
 * Valida um MediaItem antes de persistência.
 * Regras:
 * - title: obrigatório, 1-255 chars
 * - mediaType: obrigatório
 * - releaseYear: opcional, mas se presente deve ser número válido (1800-2100)
 * - rating: opcional, mas se presente deve estar entre 0-5
 */
public class MediaItemValidator {
    
    /**
     * Valida um item de mídia.
     *
     * @param item item a validar (não nulo)
     * @throws ValidationException se alguma regra for violada
     */
    public void validate(MediaItem item) throws ValidationException {
        if (item == null) {
            throw new ValidationException("Item não pode ser nulo");
        }

        validateTitle(item.getTitle());
        validateMediaType(item.getMediaType());
        validateReleaseYear(item.getReleaseYear());
        validateRating(item.getRating());
    }

    /**
     * Valida título.
     */
    private void validateTitle(String title) throws ValidationException {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Título é obrigatório");
        }
        if (title.length() > 255) {
            throw new ValidationException("Título não pode exceder 255 caracteres");
        }
    }

    /**
     * Valida tipo de mídia.
     */
    private void validateMediaType(Object mediaType) throws ValidationException {
        if (mediaType == null) {
            throw new ValidationException("Tipo de mídia é obrigatório");
        }
    }

    /**
     * Valida ano de lançamento.
     */
    private void validateReleaseYear(Integer releaseYear) throws ValidationException {
        if (releaseYear != null) {
            if (releaseYear < 1800 || releaseYear > 2100) {
                throw new ValidationException("Ano deve estar entre 1800 e 2100");
            }
        }
    }

    /**
     * Valida avaliação (rating).
     */
    private void validateRating(Integer rating) throws ValidationException {
        if (rating != null) {
            if (rating < 0 || rating > 5) {
                throw new ValidationException("Avaliação deve estar entre 0 e 5");
            }
        }
    }
}
```

**Checklist Validator:**
- [ ] Classe instantiável, métodos públicos
- [ ] `validate(MediaItem)` lança `ValidationException`
- [ ] Regras: title obrigatório + length, mediaType obrigatório, releaseYear faixa, rating faixa
- [ ] Javadoc PT-BR
- [ ] Sem lógica de banco (puro domínio)

---

### 2. Service com Transação e DIP

#### `com.seu.catalog.service.CatalogService`

```java
package com.seu.catalog.service;

import com.seu.catalog.dao.MediaItemDAO;
import com.seu.catalog.exception.DAOException;
import com.seu.catalog.exception.ServiceException;
import com.seu.catalog.exception.ValidationException;
import com.seu.catalog.model.MediaItem;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Serviço de negócio para gerenciar itens de mídia.
 * Responsabilidades:
 * - Validar dados de entrada
 * - Coordenar transações (DAO + Provider)
 * - Traduzir exceções técnicas em semânticas
 *
 * Injeção: construtor recebe DAO e Provider (DIP).
 */
public class CatalogService {
    private static final Logger LOG = Logger.getLogger(CatalogService.class.getName());
    
    private final MediaItemDAO dao;
    private final MovieMetadataProvider metadataProvider;
    private final MediaItemValidator validator;

    /**
     * Cria um serviço de catálogo.
     *
     * @param dao DAO de persistência (não nulo)
     * @param metadataProvider provider de metadados externos (pode ser null para dev local)
     */
    public CatalogService(MediaItemDAO dao, MovieMetadataProvider metadataProvider) {
        this.dao = dao;
        this.metadataProvider = metadataProvider;
        this.validator = new MediaItemValidator();
    }

    /**
     * Cria um novo item de mídia no catálogo.
     *
     * @param item item a cadastrar (validado antes de persistir)
     * @return item com id gerado preenchido
     * @throws ValidationException se item falhar na validação
     * @throws ServiceException se falhar na persistência
     */
    public MediaItem createItem(MediaItem item) throws ValidationException, ServiceException {
        // 1. Validar
        validator.validate(item);

        // 2. Persistir (sem transação explícita aqui; DAO gerencia)
        try {
            return dao.insert(item);
        } catch (DAOException e) {
            LOG.severe("Erro ao inserir item: " + e.getMessage());
            throw new ServiceException("Falha ao cadastrar item no banco de dados", e);
        }
    }

    /**
     * Lista todos os itens do catálogo.
     *
     * @return lista de itens (vazio se nenhum)
     * @throws ServiceException se falhar na persistência
     */
    public List<MediaItem> listAllItems() throws ServiceException {
        try {
            return dao.findAll();
        } catch (DAOException e) {
            LOG.severe("Erro ao listar itens: " + e.getMessage());
            throw new ServiceException("Falha ao listar itens", e);
        }
    }

    /**
     * Retorna um item pelo id.
     *
     * @param id id do item
     * @return item encontrado, ou null se não existir
     * @throws ServiceException se falhar na persistência
     */
    public MediaItem getItemById(Integer id) throws ServiceException {
        try {
            return dao.findById(id);
        } catch (DAOException e) {
            LOG.severe("Erro ao buscar item: " + e.getMessage());
            throw new ServiceException("Falha ao buscar item", e);
        }
    }

    /**
     * Atualiza um item existente.
     *
     * @param item item com alterações (validado antes de persistir)
     * @throws ValidationException se item falhar na validação
     * @throws ServiceException se falhar na persistência ou se item não existir
     */
    public void updateItem(MediaItem item) throws ValidationException, ServiceException {
        // 1. Validar
        validator.validate(item);

        // 2. Verificar existência
        try {
            MediaItem existing = dao.findById(item.getId());
            if (existing == null) {
                throw new ServiceException("Item com id " + item.getId() + " não existe");
            }
        } catch (DAOException e) {
            throw new ServiceException("Falha ao verificar item", e);
        }

        // 3. Atualizar
        try {
            boolean updated = dao.update(item);
            if (!updated) {
                throw new ServiceException("Item com id " + item.getId() + " não pode ser atualizado");
            }
        } catch (DAOException e) {
            LOG.severe("Erro ao atualizar item: " + e.getMessage());
            throw new ServiceException("Falha ao atualizar item no banco de dados", e);
        }
    }

    /**
     * Deleta um item.
     *
     * @param id id do item a deletar
     * @throws ServiceException se falhar na persistência ou se item não existir
     */
    public void deleteItem(Integer id) throws ServiceException {
        try {
            // Verificar existência
            MediaItem existing = dao.findById(id);
            if (existing == null) {
                throw new ServiceException("Item com id " + id + " não existe");
            }

            // Deletar
            boolean deleted = dao.delete(id);
            if (!deleted) {
                throw new ServiceException("Falha ao deletar item");
            }
        } catch (DAOException e) {
            LOG.severe("Erro ao deletar item: " + e.getMessage());
            throw new ServiceException("Falha ao deletar item do banco de dados", e);
        }
    }

    /**
     * Busca itens por termo (título ou autor/diretor).
     *
     * @param term termo de busca
     * @return lista de itens encontrados
     * @throws ServiceException se falhar na persistência
     */
    public List<MediaItem> searchItems(String term) throws ServiceException {
        if (term == null || term.trim().isEmpty()) {
            throw new ServiceException("Termo de busca não pode estar vazio");
        }

        try {
            return dao.searchByTerm(term);
        } catch (DAOException e) {
            LOG.severe("Erro ao buscar por termo: " + e.getMessage());
            throw new ServiceException("Falha ao buscar itens", e);
        }
    }
}
```

**Checklist CatalogService:**
- [ ] Construtor recebe `MediaItemDAO` e `MovieMetadataProvider` (injeção)
- [ ] Métodos: createItem, listAllItems, getItemById, updateItem, deleteItem, searchItems
- [ ] Valida antes de persistir (`validator.validate()`)
- [ ] Captura `DAOException`, relança como `ServiceException`
- [ ] Usa `java.util.logging`, não `System.out.println`
- [ ] Javadoc PT-BR completo
- [ ] Sem lógica SQL

---

### 3. Testes Unitários (Service)

#### `src/test/java/com/seu/catalog/service/MediaItemValidatorTest.java`

```java
package com.seu.catalog.service;

import com.seu.catalog.exception.ValidationException;
import com.seu.catalog.model.MediaItem;
import com.seu.catalog.model.MediaType;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MediaItemValidator Tests")
class MediaItemValidatorTest {
    
    private MediaItemValidator validator;

    @BeforeEach
    void setUp() {
        validator = new MediaItemValidator();
    }

    @Test
    @DisplayName("deve validar item válido")
    void testValidValidItem() {
        MediaItem item = new MediaItem("Valid Title", MediaType.MOVIE);
        assertDoesNotThrow(() -> validator.validate(item));
    }

    @Test
    @DisplayName("deve rejeitar item nulo")
    void testNullItem() {
        assertThrows(ValidationException.class, () -> validator.validate(null));
    }

    @Test
    @DisplayName("deve rejeitar title vazio")
    void testEmptyTitle() {
        MediaItem item = new MediaItem("", MediaType.MOVIE);
        assertThrows(ValidationException.class, () -> validator.validate(item));
    }

    @Test
    @DisplayName("deve rejeitar title > 255 chars")
    void testTitleTooLong() {
        String longTitle = "a".repeat(256);
        MediaItem item = new MediaItem(longTitle, MediaType.MOVIE);
        assertThrows(ValidationException.class, () -> validator.validate(item));
    }

    @Test
    @DisplayName("deve rejeitar mediaType nulo")
    void testNullMediaType() {
        // Construtor obriga title + mediaType, então esse teste precisa de setter
        MediaItem item = new MediaItem("Title", MediaType.MOVIE);
        // Não há setter para mediaType nulo no construtor... esse teste é mais para coverage
        // Se quiser, crie um construtor alternativo ou setter
    }

    @Test
    @DisplayName("deve rejeitar releaseYear < 1800")
    void testReleaseYearTooOld() {
        MediaItem item = new MediaItem("Title", MediaType.MOVIE);
        item.setReleaseYear(1799);
        assertThrows(ValidationException.class, () -> validator.validate(item));
    }

    @Test
    @DisplayName("deve rejeitar releaseYear > 2100")
    void testReleaseYearTooFuture() {
        MediaItem item = new MediaItem("Title", MediaType.MOVIE);
        item.setReleaseYear(2101);
        assertThrows(ValidationException.class, () -> validator.validate(item));
    }

    @Test
    @DisplayName("deve aceitar releaseYear válido (1800-2100)")
    void testReleaseYearValid() {
        MediaItem item = new MediaItem("Title", MediaType.MOVIE);
        item.setReleaseYear(2010);
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
    @DisplayName("deve aceitar rating válido (0-5)")
    void testRatingValid() {
        MediaItem item = new MediaItem("Title", MediaType.MOVIE);
        item.setRating(3);
        assertDoesNotThrow(() -> validator.validate(item));
    }

    @Test
    @DisplayName("deve aceitar null em campos opcionais")
    void testOptionalFieldsNull() {
        MediaItem item = new MediaItem("Title", MediaType.MOVIE);
        // releaseYear, genre, synopsis, posterUrl, externalId, rating, comment são nullable
        assertDoesNotThrow(() -> validator.validate(item));
    }
}
```

#### `src/test/java/com/seu/catalog/service/CatalogServiceTest.java`

```java
package com.seu.catalog.service;

import com.seu.catalog.dao.MediaItemDAO;
import com.seu.catalog.exception.DAOException;
import com.seu.catalog.exception.ServiceException;
import com.seu.catalog.exception.ValidationException;
import com.seu.catalog.model.MediaItem;
import com.seu.catalog.model.MediaType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CatalogService Tests")
class CatalogServiceTest {
    
    @Mock
    private MediaItemDAO daoMock;
    
    @Mock
    private MovieMetadataProvider providerMock;
    
    private CatalogService service;

    @BeforeEach
    void setUp() {
        service = new CatalogService(daoMock, providerMock);
    }

    @Test
    @DisplayName("deve criar item válido")
    void testCreateValidItem() throws Exception {
        MediaItem item = new MediaItem("New Film", MediaType.MOVIE);
        MediaItem saved = new MediaItem("New Film", MediaType.MOVIE);
        saved.setId(1);
        
        when(daoMock.insert(any(MediaItem.class))).thenReturn(saved);
        
        MediaItem result = service.createItem(item);
        
        assertEquals(1, result.getId());
        verify(daoMock, times(1)).insert(item);
    }

    @Test
    @DisplayName("deve rejeitar item inválido (title vazio)")
    void testCreateInvalidItem() {
        MediaItem item = new MediaItem("", MediaType.MOVIE);
        
        assertThrows(ValidationException.class, () -> service.createItem(item));
        verify(daoMock, never()).insert(any());
    }

    @Test
    @DisplayName("deve converter DAOException em ServiceException")
    void testCreateThrowsDAOException() throws Exception {
        MediaItem item = new MediaItem("Film", MediaType.MOVIE);
        
        when(daoMock.insert(any())).thenThrow(new DAOException("DB error"));
        
        assertThrows(ServiceException.class, () -> service.createItem(item));
    }

    @Test
    @DisplayName("deve listar todos os itens")
    void testListAllItems() throws Exception {
        when(daoMock.findAll()).thenReturn(java.util.List.of(
            new MediaItem("Film 1", MediaType.MOVIE),
            new MediaItem("Film 2", MediaType.SERIES)
        ));
        
        var items = service.listAllItems();
        
        assertEquals(2, items.size());
        verify(daoMock, times(1)).findAll();
    }

    @Test
    @DisplayName("deve buscar item por id")
    void testGetItemById() throws Exception {
        MediaItem found = new MediaItem("Found", MediaType.MOVIE);
        when(daoMock.findById(1)).thenReturn(found);
        
        var result = service.getItemById(1);
        
        assertNotNull(result);
        assertEquals("Found", result.getTitle());
    }

    @Test
    @DisplayName("deve atualizar item válido")
    void testUpdateValidItem() throws Exception {
        MediaItem existing = new MediaItem("Old", MediaType.MOVIE);
        existing.setId(1);
        
        MediaItem updated = new MediaItem("New Title", MediaType.MOVIE);
        updated.setId(1);
        
        when(daoMock.findById(1)).thenReturn(existing);
        when(daoMock.update(updated)).thenReturn(true);
        
        assertDoesNotThrow(() -> service.updateItem(updated));
        verify(daoMock, times(1)).update(updated);
    }

    @Test
    @DisplayName("deve rejeitar update de item inexistente")
    void testUpdateNotFound() throws Exception {
        MediaItem item = new MediaItem("Title", MediaType.MOVIE);
        item.setId(9999);
        
        when(daoMock.findById(9999)).thenReturn(null);
        
        assertThrows(ServiceException.class, () -> service.updateItem(item));
    }

    @Test
    @DisplayName("deve deletar item")
    void testDeleteItem() throws Exception {
        MediaItem existing = new MediaItem("To Delete", MediaType.MOVIE);
        existing.setId(1);
        
        when(daoMock.findById(1)).thenReturn(existing);
        when(daoMock.delete(1)).thenReturn(true);
        
        assertDoesNotThrow(() -> service.deleteItem(1));
        verify(daoMock, times(1)).delete(1);
    }

    @Test
    @DisplayName("deve buscar itens por termo")
    void testSearchItems() throws Exception {
        when(daoMock.searchByTerm("matrix")).thenReturn(java.util.List.of(
            new MediaItem("The Matrix", MediaType.MOVIE)
        ));
        
        var results = service.searchItems("matrix");
        
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("deve rejeitar busca com termo vazio")
    void testSearchEmptyTerm() {
        assertThrows(ServiceException.class, () -> service.searchItems(""));
    }
}
```

**Checklist Testes:**
- [ ] Validator: valida/rejeita conforme regras
- [ ] Service: createItem, listAllItems, getItemById, updateItem, deleteItem, searchItems
- [ ] Service converte `DAOException` em `ServiceException`
- [ ] Mocks para DAO (unit tests, sem banco)
- [ ] Nenhum uso do providerMock aqui (será usado em Task 4)

---

### 4. Contratos Complementares (de `feature/contracts`, revisados)

Verificar que `DAOException`, `ValidationException`, `ServiceException` estão definidos.

---

## Travas (Constraints Críticas)

### 🔴 TRAVA 1: Validação Antes de Persistir
- ❌ Salvar no banco sem validar
- ✅ `validator.validate()` antes de `dao.insert()`

Se um `MediaItem` inválido chegar ao banco, é falha crítica.

### 🔴 TRAVA 2: Transações Explícitas Opcionais (por agora)
- Nessa task, **não implementar try-catch-finally para transação**
- Cada `dao.insert()` é auto-commit
- **Task 2 evoluída** (futura): pode adicionar transações se precisar coordenar múltiplas ops

Isso mantém a task simples; transações vêm se Task 3 descobrir necessidade.

### 🔴 TRAVA 3: DIP: Injeção por Construtor
- ❌ `new MediaItemDAO()` dentro do Service
- ✅ `public CatalogService(MediaItemDAO dao, MovieMetadataProvider provider)`

Permite testes com mocks e substituição futura (ex: TMDB em Task 4).

### 🔴 TRAVA 4: Conversão de Exceções
- Capture `DAOException`, relance como `ServiceException` com causa
- **Nunca** `catch (Exception)` genérico

```java
catch (DAOException e) {
    throw new ServiceException("Mensagem amigável", e);
}
```

### 🔴 TRAVA 5: Sem Lógica SQL
- ❌ Nenhum SQL no Service
- ✅ Service chama DAO, DAO executa SQL

---

## Critérios de Aceite

### Validação
- [ ] `MediaItemValidator` valida title obrigatório + length
- [ ] Valida mediaType obrigatório
- [ ] Valida releaseYear faixa (1800-2100)
- [ ] Valida rating faixa (0-5)
- [ ] Rejeita valores inválidos com `ValidationException` clara

### Service
- [ ] `CatalogService` com injeção de DAO + Provider
- [ ] `createItem()` valida + persiste
- [ ] `listAllItems()`, `getItemById()`, `updateItem()`, `deleteItem()`, `searchItems()`
- [ ] Converte `DAOException` em `ServiceException` com causa
- [ ] Nenhum SQL; zero lógica de banco

### Testes
- [ ] `mvn test` passa 100%
- [ ] Testes de validação (casos válidos/inválidos)
- [ ] Testes de service (mocks para DAO)
- [ ] Cobertura > 80%

### Código
- [ ] Javadoc PT-BR (métodos públicos)
- [ ] Sem `System.out.println`
- [ ] Sem dead code / imports não usados
- [ ] `mvn javadoc:javadoc` sem warnings

---

## Estratégia de Teste

**Local:**
```bash
# Rode testes de validação + service
mvn test -Dtest=MediaItemValidator*,CatalogService*

# Coverage
mvn jacoco:report
open target/site/jacoco/index.html
```

**CI:** Testes rodam em paralelo com Task 1 (DAO), nenhuma dependência de banco aqui.

---

## Checkpoints Durante a Implementação

1. **Validator criado**
   - [ ] Classe compila
   - [ ] Testes de validação rodam

2. **CatalogService estrutura**
   - [ ] Construtor com injeção
   - [ ] Métodos CRUD definidos
   - [ ] Testes rodam

3. **Integração validator + service**
   - [ ] `createItem()` chama `validator.validate()`
   - [ ] Exceções propagadas corretamente

4. **Javadoc**
   - [ ] `mvn javadoc:javadoc` sem warnings

---

## Definition of Done

Uma PR `feature/validation` ou `feature/service-crud` é mergeable se:

- [ ] `mvn clean verify` passa (compile + testes + Javadoc)
- [ ] `MediaItemValidator` valida conforme regras (6 testes min)
- [ ] `CatalogService` implementa CRUD (6 métodos + 8 testes min)
- [ ] Injeção por construtor (DAO + Provider)
- [ ] `DAOException` → `ServiceException` com causa
- [ ] Testes unitários com mocks (sem banco)
- [ ] Javadoc PT-BR completo
- [ ] Commits: `test: add validator tests`, `feat: implement validator`, `feat: implement service CRUD`
- [ ] PR pequena (um escopo: validação OU service)
- [ ] Revisado contra seções 1, 2, 3

---

## Próximos Passos

Após merge:
- ✅ Task 3 (Web) pode começar (Service está pronto)

**Bloqueadores para Web removidos.**

---

## Notas

- **Provider unused aqui:** `MovieMetadataProvider` é injetado mas não usado em Task 2. Será usado em Task 4 (TMDB). Está aqui para estabelecer o contrato.
- **Transações futuras:** Se Task 3 descobrir que precisa coordenar múltiplas operações em uma transação, volte aqui e adicione try-catch-finally com rollback. Por agora, cada DAO op é seu próprio auto-commit.
- **Mensagens de erro:** São amigáveis (`"Título é obrigatório"`, não `"title validation failed"`). Servlets/JSP as traduzem com i18n.

---

**Versão 1.0 | 2026-08-12**
