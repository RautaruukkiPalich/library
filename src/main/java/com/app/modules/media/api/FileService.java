package com.app.modules.media.api;

import com.app.modules.media.source.MediaSource;
import lombok.NonNull;

import java.io.InputStream;
import java.util.UUID;

public interface FileService {
    String upload(@NonNull MediaSource mediaSource,
                  @NonNull UUID mediaUuid);

    String upload(@NonNull InputStream stream,
                  @NonNull UUID mediaUuid,
                  @NonNull String extension);

    InputStream download(@NonNull String relativePath);

    void delete(@NonNull String relativePath);

    Long fileSize(@NonNull String relativePath);
}
