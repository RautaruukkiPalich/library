package com.app.modules.author.api;

import com.app.modules.author.dto.AuthorDTO;
import com.app.modules.author.dto.AuthorFilter;

import java.util.List;

public interface AuthorService {
    List<AuthorDTO> getAll();

    List<AuthorDTO> getAll(AuthorFilter filter);

    Long add(AuthorDTO dto);

    AuthorDTO getByID(Long id);

    void delete(Long id);
}
