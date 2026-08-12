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
        dao = new MySqlMediaItemDAO();
        
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

    @Test
    @DisplayName("SQL Injection: DELETE statement injected deve falhar silenciosamente")
    void testSearchInjectionDelete() throws Exception {
        dao.insert(new MediaItem("Valid Item", MediaType.MOVIE));
        
        String malicious = "'; DROP TABLE item_media; --";
        var results = dao.searchByTerm(malicious);
        
        assertTrue(results.isEmpty());
        
        var allItems = dao.findAll();
        assertEquals(1, allItems.size(), "Tabela não foi deletada");
    }

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
