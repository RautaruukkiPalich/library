package com.app.service;

import com.app.dto.AuthorDTO;
import com.app.filter.AuthorFilter;

import java.util.List;

public interface IAuthorService {
    List<AuthorDTO> getAll();

    List<AuthorDTO> getAll(AuthorFilter filter);

    Long add(AuthorDTO dto);

    AuthorDTO getByID(Long id);

    void delete(Long id);
}
