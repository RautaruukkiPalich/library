package com.app.modules.media.repository;

import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.UUID;

public interface FilePersistRepository {
    Path save(@NonNull InputStream inputStream,
              @NonNull UUID mediaUuid,
              String extension) throws IOException;
}
