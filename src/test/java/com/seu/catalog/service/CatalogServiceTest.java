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
    void testCreateInvalidItem() throws Exception {
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
