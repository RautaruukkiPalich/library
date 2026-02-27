package com.app.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.app.dto.BookDTO;
import com.app.dto.controller.ControllerBookDTO;
import com.app.model.Book;

@Component
public class BookMapper {
    public static ControllerBookDTO.Response toResponse(Book book){
        if (book == null){
            return null;
        }

        ControllerBookDTO.Response resp = new ControllerBookDTO.Response();
        
        resp.id = book.getId();
        resp.title = book.getTitle();
        resp.author_id = book.getAuthor().getId();
        resp.genre_id = book.getGenre().getId();
        resp.pubYear = book.getPubYear();
        resp.isbn = book.getIsbn();
        resp.isAvailable = book.isAvailable();
        resp.pageCount = book.getPageCount();

        return resp;
    }

    public static BookDTO toDTO(ControllerBookDTO.Create dto){
        if (dto == null){
            return null;
        }

        BookDTO book = new BookDTO();
        book.title = dto.title;
        book.author_id = dto.author_id;
        book.genre_id = dto.genre_id;
        book.pubYear = dto.pubYear;
        book.isbn = dto.isbn;
        book.isAvailable = dto.isAvailable;
        book.pageCount = dto.pageCount;

        return book;
    } 

    public static BookDTO toDTO(ControllerBookDTO.Patch dto){
        if (dto == null){
            return null;
        }

        BookDTO book = new BookDTO();
        book.title = dto.title;
        book.author_id = dto.author_id;
        book.genre_id = dto.genre_id;
        book.pubYear = dto.pubYear;
        book.isbn = dto.isbn;
        book.isAvailable = dto.isAvailable;
        book.pageCount = dto.pageCount;

        return book;
    } 


    public static List<ControllerBookDTO.Response> toResponse(List<Book> books){
        return books.stream()
        .map(BookMapper::toResponse)
        .collect(Collectors.toList());
    }
}
