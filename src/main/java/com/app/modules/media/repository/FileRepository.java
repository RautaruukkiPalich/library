package com.app.modules.media.repository;

import com.app.modules.media.exceptions.FileNotFoundException;
import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;

public interface FileRepository {
    Path save(@NonNull InputStream inputStream,
              @NonNull String pathPrefix,
              @NonNull String filename) throws IOException;

    Optional<InputStream> find(@NonNull String path);

    Long fileSize(@NonNull String path);

    void delete(String path) throws FileNotFoundException, IOException;
}
