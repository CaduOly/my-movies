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

/**
 * Serviço responsável por orquestrar as regras de negócio 
 * relacionadas ao catálogo de mídias (filmes, séries, livros).
 */
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

    /**
     * Adiciona um novo item de mídia ao catálogo.
     * 
     * @param item O item de mídia a ser adicionado.
     * @throws ServiceException Se houver erro de validação ou de persistência.
     * @since 1.0
     */
    public void addMediaItem(MediaItem item) throws ServiceException {
        Connection conn = null;
        try {
            validator.validate(item);
            if (metadataProvider != null) {
                String desc = metadataProvider.fetchMetadata(item.getTitle());
                if (desc != null && !desc.isEmpty()) {
                    item.setSynopsis(desc);
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

    /**
     * Atualiza os dados de um item de mídia existente.
     * 
     * @param item O item de mídia contendo os dados atualizados.
     * @throws ServiceException Se houver erro de validação ou de persistência.
     * @since 1.0
     */
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

    /**
     * Remove um item de mídia do catálogo pelo seu identificador.
     * 
     * @param id O identificador numérico do item.
     * @throws ServiceException Se houver erro durante a exclusão.
     * @since 1.0
     */
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

    /**
     * Busca um item de mídia pelo seu identificador único.
     * 
     * @param id O identificador numérico do item.
     * @return O item de mídia encontrado ou null.
     * @throws ServiceException Se houver erro durante a busca.
     * @since 1.0
     */
    public MediaItem findById(Long id) throws ServiceException {
        try (Connection conn = getConnection()) {
            return getMediaItemDAO(conn).findById(id);
        } catch (Exception e) {
            throw new ServiceException("Failed to find media item by id", e);
        }
    }

    /**
     * Recupera todos os itens de mídia cadastrados.
     * 
     * @return Uma lista contendo todos os itens de mídia.
     * @throws ServiceException Se houver erro durante a busca.
     * @since 1.0
     */
    public List<MediaItem> findAll() throws ServiceException {
        try (Connection conn = getConnection()) {
            return getMediaItemDAO(conn).findAll();
        } catch (Exception e) {
            throw new ServiceException("Failed to find all media items", e);
        }
    }

    /**
     * Busca itens de mídia que correspondam a um termo específico.
     * 
     * @param term O termo de busca para filtrar os itens.
     * @return Uma lista de itens que correspondem ao termo buscado.
     * @throws ServiceException Se houver erro durante a busca.
     * @since 1.0
     */
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
                /** Ignore - nenhuma ação necessária caso o rollback falhe silenciosamente */
            }
        }
    }

    private void closeQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ex) {
                /** Ignore - nenhuma ação necessária caso o fechamento falhe silenciosamente */
            }
        }
    }
}
