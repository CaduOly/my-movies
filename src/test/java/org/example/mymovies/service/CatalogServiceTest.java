package org.example.mymovies.service;

import org.example.mymovies.dao.MediaItemDAO;
import org.example.mymovies.exception.DAOException;
import org.example.mymovies.exception.ServiceException;
import org.example.mymovies.exception.ValidationException;
import org.example.mymovies.model.MediaItem;
import org.example.mymovies.provider.MovieMetadataProvider;
import org.example.mymovies.validator.MediaItemValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CatalogServiceTest {

    private MediaItemValidator validator;
    private MovieMetadataProvider metadataProvider;
    private MediaItemDAO mediaItemDAO;
    private Connection connection;
    private CatalogService catalogService;

    @BeforeEach
    public void setUp() throws Exception {
        validator = mock(MediaItemValidator.class);
        metadataProvider = mock(MovieMetadataProvider.class);
        mediaItemDAO = mock(MediaItemDAO.class);
        connection = mock(Connection.class);

        catalogService = new CatalogService(validator, metadataProvider) {
            @Override
            protected Connection getConnection() throws SQLException {
                return connection;
            }

            @Override
            protected MediaItemDAO getMediaItemDAO(Connection conn) {
                return mediaItemDAO;
            }
        };
    }

    @Test
    public void testAddMediaItemSuccess() throws Exception {
        MediaItem item = new MediaItem("The Matrix", "Sci-fi", "MOVIE", 1999);
        when(metadataProvider.fetchMetadata("The Matrix")).thenReturn("Great movie");
        
        catalogService.addMediaItem(item);
        
        verify(validator).validate(item);
        verify(connection).setAutoCommit(false);
        verify(mediaItemDAO).insert(item);
        verify(connection).commit();
        assertEquals("Great movie", item.getDescription());
    }

    @Test
    public void testAddMediaItemValidationFailure() throws Exception {
        MediaItem item = new MediaItem("", "", "MOVIE", 1999);
        doThrow(new ValidationException("Invalid title")).when(validator).validate(item);
        
        assertThrows(ServiceException.class, () -> catalogService.addMediaItem(item));
        
        verify(connection, never()).setAutoCommit(false);
        verify(mediaItemDAO, never()).insert(any(MediaItem.class));
    }

    @Test
    public void testAddMediaItemDAOExceptionTriggersRollback() throws Exception {
        MediaItem item = new MediaItem("The Matrix", "Sci-fi", "MOVIE", 1999);
        doThrow(new DAOException("DB error", new RuntimeException())).when(mediaItemDAO).insert(item);
        
        assertThrows(ServiceException.class, () -> catalogService.addMediaItem(item));
        
        verify(connection).rollback();
    }
}
