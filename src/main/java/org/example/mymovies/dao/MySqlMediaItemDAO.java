package org.example.mymovies.dao;

import org.example.mymovies.model.MediaItem;
import org.example.mymovies.util.ConnectionFactory;
import org.example.mymovies.exception.DAOException;
import java.sql.Connection;
import java.util.List;

public class MySqlMediaItemDAO implements MediaItemDAO {
    private final Connection connection;

    public MySqlMediaItemDAO(Connection connection) {
        this.connection = connection;
    }

    public MySqlMediaItemDAO() {
        try {
            this.connection = ConnectionFactory.getConnection();
        } catch (Exception e) {
            throw new DAOException("Failed to connect to database", e);
        }
    }

    @Override
    public void insert(MediaItem item) {
        String sql = "INSERT INTO media_items (title, description, type, release_year) VALUES (?, ?, ?, ?)";
        try (java.sql.PreparedStatement stmt = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, item.getTitle());
            stmt.setString(2, item.getDescription());
            stmt.setString(3, item.getType());
            if (item.getReleaseYear() != null) {
                stmt.setInt(4, item.getReleaseYear());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            stmt.executeUpdate();
            try (java.sql.ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    item.setId(rs.getLong(1));
                }
            }
        } catch (java.sql.SQLException e) {
            throw new DAOException("Error inserting media item", e);
        }
    }

    @Override
    public MediaItem findById(Long id) {
        String sql = "SELECT * FROM media_items WHERE id = ?";
        try (java.sql.PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    MediaItem item = new MediaItem(
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("type"),
                        rs.getInt("release_year")
                    );
                    item.setId(rs.getLong("id"));
                    return item;
                }
            }
        } catch (java.sql.SQLException e) {
            throw new DAOException("Error finding media item by id", e);
        }
        return null;
    }

    @Override
    public List<MediaItem> findAll() {
        String sql = "SELECT * FROM media_items";
        List<MediaItem> result = new java.util.ArrayList<>();
        try (java.sql.PreparedStatement stmt = connection.prepareStatement(sql);
             java.sql.ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                MediaItem item = new MediaItem(
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("type"),
                    rs.getInt("release_year")
                );
                item.setId(rs.getLong("id"));
                result.add(item);
            }
        } catch (java.sql.SQLException e) {
            throw new DAOException("Error finding all media items", e);
        }
        return result;
    }

    @Override
    public void update(MediaItem item) {
        String sql = "UPDATE media_items SET title = ?, description = ?, type = ?, release_year = ? WHERE id = ?";
        try (java.sql.PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, item.getTitle());
            stmt.setString(2, item.getDescription());
            stmt.setString(3, item.getType());
            if (item.getReleaseYear() != null) {
                stmt.setInt(4, item.getReleaseYear());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            stmt.setLong(5, item.getId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new DAOException("Error updating media item", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM media_items WHERE id = ?";
        try (java.sql.PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new DAOException("Error deleting media item", e);
        }
    }

    @Override
    public List<MediaItem> searchByTerm(String term) {
        String sql = "SELECT * FROM media_items WHERE title LIKE ?";
        List<MediaItem> result = new java.util.ArrayList<>();
        try (java.sql.PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + term + "%");
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MediaItem item = new MediaItem(
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("type"),
                        rs.getInt("release_year")
                    );
                    item.setId(rs.getLong("id"));
                    result.add(item);
                }
            }
        } catch (java.sql.SQLException e) {
            throw new DAOException("Error searching media items", e);
        }
        return result;
    }
}
