package org.example.mymovies.dao;

import org.example.mymovies.model.MediaItem;
import java.util.List;

public interface MediaItemDAO {
    void insert(MediaItem item);
    MediaItem findById(Long id);
    List<MediaItem> findAll();
    void update(MediaItem item);
    void delete(Long id);
    List<MediaItem> searchByTerm(String term);
}
