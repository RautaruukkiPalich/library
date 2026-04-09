package com.app.modules.media.api;

import lombok.NonNull;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

public interface FileService {
    String upload(@NonNull MultipartFile file,
                  @NonNull UUID mediaUuid);

    String upload(@NonNull InputStream stream,
                  @NonNull UUID mediaUuid,
                  @NonNull String extension);

    Resource download(@NonNull String relativePath);

    void delete(@NonNull String relativePath);
}
