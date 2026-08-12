package org.example.mymovies.dao;

import org.example.mymovies.model.MediaItem;
import org.example.mymovies.model.MediaType;
import org.example.mymovies.exception.DAOException;
import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/**
 * Implementação de acesso a dados (DAO) para itens de mídia utilizando MySQL.
 * Realiza as operações de CRUD através de conexões JDBC diretas.
 */
public class MySqlMediaItemDAO implements MediaItemDAO {
    private final Connection connection;

    public MySqlMediaItemDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Realiza a inserção de um item de mídia, recuperando seu ID gerado automaticamente.
     *
     * @param item A entidade contendo os dados a serem salvos (título é obrigatório).
     * @throws DAOException Caso ocorra uma violação de restrição do SQL ou queda de conexão.
     * @since 1.0
     */
    @Override
    public void insert(MediaItem item) throws DAOException {
        String sql = "INSERT INTO item_media (title, synopsis, media_type, release_year, author_director, genre, poster_url, external_id, rating, comment) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, item.getTitle());
            stmt.setString(2, item.getSynopsis());
            stmt.setString(3, item.getType() != null ? item.getType().name() : null);
            if (item.getReleaseYear() != null) {
                stmt.setInt(4, item.getReleaseYear());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setString(5, item.getAuthorDirector());
            stmt.setString(6, item.getGenre());
            stmt.setString(7, item.getPosterUrl());
            stmt.setString(8, item.getExternalId());
            if (item.getRating() != null) {
                stmt.setInt(9, item.getRating());
            } else {
                stmt.setNull(9, Types.INTEGER);
            }
            stmt.setString(10, item.getComment());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    item.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error inserting media item", e);
        }
    }

    /**
     * Busca os dados completos de uma mídia específica pelo identificador primário.
     *
     * @param id O identificador numérico persistido na base.
     * @return A entidade preenchida caso encontrada, ou nulo se não existir registro.
     * @throws DAOException Em caso de falha de conexão ou erro de sintaxe SQL.
     * @since 1.0
     */
    @Override
    public MediaItem findById(Long id) throws DAOException {
        String sql = "SELECT * FROM item_media WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToMediaItem(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding media item by id", e);
        }
        return null;
    }

    /**
     * Recupera a coleção inteira de mídias sem filtros.
     *
     * @return Lista contendo as mídias registradas, podendo estar vazia.
     * @throws DAOException Caso a tabela não exista ou o banco rejeite a query.
     * @since 1.0
     */
    @Override
    public List<MediaItem> findAll() throws DAOException {
        String sql = "SELECT * FROM item_media";
        List<MediaItem> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(mapRowToMediaItem(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding all media items", e);
        }
        return result;
    }

    /**
     * Sobrescreve as informações de um registro previamente salvo.
     *
     * @param item A entidade com o ID existente e as propriedades atualizadas.
     * @throws DAOException Se o registro não puder ser modificado devido a erros no driver.
     * @since 1.0
     */
    @Override
    public void update(MediaItem item) throws DAOException {
        String sql = "UPDATE item_media SET title = ?, synopsis = ?, media_type = ?, release_year = ?, author_director = ?, genre = ?, poster_url = ?, external_id = ?, rating = ?, comment = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, item.getTitle());
            stmt.setString(2, item.getSynopsis());
            stmt.setString(3, item.getType() != null ? item.getType().name() : null);
            if (item.getReleaseYear() != null) {
                stmt.setInt(4, item.getReleaseYear());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setString(5, item.getAuthorDirector());
            stmt.setString(6, item.getGenre());
            stmt.setString(7, item.getPosterUrl());
            stmt.setString(8, item.getExternalId());
            if (item.getRating() != null) {
                stmt.setInt(9, item.getRating());
            } else {
                stmt.setNull(9, Types.INTEGER);
            }
            stmt.setString(10, item.getComment());
            stmt.setLong(11, item.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error updating media item", e);
        }
    }

    /**
     * Remove definitivamente o registro de mídia associado à chave primária.
     *
     * @param id A chave primária (ID) a ser excluída.
     * @throws DAOException Se o banco negar a deleção ou houver perda de conexão.
     * @since 1.0
     */
    @Override
    public void delete(Long id) throws DAOException {
        String sql = "DELETE FROM item_media WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error deleting media item", e);
        }
    }

    /**
     * Executa uma busca não sensível a maiúsculas baseada no título ou autor/diretor.
     *
     * @param term O pedaço de texto para corresponder nos registros (LIKE %term%).
     * @return Lista de mídias que satisfazem o critério textual.
     * @throws DAOException Se ocorrer alguma limitação do banco ao executar a pesquisa.
     * @since 1.0
     */
    @Override
    public List<MediaItem> searchByTerm(String term) throws DAOException {
        String sql = "SELECT * FROM item_media WHERE LOWER(title) LIKE LOWER(?) OR LOWER(author_director) LIKE LOWER(?)";
        List<MediaItem> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + term + "%");
            stmt.setString(2, "%" + term + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRowToMediaItem(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error searching media items", e);
        }
        return result;
    }

    private MediaItem mapRowToMediaItem(ResultSet rs) throws SQLException {
        Integer releaseYear = (Integer) rs.getObject("release_year");
        String typeStr = rs.getString("media_type");
        MediaType type = typeStr != null ? MediaType.valueOf(typeStr) : null;
        MediaItem item = new MediaItem(
            rs.getString("title"),
            rs.getString("synopsis"),
            type,
            releaseYear,
            rs.getString("author_director"),
            rs.getString("genre")
        );
        item.setId(rs.getLong("id"));
        item.setPosterUrl(rs.getString("poster_url"));
        item.setExternalId(rs.getString("external_id"));
        item.setRating((Integer) rs.getObject("rating"));
        item.setComment(rs.getString("comment"));
        return item;
    }
}
