package org.example.mymovies.dao;

import org.example.mymovies.model.MediaItem;
import java.util.List;

import org.example.mymovies.exception.DAOException;

public interface MediaItemDAO {
    void insert(MediaItem item) throws DAOException;
    MediaItem findById(Long id) throws DAOException;
    List<MediaItem> findAll() throws DAOException;
    void update(MediaItem item) throws DAOException;
    void delete(Long id) throws DAOException;
    List<MediaItem> searchByTerm(String term) throws DAOException;
}
