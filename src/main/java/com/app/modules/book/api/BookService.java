package com.app.modules.book.api;

import com.app.modules.book.dto.BookDTO;
import com.app.modules.book.dto.BookFilter;
import org.jspecify.annotations.NonNull;

import java.util.List;

public interface BookService {
    List<BookDTO> getAll();

    List<BookDTO> getAll(@NonNull BookFilter filter);

    BookDTO getByID(@NonNull Long id);

    void putByID(@NonNull Long id, @NonNull BookDTO book);

    void patchByID(@NonNull Long id, @NonNull BookDTO book);

    Long add(@NonNull BookDTO book);

    void delete(@NonNull Long id);
}
