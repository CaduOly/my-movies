package org.example.mymovies.validator;

import org.example.mymovies.exception.ValidationException;
import org.example.mymovies.model.MediaItem;

public class MediaItemValidator {
    public void validate(MediaItem item) throws ValidationException {
        if (item.getTitle() == null || item.getTitle().trim().isEmpty()) {
            throw new ValidationException("Title cannot be empty");
        }
        if (item.getType() == null) {
            throw new ValidationException("Type must be MOVIE, SERIES or BOOK");
        }
        if (item.getReleaseYear() != null && (item.getReleaseYear() < 1888 || item.getReleaseYear() > 2100)) {
            throw new ValidationException("Release year is invalid");
        }
    }
}
