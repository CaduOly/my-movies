package com.seu.catalog.dao;

import com.seu.catalog.infra.ConnectionFactory;
import com.seu.catalog.model.MediaItem;
import com.seu.catalog.model.MediaType;
import com.seu.catalog.exception.DAOException;
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
                   + "synopsis, media_type, poster_url, rating, comment) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.get();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, item.getTitle());
            stmt.setString(2, item.getAuthorDirector());
            stmt.setObject(3, item.getReleaseYear());
            stmt.setString(4, item.getGenre());
            stmt.setString(5, item.getSynopsis());
            stmt.setString(6, item.getMediaType().toString());
            stmt.setString(7, item.getPosterUrl());
            stmt.setObject(8, item.getRating());
            stmt.setString(9, item.getComment());
            
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
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
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
                   + "genre = ?, synopsis = ?, media_type = ?, poster_url = ?, "
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
            stmt.setObject(8, item.getRating());
            stmt.setString(9, item.getComment());
            stmt.setInt(10, item.getId());
            
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
        String sql = "SELECT * FROM item_media WHERE MATCH(title, author_director) AGAINST(? IN BOOLEAN MODE) OR CAST(release_year AS CHAR) LIKE ? ORDER BY title ASC";
        List<MediaItem> items = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String matchTerm = term.trim().isEmpty() ? "" : "+" + term.trim().replaceAll("\\s+", "* +") + "*";
            String like = "%" + term + "%";
            stmt.setString(1, matchTerm);
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
     * 
     * @param rs resultset posicionado na linha desejada
     * @return o objeto MediaItem preenchido
     * @throws SQLException se houver falha ao ler dados
     */
    private MediaItem rowToMediaItem(ResultSet rs) throws SQLException {
        MediaItem item = new MediaItem(rs.getString("title"), MediaType.valueOf(rs.getString("media_type")));
        item.setId(rs.getInt("id"));
        item.setAuthorDirector(rs.getString("author_director"));
        item.setReleaseYear((Integer) rs.getObject("release_year"));
        item.setGenre(rs.getString("genre"));
        item.setSynopsis(rs.getString("synopsis"));
        item.setPosterUrl(rs.getString("poster_url"));
        item.setRating((Integer) rs.getObject("rating"));
        item.setComment(rs.getString("comment"));
        return item;
    }
}
