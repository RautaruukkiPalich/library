package com.app.modules.book.mapper;

import com.app.modules.book.dto.BookControllerDTO;
import com.app.modules.book.dto.BookDTO;
import com.app.modules.book.model.Book;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookMapper {
    public static BookDTO toDTO(@NonNull BookControllerDTO.Create dto) {
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

    public static BookDTO toDTO(@NonNull BookControllerDTO.Patch dto) {
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

    public static BookControllerDTO.Response toResponse(@NonNull BookDTO book) {
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

    public static List<BookControllerDTO.Response> toResponse(@NonNull List<BookDTO> books) {
        return books.stream()
                .map(BookMapper::toResponse)
                .collect(Collectors.toList());
    }

    public static BookDTO toDTO(@NonNull Book book) {
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorId(book.getAuthor() == null ? null : book.getAuthor().getId())
                .genreId(book.getGenre() == null ? null : book.getGenre().getId())
                .pubYear(book.getPubYear())
                .isbn(book.getIsbn())
                .isAvailable(book.isAvailable())
                .pageCount(book.getPageCount())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }

    public static List<BookDTO> toListDTO(@NonNull List<Book> books) {
        return books.stream().map(BookMapper::toDTO).collect(Collectors.toList());
    }
}
