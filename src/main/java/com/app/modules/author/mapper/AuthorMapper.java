package com.app.modules.author.mapper;

import com.app.modules.author.dto.AuthorControllerDTO;
import com.app.modules.author.dto.AuthorDTO;
import com.app.modules.author.model.Author;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class AuthorMapper {
    public static AuthorControllerDTO.Response toResponse(AuthorDTO a) {
        Objects.requireNonNull(a, "author must not be null");

        return AuthorControllerDTO.Response.builder()
                .id(a.id())
                .surname(a.surname())
                .firstname(a.firstname())
                .lastname(a.lastname())
                .createdAt(a.createdAt())
                .updatedAt(a.updatedAt())
                .build();
    }

    public static List<AuthorControllerDTO.Response> toResponse(List<AuthorDTO> authors) {
        Objects.requireNonNull(authors, "authors must not be null");

        return authors.stream().map(AuthorMapper::toResponse).collect(Collectors.toList());
    }

    public static AuthorDTO toDTO(AuthorControllerDTO.Create dto) {
        Objects.requireNonNull(dto, "dto must not be null");

        return AuthorDTO
                .builder()
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .surname(dto.getSurname())
                .build();
    }

    public static AuthorDTO toDTO(Author a) {
        Objects.requireNonNull(a, "author must not be null");

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
        Objects.requireNonNull(authors, "authors must not be null");

        return authors.stream().map(AuthorMapper::toDTO).collect(Collectors.toList());
    }
}
