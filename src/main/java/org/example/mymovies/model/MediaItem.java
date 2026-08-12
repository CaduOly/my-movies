package org.example.mymovies.model;

public class MediaItem {
    private Long id;
    private String title;
    private String description;
    private MediaType type;
    private Integer releaseYear;

    public MediaItem() {
    }

    public MediaItem(String title, String description, MediaType type, Integer releaseYear) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.releaseYear = releaseYear;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MediaType getType() {
        return type;
    }

    public void setType(MediaType type) {
        this.type = type;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }
}
