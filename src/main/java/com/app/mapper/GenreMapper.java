package com.app.mapper;


import com.app.dto.GenreDTO;
import com.app.dto.controller.ControllerGenreDTO;
import com.app.model.Genre;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class GenreMapper {
    public static ControllerGenreDTO.Response toResponse(GenreDTO genre) {
        Objects.requireNonNull(genre, "genre cant be null");

        return ControllerGenreDTO.Response.builder()
                .id(genre.id())
                .name(genre.name())
                .createdAt(genre.createdAt())
                .updatedAt(genre.updatedAt())
                .build();
    }

    public static List<ControllerGenreDTO.Response> toResponse(List<GenreDTO> genres) {
        return genres.stream().map(GenreMapper::toResponse).collect(Collectors.toList());
    }

    public static GenreDTO toDTO(ControllerGenreDTO.Create dto) {
        Objects.requireNonNull(dto, "dto cant be null");

        return GenreDTO.builder().name(dto.getName()).build();
    }

    public static GenreDTO toDTO(Genre genre) {
        Objects.requireNonNull(genre, "genre cant be null");
        return GenreDTO.builder()
                .id(genre.getId())
                .name(genre.getName())
                .createdAt(genre.getCreatedAt())
                .updatedAt(genre.getUpdatedAt())
                .build();
    }

    public static List<GenreDTO> toListDTO(List<Genre> genres) {
        Objects.requireNonNull(genres, "genres cant be null");
        return genres.stream().map(GenreMapper::toDTO).collect(Collectors.toList());
    }
}
