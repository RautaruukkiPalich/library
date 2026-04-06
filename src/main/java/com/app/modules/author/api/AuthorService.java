package com.app.modules.author.api;

import com.app.modules.author.dto.AuthorDTO;
import com.app.modules.author.dto.AuthorFilter;
import org.jspecify.annotations.NonNull;

import java.util.List;

public interface AuthorService {
    List<AuthorDTO> getAll();

    List<AuthorDTO> getAll(@NonNull AuthorFilter filter);

    Long add(@NonNull AuthorDTO dto);

    AuthorDTO getByID(@NonNull Long id);

    void delete(@NonNull Long id);
}
