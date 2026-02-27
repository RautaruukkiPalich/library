package com.app.dto.controller;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "dto")
public class ControllerBookDTO {

    @Schema(name = "create book", description = "create book")
    public static class Create {

        @Schema(description = "book title", example = "matrix", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 255)
        @JsonProperty("title")
        @NotBlank(message = "title is required")
        @Size(min = 2, max = 255, message = "title must be between 2 and 255 characters")
        public String title;

        @Schema(description = "book author_id", example = "2", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1")
        @JsonProperty("author_id")
        @NotNull(message = "author_id is required")
        @Min(value = 1, message = "author_id must be greater than 0")
        public Long authorId;

        @Schema(description = "book genre", example = "23", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1")
        @JsonProperty("genre_id")
        @NotNull(message = "genre_id is required")
        @Min(value = 1, message = "genre_id must be greater than 0")
        public Long genreId;

        @Schema(description = "book publication year", example = "1984", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1900", maximum = "2040")
        @JsonProperty("pub_year")
        @Min(value = 1900, message = "year must be greater than 1900")
        @Max(value = 2040, message = "year must be lower than 2040")
        public int pubYear;

        @Schema(description = "international standard book number", example = "978-5-17-123456-7", requiredMode = Schema.RequiredMode.REQUIRED, pattern = "\\d{3}-\\d-\\d{3}-\\d{5}-\\d")
        @JsonProperty("isbn")
        @Pattern(regexp = "\\d{3}-\\d-\\d{3}-\\d{5}-\\d", message = "Invalid ISBN format")
        @NotBlank(message = "isbn is required")
        public String isbn;

        @Schema(description = "book page count", example = "123", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1", maximum = "20000")
        @JsonProperty("page_count")
        @Min(value = 1, message = "page count must be greater than 1")
        @Max(value = 20000, message = "Page count must be lower than 20000")
        public int pageCount;

        @Schema(description = "book availability status", example = "true", defaultValue = "true")
        @JsonProperty("is_available")
        public boolean isAvailable;
    }

    @Schema(name = "patch book", description = "update book (all fields optional)")
    public static class Patch {

        @Schema(description = "book title", example = "matrix", minLength = 2, maxLength = 255)
        @JsonProperty("title")
        public String title;

        @Schema(description = "book author_id", example = "2", minimum = "1")
        @JsonProperty("author_id")
        @Min(value = 1, message = "author_id must be greater than 0")
        public Long authorId;

        @Schema(description = "book genre_id", example = "4", minimum = "1")
        @JsonProperty("genre_id")
        @Min(value = 1, message = "author_id must be greater than 0")
        public Long genreId;

        @Schema(description = "book publication year", example = "1984", minimum = "1900", maximum = "2040")
        @JsonProperty("pub_year")
        @Min(value = 1900, message = "year must be greater than 1900")
        @Max(value = 2040, message = "year must be lower than 2040")
        public Integer pubYear;

        @Schema(description = "international standard book number", example = "978-5-17-123456-7", pattern = "\\d{3}-\\d-\\d{3}-\\d{5}-\\d")
        @JsonProperty("isbn")
        @Pattern(regexp = "\\d{3}-\\d-\\d{3}-\\d{5}-\\d", message = "Invalid ISBN format")
        public String isbn;

        @Schema(description = "book page count", example = "123", minimum = "1", maximum = "20000")
        @JsonProperty("page_count")
        @Min(value = 1, message = "page count must be greater than 1")
        @Max(value = 20000, message = "Page count must be lower than 20000")
        public Integer pageCount;

        @Schema(description = "book availability status", example = "true")
        @JsonProperty("is_available")
        public Boolean isAvailable;
    }

    @Schema(name = "response book", description = "response for book (includes all fields + id)")
    public static class Response extends ControllerBookDTO.Create {

        @Schema(description = "unique book identifier", example = "101", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("id")
        public Long id;

    }

    @Schema(name = "book list response")
    public static class ListResponse {

        @Schema(name = "book list response", description = "List of books", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("books")
        public List<ControllerBookDTO.Response> books;

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
