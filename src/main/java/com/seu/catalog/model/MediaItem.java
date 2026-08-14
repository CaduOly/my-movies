package com.seu.catalog.model;

import java.util.Objects;

/**
 * Representa um item de mídia no catálogo.
 * Um item pode ser um filme ou série.
 */
public class MediaItem {
    
    private Integer id;
    private String title;
    private String authorDirector;
    private Integer releaseYear;
    private String genre;
    private String synopsis;
    private MediaType mediaType;
    private String posterUrl;
    private Integer rating;
    private String comment;

    /**
     * Cria um novo item de mídia com os atributos obrigatórios.
     *
     * @param title     o título do item; não pode ser nulo ou vazio
     * @param mediaType o tipo de mídia; não pode ser nulo
     */
    public MediaItem(String title, MediaType mediaType) {
        this.title = title;
        this.mediaType = mediaType;
    }

    /**
     * Construtor vazio padrão, útil para inicializações sem argumentos.
     */
    public MediaItem() {
    }

    /**
     * Obtém o identificador único do item.
     *
     * @return o identificador único
     */
    public Integer getId() {
        return id;
    }

    /**
     * Define o identificador único do item.
     *
     * @param id o identificador único a ser definido
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Obtém o título do item.
     *
     * @return o título do item
     */
    public String getTitle() {
        return title;
    }

    /**
     * Define o título do item.
     *
     * @param title o título a ser definido
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Obtém o autor ou diretor do item.
     *
     * @return o autor ou diretor
     */
    public String getAuthorDirector() {
        return authorDirector;
    }

    /**
     * Define o autor ou diretor do item.
     *
     * @param authorDirector o autor ou diretor a ser definido
     */
    public void setAuthorDirector(String authorDirector) {
        this.authorDirector = authorDirector;
    }

    /**
     * Obtém o ano de lançamento do item.
     *
     * @return o ano de lançamento
     */
    public Integer getReleaseYear() {
        return releaseYear;
    }

    /**
     * Define o ano de lançamento do item.
     *
     * @param releaseYear o ano de lançamento a ser definido
     */
    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    /**
     * Obtém o gênero do item.
     *
     * @return o gênero
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Define o gênero do item.
     *
     * @param genre o gênero a ser definido
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Obtém a sinopse do item.
     *
     * @return a sinopse
     */
    public String getSynopsis() {
        return synopsis;
    }

    /**
     * Define a sinopse do item.
     *
     * @param synopsis a sinopse a ser definida
     */
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    /**
     * Obtém o tipo de mídia do item.
     *
     * @return o tipo de mídia
     */
    public MediaType getMediaType() {
        return mediaType;
    }

    /**
     * Define o tipo de mídia do item.
     *
     * @param mediaType o tipo de mídia a ser definido
     */
    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    /**
     * Obtém a URL do pôster do item.
     *
     * @return a URL do pôster
     */
    public String getPosterUrl() {
        return posterUrl;
    }

    /**
     * Define a URL do pôster do item.
     *
     * @param posterUrl a URL do pôster a ser definida
     */
    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }


    /**
     * Obtém a nota de avaliação do item.
     *
     * @return a nota (0-5)
     */
    public Integer getRating() {
        return rating;
    }

    /**
     * Define a nota de avaliação do item.
     *
     * @param rating a nota a ser definida, deve estar entre 0 e 5
     */
    public void setRating(Integer rating) {
        this.rating = rating;
    }

    /**
     * Obtém o comentário ou resenha do item.
     *
     * @return o comentário
     */
    public String getComment() {
        return comment;
    }

    /**
     * Define o comentário ou resenha do item.
     *
     * @param comment o comentário a ser definido
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaItem mediaItem = (MediaItem) o;
        return Objects.equals(id, mediaItem.id) &&
               Objects.equals(title, mediaItem.title) &&
               mediaType == mediaItem.mediaType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, mediaType);
    }

    @Override
    public String toString() {
        return "MediaItem{" +
               "id=" + id +
               ", title='" + title + '\'' +
               ", mediaType=" + mediaType +
               '}';
    }
}
