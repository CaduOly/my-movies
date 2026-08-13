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
            throw new ValidationException("Item não pode ser nulo");
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
            throw new ValidationException("Título é obrigatório");
        }
        if (title.length() > 255) {
            throw new ValidationException("Título não pode exceder 255 caracteres");
        }
    }

    /**
     * Valida tipo de mídia.
     */
    private void validateMediaType(Object mediaType) throws ValidationException {
        if (mediaType == null) {
            throw new ValidationException("Tipo de mídia é obrigatório");
        }
    }

    /**
     * Valida ano de lançamento.
     */
    private void validateReleaseYear(Integer releaseYear) throws ValidationException {
        if (releaseYear != null) {
            if (releaseYear < 1800 || releaseYear > 2100) {
                throw new ValidationException("Ano deve estar entre 1800 e 2100");
            }
        }
    }

    /**
     * Valida avaliação (rating).
     */
    private void validateRating(Integer rating) throws ValidationException {
        if (rating != null) {
            if (rating < 0 || rating > 5) {
                throw new ValidationException("Avaliação deve estar entre 0 e 5");
            }
        }
    }

    /**
     * Valida comentário (máximo 1000 caracteres).
     */
    private void validateComment(String comment) throws ValidationException {
        if (comment != null && comment.length() > 1000) {
            throw new ValidationException("Comentário não pode exceder 1000 caracteres");
        }
    }
}
