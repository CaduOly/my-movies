package org.example.mymovies.validator;

import org.example.mymovies.exception.ValidationException;
import org.example.mymovies.model.MediaItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MediaItemValidatorTest {

    private final MediaItemValidator validator = new MediaItemValidator();

    @Test
    public void testValidMediaItem() {
        MediaItem item = new MediaItem("The Matrix", "Sci-fi classic", org.example.mymovies.model.MediaType.MOVIE, 1999, "Wachowskis", "Sci-Fi");
        assertDoesNotThrow(() -> validator.validate(item));
    }

    @Test
    public void testNullOrEmptyTitle() {
        MediaItem item1 = new MediaItem(null, "Sci-fi", org.example.mymovies.model.MediaType.MOVIE, 1999, "Wachowskis", "Sci-Fi");
        assertThrows(ValidationException.class, () -> validator.validate(item1));

        MediaItem item2 = new MediaItem("   ", "Sci-fi", org.example.mymovies.model.MediaType.MOVIE, 1999, "Wachowskis", "Sci-Fi");
        assertThrows(ValidationException.class, () -> validator.validate(item2));
    }

    @Test
    public void testInvalidType() {
        MediaItem item = new MediaItem("The Matrix", "Sci-fi", null, 1999, "Wachowskis", "Sci-Fi");
        assertThrows(ValidationException.class, () -> validator.validate(item));
    }

    @Test
    public void testInvalidYear() {
        MediaItem item = new MediaItem("The Matrix", "Sci-fi", org.example.mymovies.model.MediaType.MOVIE, 1800, "Wachowskis", "Sci-Fi");
        assertThrows(ValidationException.class, () -> validator.validate(item));
    }
}
