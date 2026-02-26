package com.app.mapper;

import com.app.dto.AuthorDTO;
import com.app.dto.ControllerAuthorDTO;
import com.app.model.Author;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthorMapper {
    public static ControllerAuthorDTO.Response toResponse(Author author){
        ControllerAuthorDTO.Response resp = new ControllerAuthorDTO.Response();
        resp.setId(author.getId());
        resp.setFirstname(author.getFirstname());
        resp.setLastname(author.getLastname());
        resp.setSurname(author.getSurname());

        return resp;
    }

    public static List<ControllerAuthorDTO.Response> toResponse(List<Author> authors){
        return authors.stream().map(AuthorMapper::toResponse).collect(Collectors.toList());
    }

    public static AuthorDTO toDTO(ControllerAuthorDTO.Create dto){
        AuthorDTO author = new AuthorDTO();
        author.firstname = dto.getFirstname();
        author.lastname = dto.getLastname();
        author.surname = dto.getSurname();
        return author;
    }
}
