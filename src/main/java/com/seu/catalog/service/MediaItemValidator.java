package com.seu.catalog.service;

import com.seu.catalog.exception.ValidationException;
import com.seu.catalog.model.MediaItem;

/**
 * Valida um MediaItem antes de persistência.
 * Regras:
 * - title: obrigatório, 1-255 chars
 * - mediaType: obrigatório
 * - releaseYear: opcional, mas se presente deve ser número válido (1800-2100)
 * - rating: opcional, mas se presente deve estar entre 0-5
 */
public class MediaItemValidator {
    
    /**
     * Valida um item de mídia.
     *
     * @param item item a validar (não nulo)
     * @throws ValidationException se alguma regra for violada
     */
    public void validate(MediaItem item) throws ValidationException {
        if (item == null) {
            throw new ValidationException("error.item_null");
        }

        validateTitle(item.getTitle());
        validateMediaType(item.getMediaType());
        validateReleaseYear(item.getReleaseYear());
        validateRating(item.getRating());
        validateComment(item.getComment());
    }

    /**
     * Valida título.
     */
    private void validateTitle(String title) throws ValidationException {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("error.title_required");
        }
        if (title.length() > 255) {
            throw new ValidationException("error.title_max_length");
        }
    }

    /**
     * Valida tipo de mídia.
     */
    private void validateMediaType(Object mediaType) throws ValidationException {
        if (mediaType == null) {
            throw new ValidationException("error.type_required");
        }
    }

    /**
     * Valida ano de lançamento.
     */
    private void validateReleaseYear(Integer releaseYear) throws ValidationException {
        if (releaseYear != null) {
            if (releaseYear < 1800 || releaseYear > 2100) {
                throw new ValidationException("error.year_range");
            }
        }
    }

    /**
     * Valida avaliação (rating).
     */
    private void validateRating(Integer rating) throws ValidationException {
        if (rating != null) {
            if (rating < 0 || rating > 5) {
                throw new ValidationException("error.rating_range");
            }
        }
    }

    /**
     * Valida comentário (máximo 1000 caracteres).
     */
    private void validateComment(String comment) throws ValidationException {
        if (comment != null && comment.length() > 1000) {
            throw new ValidationException("error.comment_max_length");
        }
    }
}
