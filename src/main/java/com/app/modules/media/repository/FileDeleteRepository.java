package com.app.modules.media.repository;


import com.app.modules.media.exceptions.FileNotFoundException;
import lombok.NonNull;

public interface FileDeleteRepository {
    void delete(@NonNull String path) throws FileNotFoundException;
}
