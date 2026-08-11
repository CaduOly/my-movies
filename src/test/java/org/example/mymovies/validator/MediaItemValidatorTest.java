package org.example.mymovies.validator;

import org.example.mymovies.exception.ValidationException;
import org.example.mymovies.model.MediaItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MediaItemValidatorTest {

    private final MediaItemValidator validator = new MediaItemValidator();

    @Test
    public void testValidMediaItem() {
        MediaItem item = new MediaItem("The Matrix", "Sci-fi classic", "MOVIE", 1999);
        assertDoesNotThrow(() -> validator.validate(item));
    }

    @Test
    public void testNullOrEmptyTitle() {
        MediaItem item1 = new MediaItem(null, "Sci-fi", "MOVIE", 1999);
        assertThrows(ValidationException.class, () -> validator.validate(item1));

        MediaItem item2 = new MediaItem("   ", "Sci-fi", "MOVIE", 1999);
        assertThrows(ValidationException.class, () -> validator.validate(item2));
    }

    @Test
    public void testInvalidType() {
        MediaItem item = new MediaItem("The Matrix", "Sci-fi", "GAME", 1999);
        assertThrows(ValidationException.class, () -> validator.validate(item));
    }

    @Test
    public void testInvalidYear() {
        MediaItem item = new MediaItem("The Matrix", "Sci-fi", "MOVIE", 1800);
        assertThrows(ValidationException.class, () -> validator.validate(item));
    }
}
