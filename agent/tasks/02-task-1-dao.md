# Task 1: Foundation DAO (CRUD Seguro + Search Anti-Injection)

**Entrega:** `delivery/core`  
**Branches:** `feature/dao-crud`, `feature/dao-search`  
**Estimativa:** 8 pontos  
**Prioridade:** 🔴 BLOQUEADOR  
**Depende:** `feature/contracts` + `feature/infra` (merged)  
**Cobre:** SP1 (SQL Injection) + parte de SP3  
**Status:** Não iniciado

---

## Objetivo

Implementar **DAO seguro** (`MySqlMediaItemDAO`) que:
- Executa CRUD (create, read, update, delete) sem SQL Injection
- Busca parametrizada por termo
- Testes de integração contra banco real (Testcontainers ou schema dedicado)
- **ZERO concatenação em SQL**

> A segurança **começa aqui**. Este é o teste mais importante do projeto.

---

## Escopo

### 1. Implementação: `MySqlMediaItemDAO`

#### `com.seu.catalog.dao.MySqlMediaItemDAO`

```java
package com.seu.catalog.dao;

import com.seu.catalog.infra.ConnectionFactory;
import com.seu.catalog.model.MediaItem;
import com.seu.catalog.model.MediaType;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Implementação MySQL de MediaItemDAO.
 * Usa JDBC puro com PreparedStatement para evitar SQL Injection.
 */
public class MySqlMediaItemDAO implements MediaItemDAO {
    private static final Logger LOG = Logger.getLogger(MySqlMediaItemDAO.class.getName());

    /**
     * Insere um novo item de mídia.
     *
     * @param item item a persistir; não pode ser nulo
     * @return item com id preenchido (gerado pelo banco)
     * @throws DAOException se ocorrer erro de persistência
     */
    @Override
    public MediaItem insert(MediaItem item) throws DAOException {
        String sql = "INSERT INTO item_media (title, author_director, release_year, genre, "
                   + "synopsis, media_type, poster_url, external_id, rating, comment) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.get();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, item.getTitle());
            stmt.setString(2, item.getAuthorDirector());
            stmt.setObject(3, item.getReleaseYear()); // setObject para Int nullable
            stmt.setString(4, item.getGenre());
            stmt.setString(5, item.getSynopsis());
            stmt.setString(6, item.getMediaType().toString());
            stmt.setString(7, item.getPosterUrl());
            stmt.setString(8, item.getExternalId());
            stmt.setObject(9, item.getRating());
            stmt.setString(10, item.getComment());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    item.setId(rs.getInt(1));
                }
            }
            
            return item;
            
        } catch (SQLException e) {
            throw new DAOException("Falha ao inserir item", e);
        }
    }

    /**
     * Retorna todos os itens cadastrados.
     *
     * @return lista de itens (vazio se nenhum)
     * @throws DAOException se ocorrer erro de persistência
     */
    @Override
    public List<MediaItem> findAll() throws DAOException {
        String sql = "SELECT * FROM item_media ORDER BY title ASC";
        List<MediaItem> items = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.get();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                items.add(rowToMediaItem(rs));
            }
            
            return items;
            
        } catch (SQLException e) {
            throw new DAOException("Falha ao listar itens", e);
        }
    }

    /**
     * Retorna um item pelo id.
     *
     * @param id id do item
     * @return item encontrado, ou null se não existir
     * @throws DAOException se ocorrer erro de persistência
     */
    @Override
    public MediaItem findById(Integer id) throws DAOException {
        String sql = "SELECT * FROM item_media WHERE id = ?";
        
        try (Connection conn = ConnectionFactory.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rowToMediaItem(rs);
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            throw new DAOException("Falha ao buscar item por id", e);
        }
    }

    /**
     * Atualiza um item existente.
     *
     * @param item item com alterações; deve ter id preenchido
     * @return true se foi atualizado, false se id não existe
     * @throws DAOException se ocorrer erro de persistência
     */
    @Override
    public boolean update(MediaItem item) throws DAOException {
        String sql = "UPDATE item_media SET title = ?, author_director = ?, release_year = ?, "
                   + "genre = ?, synopsis = ?, media_type = ?, poster_url = ?, external_id = ?, "
                   + "rating = ?, comment = ? WHERE id = ?";
        
        try (Connection conn = ConnectionFactory.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, item.getTitle());
            stmt.setString(2, item.getAuthorDirector());
            stmt.setObject(3, item.getReleaseYear());
            stmt.setString(4, item.getGenre());
            stmt.setString(5, item.getSynopsis());
            stmt.setString(6, item.getMediaType().toString());
            stmt.setString(7, item.getPosterUrl());
            stmt.setString(8, item.getExternalId());
            stmt.setObject(9, item.getRating());
            stmt.setString(10, item.getComment());
            stmt.setInt(11, item.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            throw new DAOException("Falha ao atualizar item", e);
        }
    }

    /**
     * Deleta um item pelo id.
     *
     * @param id id do item a deletar
     * @return true se foi deletado, false se id não existe
     * @throws DAOException se ocorrer erro de persistência
     */
    @Override
    public boolean delete(Integer id) throws DAOException {
        String sql = "DELETE FROM item_media WHERE id = ?";
        
        try (Connection conn = ConnectionFactory.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            throw new DAOException("Falha ao deletar item", e);
        }
    }

    /**
     * Busca itens por termo (título ou autor/diretor).
     * Entrada é tratada como DADO, nunca como SQL.
     *
     * @param term termo de busca (não nulo)
     * @return lista de itens encontrados (vazio se nenhum)
     * @throws DAOException se ocorrer erro de persistência
     */
    @Override
    public List<MediaItem> searchByTerm(String term) throws DAOException {
        String sql = "SELECT * FROM item_media WHERE title LIKE ? OR author_director LIKE ? ORDER BY title ASC";
        List<MediaItem> items = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // CHAVE: term é passado como DADO, não como SQL
            // O '%' é concatenado na STRING, não no SQL
            String like = "%" + term + "%";
            stmt.setString(1, like);
            stmt.setString(2, like);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(rowToMediaItem(rs));
                }
            }
            
            return items;
            
        } catch (SQLException e) {
            throw new DAOException("Falha ao buscar por termo", e);
        }
    }

    /**
     * Converte uma linha de ResultSet em MediaItem.
     * Métodos privados podem ter Javadoc reduzido ou omitido.
     */
    private MediaItem rowToMediaItem(ResultSet rs) throws SQLException {
        MediaItem item = new MediaItem(rs.getString("title"), MediaType.valueOf(rs.getString("media_type")));
        item.setId(rs.getInt("id"));
        item.setAuthorDirector(rs.getString("author_director"));
        item.setReleaseYear((Integer) rs.getObject("release_year"));
        item.setGenre(rs.getString("genre"));
        item.setSynopsis(rs.getString("synopsis"));
        item.setPosterUrl(rs.getString("poster_url"));
        item.setExternalId(rs.getString("external_id"));
        item.setRating((Integer) rs.getObject("rating"));
        item.setComment(rs.getString("comment"));
        return item;
    }
}
```

**Checklist `MySqlMediaItemDAO`:**
- [ ] Implementa `MediaItemDAO`
- [ ] Todos os métodos usam `PreparedStatement`
- [ ] ZERO concatenação de entrada em SQL
- [ ] `setString()` para strings, `setInt()` para ints, `setObject()` para nullable
- [ ] `try-with-resources` em toda operação
- [ ] `RETURN_GENERATED_KEYS` no insert para recuperar id auto-gerado
- [ ] Javadoc PT-BR em métodos públicos
- [ ] Sem `System.out.println`; sem logs desnecessários

---

### 2. Testes de Integração

#### `src/test/java/com/seu/catalog/dao/MySqlMediaItemDAOTest.java`

```java
package com.seu.catalog.dao;

import com.seu.catalog.model.MediaItem;
import com.seu.catalog.model.MediaType;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração do DAO contra banco real.
 * Usa banco de teste (Flyway migra schema de teste antes dos testes).
 */
@DisplayName("MySqlMediaItemDAO Integration Tests")
class MySqlMediaItemDAOTest {
    
    private MediaItemDAO dao;

    @BeforeEach
    void setUp() throws Exception {
        // Limpa e migra schema de teste
        // (em CI, usa schema dedicado; localmente, pode usar Testcontainers)
        dao = new MySqlMediaItemDAO();
        
        // Limpa tabela antes de cada teste
        try (var conn = com.seu.catalog.infra.ConnectionFactory.get();
             var stmt = conn.createStatement()) {
            stmt.execute("TRUNCATE TABLE item_media");
        }
    }

    @Test
    @DisplayName("deve inserir item com sucesso e retornar id gerado")
    void testInsert() throws Exception {
        MediaItem item = new MediaItem("Inception", MediaType.MOVIE);
        item.setAuthorDirector("Christopher Nolan");
        item.setReleaseYear(2010);
        
        MediaItem saved = dao.insert(item);
        
        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);
        assertEquals("Inception", saved.getTitle());
    }

    @Test
    @DisplayName("deve listar todos os itens")
    void testFindAll() throws Exception {
        dao.insert(new MediaItem("Film 1", MediaType.MOVIE));
        dao.insert(new MediaItem("Film 2", MediaType.SERIES));
        
        var items = dao.findAll();
        
        assertEquals(2, items.size());
    }

    @Test
    @DisplayName("deve encontrar item por id")
    void testFindById() throws Exception {
        MediaItem item = new MediaItem("The Matrix", MediaType.MOVIE);
        MediaItem saved = dao.insert(item);
        
        MediaItem found = dao.findById(saved.getId());
        
        assertNotNull(found);
        assertEquals("The Matrix", found.getTitle());
    }

    @Test
    @DisplayName("deve retornar null se id não existe")
    void testFindByIdNotFound() throws Exception {
        MediaItem found = dao.findById(9999);
        assertNull(found);
    }

    @Test
    @DisplayName("deve atualizar item")
    void testUpdate() throws Exception {
        MediaItem item = new MediaItem("Title 1", MediaType.MOVIE);
        MediaItem saved = dao.insert(item);
        
        saved.setTitle("Title Updated");
        boolean updated = dao.update(saved);
        
        assertTrue(updated);
        MediaItem reloaded = dao.findById(saved.getId());
        assertEquals("Title Updated", reloaded.getTitle());
    }

    @Test
    @DisplayName("deve retornar false se id não existe no update")
    void testUpdateNotFound() throws Exception {
        MediaItem item = new MediaItem("Title", MediaType.MOVIE);
        item.setId(9999);
        
        boolean updated = dao.update(item);
        assertFalse(updated);
    }

    @Test
    @DisplayName("deve deletar item")
    void testDelete() throws Exception {
        MediaItem item = new MediaItem("To Delete", MediaType.MOVIE);
        MediaItem saved = dao.insert(item);
        
        boolean deleted = dao.delete(saved.getId());
        
        assertTrue(deleted);
        assertNull(dao.findById(saved.getId()));
    }

    @Test
    @DisplayName("deve retornar false se id não existe no delete")
    void testDeleteNotFound() throws Exception {
        boolean deleted = dao.delete(9999);
        assertFalse(deleted);
    }

    @Test
    @DisplayName("deve buscar por termo com LIKE")
    void testSearchByTerm() throws Exception {
        dao.insert(new MediaItem("Inception", MediaType.MOVIE));
        dao.insert(new MediaItem("The Matrix", MediaType.MOVIE));
        dao.insert(new MediaItem("Breaking Bad", MediaType.SERIES));
        
        var results = dao.searchByTerm("matrix");
        
        assertEquals(1, results.size());
        assertEquals("The Matrix", results.get(0).getTitle());
    }

    @Test
    @DisplayName("deve buscar case-insensitive")
    void testSearchCaseInsensitive() throws Exception {
        dao.insert(new MediaItem("Inception", MediaType.MOVIE));
        
        var results = dao.searchByTerm("INCEPTION");
        
        assertEquals(1, results.size());
    }

    /**
     * TESTE CRÍTICO: SQL Injection.
     * Entrada maliciosa não deve executar SQL; deve ser tratada como texto literal.
     */
    @Test
    @DisplayName("SQL Injection: DELETE statement injected deve falhar silenciosamente")
    void testSearchInjectionDelete() throws Exception {
        // Insira dados válidos
        dao.insert(new MediaItem("Valid Item", MediaType.MOVIE));
        
        // Tente injetar DELETE
        String malicious = "'; DROP TABLE item_media; --";
        var results = dao.searchByTerm(malicious);
        
        // Deve retornar vazio, não deletar tabela
        assertTrue(results.isEmpty());
        
        // Verifique que dados ainda existem
        var allItems = dao.findAll();
        assertEquals(1, allItems.size(), "Tabela não foi deletada");
    }

    /**
     * TESTE CRÍTICO: SQL Injection em outro vetor.
     */
    @Test
    @DisplayName("SQL Injection: UNION attack deve retornar vazio")
    void testSearchInjectionUnion() throws Exception {
        dao.insert(new MediaItem("Real Item", MediaType.MOVIE));
        
        String malicious = "' UNION SELECT 1,2,3,4,5,6,7,8,9,10 --";
        var results = dao.searchByTerm(malicious);
        
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("deve buscar por autor/diretor também")
    void testSearchByAuthor() throws Exception {
        MediaItem item = new MediaItem("Film", MediaType.MOVIE);
        item.setAuthorDirector("Nolan");
        dao.insert(item);
        
        var results = dao.searchByTerm("nolan");
        
        assertEquals(1, results.size());
    }
}
```

**Checklist dos Testes:**
- [ ] Testes de CRUD básico (insert, findAll, findById, update, delete)
- [ ] Testes de casos "not found"
- [ ] **Testes de SQL Injection** (`DROP TABLE`, `UNION`, etc.)
- [ ] Testes case-insensitive na busca
- [ ] Testes buscam por título E autor/diretor
- [ ] Setup limpa tabela antes de cada teste
- [ ] Sem mock; testes contra banco real

---

### 3. Migrações (complemento da Task 0)

Revisar `V1__create_item_media.sql` (de Task 0):
- [ ] Charset `utf8mb4` ✓
- [ ] Índices em `title` e `author_director` ✓
- [ ] Coluna `media_type` VARCHAR (enum em Java) ✓
- [ ] Campos ext nullable ✓

---

## Travas (Constraints Críticas)

### 🔴 TRAVA 1: ZERO Concatenação em SQL
- ❌ `String sql = "SELECT ... WHERE id = " + id;`
- ❌ `String like = "SELECT ... WHERE title LIKE '%" + term + "%'";`
- ✅ `PreparedStatement` com `setString()`, `setInt()`, etc.

**Verificação:** Code review linha por linha, busque por `+` em variáveis SQL.

### 🔴 TRAVA 2: Banco de Teste Isolado
- ❌ Testes não podem rodar contra produção
- ✅ Schema de teste ou Testcontainers

**CI:** Use variável de ambiente `JDBC_URL_TEST` ou container efêmero.

### 🔴 TRAVA 3: Try-with-Resources Obrigatório
- Toda `Connection`, `Statement`, `ResultSet` em try-with-resources
- Garante limpeza mesmo em exception

```java
try (Connection conn = ConnectionFactory.get();
     PreparedStatement stmt = conn.prepareStatement(sql)) {
    // use
} // auto-close
```

### 🔴 TRAVA 4: Nomes de Colunas = Snake_case em SQL, camelCase em Java
- Banco: `author_director`, `release_year`, `media_type`
- Java: `authorDirector`, `releaseYear`, `mediaType`

Mapeamento está em `rowToMediaItem()`.

### 🔴 TRAVA 5: Teste de Injection é DEVE-TER
Se essa tarefa não tiver `testSearchInjectionDelete()` ou similar, é motivo para rejeição.

---

## Critérios de Aceite

### Funcionalidade
- [ ] CRUD completo: insert, findAll, findById, update, delete
- [ ] Search com busca em 2 colunas (title, author_director)
- [ ] Nenhum SQL concatenado
- [ ] Tratamento de valores nullable (release_year, rating, etc.)

### Testes
- [ ] `mvn test` passa 100%
- [ ] Testes de CRUD
- [ ] **Testes de SQL Injection** (DROP TABLE, UNION, etc.)
- [ ] Case-insensitive em busca
- [ ] Testes contra banco real (não mock)

### Código
- [ ] `MySqlMediaItemDAO` implementa `MediaItemDAO`
- [ ] Todas as operações usam `PreparedStatement`
- [ ] `try-with-resources` em toda conn/statement
- [ ] Javadoc PT-BR (métodos públicos)
- [ ] Sem `System.out.println`

### Build
- [ ] `mvn clean verify` passa
- [ ] `mvn javadoc:javadoc` sem warnings

---

## Estratégia de Teste

**Local:**
```bash
# 1. Start banco (Task 0)
docker compose up -d db

# 2. Rode testes
mvn test -Dtest=MySqlMediaItemDAOTest

# 3. Verifique coverage
mvn jacoco:report
```

**CI:**
- Testes rodam contra banco efêmero (Testcontainers ou schema temporário)
- Cleanup automático após testes

---

## Checkpoints Durante a Implementação

1. **Esqueleto DAO**
   - [ ] Classe criada, implementa interface
   - [ ] Compila

2. **CRUD básico**
   - [ ] Insert, findAll, findById compilam
   - [ ] Testes rodam (mesmo se falham)

3. **Update + Delete**
   - [ ] Métodos compilam
   - [ ] Retornam boolean correto

4. **Search**
   - [ ] SearchByTerm com LIKE
   - [ ] Testes rodam

5. **Injection tests**
   - [ ] Testes de DROP TABLE, UNION
   - [ ] Passam (confirma segurança)

6. **Javadoc + cleanup**
   - [ ] `mvn javadoc:javadoc` sem warnings
   - [ ] Sem dead code

---

## Definition of Done

Uma PR `feature/dao-*` é mergeable se:

- [ ] `mvn clean verify` passa (compile + testes + Javadoc)
- [ ] CRUD: insert, findAll, findById, update, delete com 100% cobertura
- [ ] Search: searchByTerm buscando em 2 colunas, case-insensitive
- [ ] **SQL Injection tests:** `DROP TABLE`, `UNION`, etc. retornam vazio
- [ ] `MySqlMediaItemDAO` implements `MediaItemDAO` (contrato)
- [ ] Try-with-resources em toda operação
- [ ] Javadoc PT-BR completo (métodos públicos, sem warnings)
- [ ] Commits claros: `test: add DAO integration tests`, `feat: implement CRUD operations`, `feat: implement search with injection protection`
- [ ] PR pequena (um escopo: CRUD ou Search)
- [ ] Revisado contra seções 1 (Inegociáveis), 2 (Proibições), 3 (Javadoc), 4 (TDD)

---

## Próximos Passos

Após merge:
- ✅ Task 2 (Service) pode começar (DAO está pronto)
- ✅ Task 3 (Web) pode começar (contratos + infra prontos)

**Bloqueadores removidos.**

---

## Notas

- **Testcontainers:** Se quiser Testcontainers (MySQL efêmero), adicione dependência e `@Testcontainers`. Mais complexo, mas isolado.
- **Índices:** Índices em `title` e `author_director` melhoram performance de search. Estão em V1 (Task 0).
- **Versionamento de migrations:** Se descobrir erro em V1 durante Task 1, crie V3 (não edite V1). Flyway só executa forward.

---

**Versão 1.0 | 2026-08-12**
