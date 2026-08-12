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
import java.util.List;

public class CatalogService {
    private MediaItemValidator validator;
    private MovieMetadataProvider metadataProvider;

    public CatalogService(MediaItemValidator validator, MovieMetadataProvider metadataProvider) {
        this.validator = validator;
        this.metadataProvider = metadataProvider;
    }

    protected Connection getConnection() throws SQLException {
        return ConnectionFactory.getConnection();
    }

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
            rollbackQuietly(conn);
            throw new ServiceException("Failed to add media item", e);
        } finally {
            closeQuietly(conn);
        }
    }

    public void updateMediaItem(MediaItem item) throws ServiceException {
        Connection conn = null;
        try {
            validator.validate(item);
            conn = getConnection();
            conn.setAutoCommit(false);
            MediaItemDAO dao = getMediaItemDAO(conn);
            dao.update(item);
            conn.commit();
        } catch (ValidationException e) {
            throw new ServiceException("Validation failed: " + e.getMessage(), e);
        } catch (Exception e) {
            rollbackQuietly(conn);
            throw new ServiceException("Failed to update media item", e);
        } finally {
            closeQuietly(conn);
        }
    }

    public void deleteMediaItem(Long id) throws ServiceException {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            MediaItemDAO dao = getMediaItemDAO(conn);
            dao.delete(id);
            conn.commit();
        } catch (Exception e) {
            rollbackQuietly(conn);
            throw new ServiceException("Failed to delete media item", e);
        } finally {
            closeQuietly(conn);
        }
    }

    public MediaItem findById(Long id) throws ServiceException {
        try (Connection conn = getConnection()) {
            return getMediaItemDAO(conn).findById(id);
        } catch (Exception e) {
            throw new ServiceException("Failed to find media item by id", e);
        }
    }

    public List<MediaItem> findAll() throws ServiceException {
        try (Connection conn = getConnection()) {
            return getMediaItemDAO(conn).findAll();
        } catch (Exception e) {
            throw new ServiceException("Failed to find all media items", e);
        }
    }

    public List<MediaItem> searchByTerm(String term) throws ServiceException {
        try (Connection conn = getConnection()) {
            return getMediaItemDAO(conn).searchByTerm(term);
        } catch (Exception e) {
            throw new ServiceException("Failed to search media items", e);
        }
    }

    private void rollbackQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                // Ignore
            }
        }
    }

    private void closeQuietly(Connection conn) {
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
