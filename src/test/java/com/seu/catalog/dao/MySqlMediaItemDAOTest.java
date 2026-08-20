package com.seu.catalog.dao;

import com.seu.catalog.model.MediaItem;
import com.seu.catalog.model.MediaType;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.flywaydb.core.Flyway;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração do DAO contra banco real.
 * Usa banco de teste (Flyway migra schema de teste antes dos testes).
 */
@Testcontainers
@DisplayName("MySqlMediaItemDAO Integration Tests")
class MySqlMediaItemDAOTest {
    
    @Container
    private static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("my_movies")
            .withUsername("app")
            .withPassword("app123");

    @BeforeAll
    static void initDb() {
        com.seu.catalog.infra.ConnectionFactory.setForTests(
                mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword());
        
        Flyway.configure()
                .dataSource(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword())
                .load()
                .migrate();
    }
    
    private MediaItemDAO dao;

    @BeforeEach
    void setUp() throws Exception {
        dao = new MySqlMediaItemDAO();
        
        try (var conn = com.seu.catalog.infra.ConnectionFactory.get();
             var stmt = conn.createStatement()) {
            stmt.execute("TRUNCATE TABLE item_media");
        }
    }

    @Test
    @DisplayName("deve inserir item com sucesso e retornar id gerado")
    void testInsert() throws Exception {
        MediaItem item = new MediaItem() {{ setTitle("Inception"); setMediaType(MediaType.MOVIE); }};
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
        dao.insert(new MediaItem() {{ setTitle("Film 1"); setMediaType(MediaType.MOVIE); }});
        dao.insert(new MediaItem() {{ setTitle("Film 2"); setMediaType(MediaType.SERIES); }});
        
        var items = dao.findAll();
        
        assertEquals(2, items.size());
    }

    @Test
    @DisplayName("deve encontrar item por id")
    void testFindById() throws Exception {
        MediaItem item = new MediaItem() {{ setTitle("The Matrix"); setMediaType(MediaType.MOVIE); }};
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
        MediaItem item = new MediaItem() {{ setTitle("Title 1"); setMediaType(MediaType.MOVIE); }};
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
        MediaItem item = new MediaItem() {{ setTitle("Title"); setMediaType(MediaType.MOVIE); }};
        item.setId(9999);
        
        boolean updated = dao.update(item);
        assertFalse(updated);
    }

    @Test
    @DisplayName("deve deletar item")
    void testDelete() throws Exception {
        MediaItem item = new MediaItem() {{ setTitle("To Delete"); setMediaType(MediaType.MOVIE); }};
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
        dao.insert(new MediaItem() {{ setTitle("Inception"); setMediaType(MediaType.MOVIE); }});
        dao.insert(new MediaItem() {{ setTitle("The Matrix"); setMediaType(MediaType.MOVIE); }});
        dao.insert(new MediaItem() {{ setTitle("Breaking Bad"); setMediaType(MediaType.SERIES); }});
        
        var results = dao.searchByTerm("matrix");
        
        assertEquals(1, results.size());
        assertEquals("The Matrix", results.get(0).getTitle());
    }

    @Test
    @DisplayName("deve buscar case-insensitive")
    void testSearchCaseInsensitive() throws Exception {
        dao.insert(new MediaItem() {{ setTitle("Inception"); setMediaType(MediaType.MOVIE); }});
        
        var results = dao.searchByTerm("INCEPTION");
        
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("SQL Injection: DELETE statement injected deve falhar silenciosamente")
    void testSearchInjectionDelete() throws Exception {
        dao.insert(new MediaItem() {{ setTitle("Valid Item"); setMediaType(MediaType.MOVIE); }});
        
        String malicious = "'; DROP TABLE item_media; --";
        var results = dao.searchByTerm(malicious);
        
        assertTrue(results.isEmpty());
        
        var allItems = dao.findAll();
        assertEquals(1, allItems.size(), "Tabela não foi deletada");
    }

    @Test
    @DisplayName("SQL Injection: UNION attack deve retornar vazio")
    void testSearchInjectionUnion() throws Exception {
        dao.insert(new MediaItem() {{ setTitle("Real Item"); setMediaType(MediaType.MOVIE); }});
        
        String malicious = "' UNION SELECT 1,2,3,4,5,6,7,8,9,10 --";
        var results = dao.searchByTerm(malicious);
        
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("deve buscar por autor/diretor também")
    void testSearchByAuthor() throws Exception {
        MediaItem item = new MediaItem() {{ setTitle("Film"); setMediaType(MediaType.MOVIE); }};
        item.setAuthorDirector("Nolan");
        dao.insert(item);
        
        var results = dao.searchByTerm("nolan");
        
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("deve buscar por título curto de 2 caracteres (ex: Up)")
    void testSearchShortTitle() throws Exception {
        dao.insert(new MediaItem() {{ setTitle("Up"); setMediaType(MediaType.MOVIE); }});
        
        var results = dao.searchByTerm("Up");
        
        assertEquals(1, results.size());
        assertEquals("Up", results.get(0).getTitle());
    }

    @Test
    @DisplayName("deve buscar por termo com hífen tratado de forma literal")
    void testSearchWithHyphen() throws Exception {
        dao.insert(new MediaItem() {{ setTitle("Spider-Man"); setMediaType(MediaType.MOVIE); }});
        
        var results = dao.searchByTerm("-Man");
        
        assertEquals(1, results.size());
        assertEquals("Spider-Man", results.get(0).getTitle());
    }

    @Test
    @DisplayName("deve buscar por ano de lançamento")
    void testSearchByReleaseYear() throws Exception {
        MediaItem item = new MediaItem() {{ setTitle("Inception"); setMediaType(MediaType.MOVIE); }};
        item.setReleaseYear(2010);
        dao.insert(item);
        
        var results = dao.searchByTerm("2010");
        
        assertEquals(1, results.size());
        assertEquals("Inception", results.get(0).getTitle());
    }

    @Test
    @DisplayName("findAll deve ignorar e pular registros com media_type desconhecido")
    void testFindAllIgnoresInvalidMediaType() throws Exception {
        dao.insert(new MediaItem() {{ setTitle("Valid Movie"); setMediaType(MediaType.MOVIE); }});
        
        try (var conn = com.seu.catalog.infra.ConnectionFactory.get();
             var stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO item_media (title, media_type) VALUES ('Invalid Book', 'BOOK')");
        }
        
        var results = dao.findAll();
        
        assertEquals(1, results.size());
        assertEquals("Valid Movie", results.get(0).getTitle());
    }

    @Test
    @DisplayName("deve buscar por termo contendo caracteres curinga do LIKE de forma literal")
    void testSearchWithWildcards() throws Exception {
        dao.insert(new MediaItem() {{ setTitle("100% Real"); setMediaType(MediaType.MOVIE); }});
        dao.insert(new MediaItem() {{ setTitle("1000 items"); setMediaType(MediaType.MOVIE); }});
        
        var results = dao.searchByTerm("100%");
        
        assertEquals(1, results.size());
        assertEquals("100% Real", results.get(0).getTitle());
    }
}
