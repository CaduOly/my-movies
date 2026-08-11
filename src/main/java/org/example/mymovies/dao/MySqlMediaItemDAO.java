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

public class MySqlMediaItemDAO implements MediaItemDAO {
    private final Connection connection;

    public MySqlMediaItemDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(MediaItem item) throws DAOException {
        String sql = "INSERT INTO item_media (title, description, media_type, release_year) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, item.getTitle());
            stmt.setString(2, item.getDescription());
            stmt.setString(3, item.getType() != null ? item.getType().name() : null);
            if (item.getReleaseYear() != null) {
                stmt.setInt(4, item.getReleaseYear());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
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

    @Override
    public void update(MediaItem item) throws DAOException {
        String sql = "UPDATE item_media SET title = ?, description = ?, media_type = ?, release_year = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, item.getTitle());
            stmt.setString(2, item.getDescription());
            stmt.setString(3, item.getType() != null ? item.getType().name() : null);
            if (item.getReleaseYear() != null) {
                stmt.setInt(4, item.getReleaseYear());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setLong(5, item.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error updating media item", e);
        }
    }

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

    @Override
    public List<MediaItem> searchByTerm(String term) throws DAOException {
        String sql = "SELECT * FROM item_media WHERE LOWER(title) LIKE LOWER(?)";
        List<MediaItem> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + term + "%");
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
            rs.getString("description"),
            type,
            releaseYear
        );
        item.setId(rs.getLong("id"));
        return item;
    }
}
