package com.app.modules.book.mapper;

import com.app.modules.book.dto.BookControllerDTO;
import com.app.modules.book.dto.BookDTO;
import com.app.modules.book.model.Book;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class BookMapper {
    public static BookDTO toDTO(BookControllerDTO.Create dto) {
        Objects.requireNonNull(dto, "dto must not be null");

        return BookDTO.builder()
                .title(dto.getTitle())
                .authorId(dto.getAuthorId())
                .genreId(dto.getGenreId())
                .pubYear(dto.getPubYear())
                .isbn(dto.getIsbn())
                .isAvailable(dto.isAvailable())
                .pageCount(dto.getPageCount())
                .build();
    }

    public static BookDTO toDTO(BookControllerDTO.Patch dto) {
        Objects.requireNonNull(dto, "dto must not be null");

        return BookDTO.builder()
                .title(dto.getTitle())
                .authorId(dto.getAuthorId())
                .genreId(dto.getGenreId())
                .pubYear(dto.getPubYear())
                .isbn(dto.getIsbn())
                .isAvailable(dto.getIsAvailable())
                .pageCount(dto.getPageCount())
                .build();
    }

    public static BookControllerDTO.Response toResponse(BookDTO book) {
        Objects.requireNonNull(book, "book must not be null");

        return BookControllerDTO.Response.builder()
                .id(book.id())
                .title(book.title())
                .authorId(book.authorId())
                .genreId(book.genreId())
                .pubYear(book.pubYear())
                .isbn(book.isbn())
                .isAvailable(book.isAvailable())
                .pageCount(book.pageCount())
                .createdAt(book.createdAt())
                .updatedAt(book.updatedAt())
                .build();
    }

    public static List<BookControllerDTO.Response> toResponse(List<BookDTO> books) {
        return books.stream()
                .map(BookMapper::toResponse)
                .collect(Collectors.toList());
    }

    public static BookDTO toDTO(Book b) {
        Objects.requireNonNull(b, "book must not be null");
        Objects.requireNonNull(b.getAuthor(), "author must not be null");
        Objects.requireNonNull(b.getGenre(), "genre must not be null");

        return BookDTO.builder()
                .id(b.getId())
                .title(b.getTitle())
                .authorId(b.getAuthor().getId())
                .genreId(b.getGenre().getId())
                .pubYear(b.getPubYear())
                .isbn(b.getIsbn())
                .isAvailable(b.isAvailable())
                .pageCount(b.getPageCount())
                .createdAt(b.getCreatedAt())
                .updatedAt(b.getUpdatedAt())
                .build();
    }

    public static List<BookDTO> toListDTO(List<Book> books) {
        Objects.requireNonNull(books, "books must not be null");

        return books.stream().map(BookMapper::toDTO).collect(Collectors.toList());
    }
}
