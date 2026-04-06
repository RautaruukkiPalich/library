package com.app.modules.author.mapper;

import com.app.modules.author.dto.AuthorControllerDTO;
import com.app.modules.author.dto.AuthorDTO;
import com.app.modules.author.model.Author;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthorMapper {
    public static AuthorControllerDTO.Response toResponse(@NonNull AuthorDTO dto) {
        return AuthorControllerDTO.Response.builder()
                .id(dto.id())
                .surname(dto.surname())
                .firstname(dto.firstname())
                .lastname(dto.lastname())
                .createdAt(dto.createdAt())
                .updatedAt(dto.updatedAt())
                .build();
    }

    public static List<AuthorControllerDTO.Response> toResponse(@NonNull List<AuthorDTO> authors) {
        return authors.stream().map(AuthorMapper::toResponse).collect(Collectors.toList());
    }

    public static AuthorDTO toDTO(@NonNull AuthorControllerDTO.Create dto) {
        return AuthorDTO
                .builder()
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .surname(dto.getSurname())
                .build();
    }

    public static AuthorDTO toDTO(@NonNull Author author) {
        return AuthorDTO.builder()
                .id(author.getId())
                .surname(author.getSurname())
                .firstname(author.getFirstname())
                .lastname(author.getLastname())
                .createdAt(author.getCreatedAt())
                .updatedAt(author.getUpdatedAt())
                .build();
    }

    public static List<AuthorDTO> toListDTO(@NonNull List<Author> authors) {
        return authors.stream().map(AuthorMapper::toDTO).collect(Collectors.toList());
    }
}
