package com.app.dto.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.List;

@Schema(description = "ControllerBookDTO")
public class ControllerBookDTO {

    @Getter
    @Setter
    @SuperBuilder
    @Schema(name = "create book", description = "create book")
    public static class Create {

        @Schema(description = "book title", example = "matrix", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 255)
        @JsonProperty("title")
        @NotBlank(message = "title is required")
        @Size(min = 2, max = 255, message = "title must be between 2 and 255 characters")
        private String title;

        @Schema(description = "book author_id", example = "2", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1")
        @JsonProperty("author_id")
        @NotNull(message = "author_id is required")
        @Min(value = 1, message = "author_id must be greater than 0")
        private Long authorId;

        @Schema(description = "book genre", example = "23", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1")
        @JsonProperty("genre_id")
        @NotNull(message = "genre_id is required")
        @Min(value = 1, message = "genre_id must be greater than 0")
        private Long genreId;

        @Schema(description = "book publication year", example = "1984", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1900", maximum = "2040")
        @JsonProperty("pub_year")
        @Min(value = 1900, message = "year must be greater than 1900")
        @Max(value = 2040, message = "year must be lower than 2040")
        private int pubYear;

        @Schema(description = "international standard book number", example = "978-5-127-12345-7", requiredMode = Schema.RequiredMode.REQUIRED, pattern = "\\d{3}-\\d-\\d{3}-\\d{5}-\\d")
        @JsonProperty("isbn")
        @Pattern(regexp = "\\d{3}-\\d-\\d{3}-\\d{5}-\\d", message = "Invalid ISBN format")
        @NotBlank(message = "isbn is required")
        private String isbn;

        @Schema(description = "book page count", example = "123", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1", maximum = "20000")
        @JsonProperty("page_count")
        @Min(value = 1, message = "page count must be greater than 1")
        @Max(value = 20000, message = "Page count must be lower than 20000")
        private int pageCount;

        @Schema(description = "book availability status", example = "true", defaultValue = "true")
        @JsonProperty("is_available")
        private boolean isAvailable;

        public Create() {
        }
    }

    @Getter
    @Setter
    @SuperBuilder
    @Schema(name = "patch book", description = "update book (all fields optional)")
    public static class Patch {

        @Schema(description = "book title", example = "matrix", minLength = 2, maxLength = 255)
        @JsonProperty("title")
        private String title;

        @Schema(description = "book author_id", example = "2", minimum = "1")
        @JsonProperty("author_id")
        @Min(value = 1, message = "author_id must be greater than 0")
        private Long authorId;

        @Schema(description = "book genre_id", example = "4", minimum = "1")
        @JsonProperty("genre_id")
        @Min(value = 1, message = "author_id must be greater than 0")
        private Long genreId;

        @Schema(description = "book publication year", example = "1984", minimum = "1900", maximum = "2040")
        @JsonProperty("pub_year")
        @Min(value = 1900, message = "year must be greater than 1900")
        @Max(value = 2040, message = "year must be lower than 2040")
        private Integer pubYear;

        @Schema(description = "international standard book number", example = "978-5-17-123456-7", pattern = "\\d{3}-\\d-\\d{3}-\\d{5}-\\d")
        @JsonProperty("isbn")
        @Pattern(regexp = "\\d{3}-\\d-\\d{3}-\\d{5}-\\d", message = "Invalid ISBN format")
        private String isbn;

        @Schema(description = "book page count", example = "123", minimum = "1", maximum = "20000")
        @JsonProperty("page_count")
        @Min(value = 1, message = "page count must be greater than 1")
        @Max(value = 20000, message = "Page count must be lower than 20000")
        private Integer pageCount;

        @Schema(description = "book availability status", example = "true")
        @JsonProperty("is_available")
        private Boolean isAvailable;

        public Patch() {
        }
    }

    @Getter
    @Setter
    @SuperBuilder
    @Schema(name = "response book", description = "response for book (includes all fields + id)")
    public static class Response extends ControllerBookDTO.Create {

        @Schema(description = "unique book identifier", example = "101", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("id")
        private Long id;

        @Schema(description = "book created_at", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("created_at")
        private OffsetDateTime createdAt;

        @Schema(description = "book updated_at", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("updated_at")
        private OffsetDateTime updatedAt;

        @Override
        @JsonProperty("is_available") //HINT: jackson bug
        public boolean isAvailable() {
            return super.isAvailable();
        }

        public Response() {
        }
    }

    @Getter
    @Setter
    @SuperBuilder
    @Schema(name = "book list response")
    public static class ListResponse {

        @Schema(name = "books", description = "List of books", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("books")
        private List<ControllerBookDTO.Response> books;

        public ListResponse() {
        }

        public ListResponse(List<ControllerBookDTO.Response> books) {
            this.books = books;
        }
    }

    // public static class Stats {
    //     @Schema(description = "totalBooks", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
    //     @JsonProperty("totalBooks")
    //     public Integer total = 0;

    //     @Schema(description = "availableBooks", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
    //     @JsonProperty("availableBooks")
    //     public Integer availableBooks = 0;

    //     @Schema(description = "borrowedBooks", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
    //     @JsonProperty("borrowedBooks")
    //     public Integer borrowedBooks = 0;

    //     @Schema(description = "genres", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    //     @JsonProperty("genres")
    //     public HashMap<String, Integer> genres;

    //     public Stats(List<Book> books) {
    //         this.genres = new HashMap<String, Integer>();

    //         books.stream().forEach(b -> {
    //             if (b.isAvailable()) {
    //                 this.availableBooks++;
    //             } else {
    //                 this.borrowedBooks++;
    //             }

    //             this.total++;

    //             this.genres.compute(b.getGenre(), (k, v) -> v == null ? 1 : v + 1);
    //         });
    //     }
    // }
}
