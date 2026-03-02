package com.app.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.app.dto.BookDTO;
import com.app.dto.controller.ControllerBookDTO;
import com.app.model.Book;

@Component
public class BookMapper {
    public static ControllerBookDTO.Response toResponse(Book book) {
        Objects.requireNonNull(book, "book cant be null");
        Objects.requireNonNull(book.getAuthor(), "author cant be null");
        Objects.requireNonNull(book.getGenre(), "genre cant be null");

        return ControllerBookDTO.Response.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorId(book.getAuthor().getId())
                .genreId(book.getAuthor().getId())
                .pubYear(book.getPubYear())
                .isbn(book.getIsbn())
                .isAvailable(book.isAvailable())
                .pageCount(book.getPageCount())
                .build();
    }

    public static BookDTO toDTO(ControllerBookDTO.Create dto) {
        Objects.requireNonNull(dto, "dto cant be null");

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

    public static BookDTO toDTO(ControllerBookDTO.Patch dto) {
        Objects.requireNonNull(dto, "dto cant be null");

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


    public static List<ControllerBookDTO.Response> toResponse(List<Book> books) {
        return books.stream()
                .map(BookMapper::toResponse)
                .collect(Collectors.toList());
    }
}
