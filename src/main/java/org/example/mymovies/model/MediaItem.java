package org.example.mymovies.model;

/**
 * Classe de modelo que representa um item de mídia no catálogo.
 * Pode ser um livro, filme ou série, contendo informações básicas.
 */
public class MediaItem {
    private Long id;
    private String title;
    private String synopsis;
    private MediaType type;
    private Integer releaseYear;
    private String authorDirector;
    private String genre;
    private String posterUrl;
    private String externalId;
    private Integer rating;
    private String comment;

    public MediaItem() {
    }

    public MediaItem(String title, String synopsis, MediaType type, Integer releaseYear, String authorDirector, String genre) {
        this.title = title;
        this.synopsis = synopsis;
        this.type = type;
        this.releaseYear = releaseYear;
        this.authorDirector = authorDirector;
        this.genre = genre;
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

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
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

    public String getAuthorDirector() {
        return authorDirector;
    }

    public void setAuthorDirector(String authorDirector) {
        this.authorDirector = authorDirector;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
