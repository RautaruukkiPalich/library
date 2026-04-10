package com.app.modules.media.repository;

import com.app.modules.media.exceptions.FileNotFoundException;
import lombok.NonNull;

import java.io.InputStream;
import java.util.Optional;

public interface FileGetterRepository {
    Optional<InputStream> findByRelativePath(@NonNull String path);

    InputStream getByRelativePath(@NonNull String path) throws FileNotFoundException;

    Optional<Long> getSize(String path);
}
