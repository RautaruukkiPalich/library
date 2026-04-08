package com.app.modules.media.repository;

import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.UUID;

public interface FilePersistRepository {
    Path save(@NonNull MultipartFile file, @NonNull UUID mediaUuid) throws IOException;
    Path save(@NonNull InputStream inputStream, @NonNull UUID mediaUuid, @NonNull String extension) throws IOException;
}
