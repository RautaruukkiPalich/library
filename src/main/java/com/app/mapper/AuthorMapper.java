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
    public static ControllerAuthorDTO.Response toResponse(AuthorDTO a) {
        Objects.requireNonNull(a, "author cant be null");

        return ControllerAuthorDTO.Response.builder()
                .id(a.id())
                .surname(a.surname())
                .firstname(a.firstname())
                .lastname(a.lastname())
                .createdAt(a.createdAt())
                .updatedAt(a.updatedAt())
                .build();
    }

    public static List<ControllerAuthorDTO.Response> toResponse(List<AuthorDTO> authors) {
        Objects.requireNonNull(authors, "authors cant be null");

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

    public static AuthorDTO toDTO(Author a) {
        Objects.requireNonNull(a, "author cant be null");

        return AuthorDTO.builder()
                .id(a.getId())
                .surname(a.getSurname())
                .firstname(a.getFirstname())
                .lastname(a.getLastname())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }

    public static List<AuthorDTO> toListDTO(List<Author> authors) {
        Objects.requireNonNull(authors, "authors cant be null");

        return authors.stream().map(AuthorMapper::toDTO).collect(Collectors.toList());
    }
}
