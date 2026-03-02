package com.app.mapper;

import com.app.dto.AuthorDTO;
import com.app.dto.controller.ControllerAuthorDTO;
import com.app.model.Author;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class AuthorMapper {
    public static ControllerAuthorDTO.Response toResponse(Author author) {
        Objects.requireNonNull(author, "author cant be null");

        return ControllerAuthorDTO.Response.builder()
                .id(author.getId())
                .surname(author.getSurname())
                .firstname(author.getFirstname())
                .lastname(author.getLastname())
                .build();
    }

    public static List<ControllerAuthorDTO.Response> toResponse(List<Author> authors) {
        return authors.stream().map(AuthorMapper::toResponse).collect(Collectors.toList());
    }

    public static AuthorDTO toDTO(ControllerAuthorDTO.Create dto) {
        Objects.requireNonNull(dto, "dto cant be null");

        return AuthorDTO
                .builder()
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .surname(dto.getSurname())
                .build();
    }
}
