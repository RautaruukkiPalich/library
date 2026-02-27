package com.app.mapper;


import com.app.dto.controller.ControllerGenreDTO;
import com.app.dto.GenreDTO;
import com.app.model.Genre;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GenreMapper {
    public static ControllerGenreDTO.Response toResponse(Genre genre){
        ControllerGenreDTO.Response resp = new ControllerGenreDTO.Response();
        resp.setId(genre.getId());
        resp.setName(genre.getName());

        return resp;
    }

    public static List<ControllerGenreDTO.Response> toResponse(List<Genre> genres){
        return genres.stream().map(GenreMapper::toResponse).collect(Collectors.toList());
    }

    public static GenreDTO toDTO(ControllerGenreDTO.Create dto){
        GenreDTO genre = new GenreDTO();
        genre.name = dto.getName();
        return genre;
    }
}
