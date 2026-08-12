package org.example.mymovies.service;

import org.example.mymovies.dao.MediaItemDAO;
import org.example.mymovies.dao.MySqlMediaItemDAO;
import org.example.mymovies.exception.ServiceException;
import org.example.mymovies.exception.ValidationException;
import org.example.mymovies.model.MediaItem;
import org.example.mymovies.provider.MovieMetadataProvider;
import org.example.mymovies.util.ConnectionFactory;
import org.example.mymovies.validator.MediaItemValidator;

import java.sql.Connection;
import java.sql.SQLException;

public class CatalogService {
    private MediaItemValidator validator;
    private MovieMetadataProvider metadataProvider;

    public CatalogService(MediaItemValidator validator, MovieMetadataProvider metadataProvider) {
        this.validator = validator;
        this.metadataProvider = metadataProvider;
    }

    // Protected for testing
    protected Connection getConnection() throws SQLException {
        return ConnectionFactory.getConnection();
    }

    // Protected for testing
    protected MediaItemDAO getMediaItemDAO(Connection connection) {
        return new MySqlMediaItemDAO(connection);
    }

    public void addMediaItem(MediaItem item) throws ServiceException {
        Connection conn = null;
        try {
            validator.validate(item);
            
            if (metadataProvider != null) {
                String desc = metadataProvider.fetchMetadata(item.getTitle());
                if (desc != null && !desc.isEmpty()) {
                    item.setDescription(desc);
                }
            }

            conn = getConnection();
            conn.setAutoCommit(false);
            
            MediaItemDAO dao = getMediaItemDAO(conn);
            dao.insert(item);
            
            conn.commit();
        } catch (ValidationException e) {
            throw new ServiceException("Validation failed: " + e.getMessage(), e);
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // Ignore
                }
            }
            throw new ServiceException("Failed to add media item", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    // Ignore
                }
            }
        }
    }
}
