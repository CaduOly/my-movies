package com.seu.catalog.service;

import com.seu.catalog.exception.ValidationException;
import com.seu.catalog.model.MediaItem;
import com.seu.catalog.model.MediaType;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MediaItemValidator Tests")
class MediaItemValidatorTest {
    
    private MediaItemValidator validator;

    @BeforeEach
    void setUp() {
        validator = new MediaItemValidator();
    }

    @Test
    @DisplayName("deve validar item válido")
    void testValidValidItem() {
        MediaItem item = new MediaItem("Valid Title", MediaType.MOVIE);
        assertDoesNotThrow(() -> validator.validate(item));
    }

    @Test
    @DisplayName("deve rejeitar item nulo")
    void testNullItem() {
        assertThrows(ValidationException.class, () -> validator.validate(null));
    }

    @Test
    @DisplayName("deve rejeitar title vazio")
    void testEmptyTitle() {
        MediaItem item = new MediaItem("", MediaType.MOVIE);
        assertThrows(ValidationException.class, () -> validator.validate(item));
    }

    @Test
    @DisplayName("deve rejeitar title > 255 chars")
    void testTitleTooLong() {
        String longTitle = "a".repeat(256);
        MediaItem item = new MediaItem(longTitle, MediaType.MOVIE);
        assertThrows(ValidationException.class, () -> validator.validate(item));
    }



    @Test
    @DisplayName("deve rejeitar releaseYear < 1800")
    void testReleaseYearTooOld() {
        MediaItem item = new MediaItem("Title", MediaType.MOVIE);
        item.setReleaseYear(1799);
        assertThrows(ValidationException.class, () -> validator.validate(item));
    }

    @Test
    @DisplayName("deve rejeitar releaseYear > 2100")
    void testReleaseYearTooFuture() {
        MediaItem item = new MediaItem("Title", MediaType.MOVIE);
        item.setReleaseYear(2101);
        assertThrows(ValidationException.class, () -> validator.validate(item));
    }

    @Test
    @DisplayName("deve aceitar releaseYear válido (1800-2100)")
    void testReleaseYearValid() {
        MediaItem item = new MediaItem("Title", MediaType.MOVIE);
        item.setReleaseYear(2010);
        assertDoesNotThrow(() -> validator.validate(item));
    }

    @Test
    @DisplayName("deve rejeitar rating < 0")
    void testRatingNegative() {
        MediaItem item = new MediaItem("Title", MediaType.MOVIE);
        item.setRating(-1);
        assertThrows(ValidationException.class, () -> validator.validate(item));
    }

    @Test
    @DisplayName("deve rejeitar rating > 5")
    void testRatingTooHigh() {
        MediaItem item = new MediaItem("Title", MediaType.MOVIE);
        item.setRating(6);
        assertThrows(ValidationException.class, () -> validator.validate(item));
    }

    @Test
    @DisplayName("deve aceitar rating válido (0-5)")
    void testRatingValid() {
        MediaItem item = new MediaItem("Title", MediaType.MOVIE);
        item.setRating(3);
        assertDoesNotThrow(() -> validator.validate(item));
    }

    @Test
    @DisplayName("deve aceitar null em campos opcionais")
    void testOptionalFieldsNull() {
        MediaItem item = new MediaItem("Title", MediaType.MOVIE);
        assertDoesNotThrow(() -> validator.validate(item));
    }
}
