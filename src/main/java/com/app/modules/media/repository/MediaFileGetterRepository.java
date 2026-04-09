package com.app.modules.media.repository;

import com.app.modules.media.exceptions.MediaFileNotFoundException;
import com.app.modules.media.model.MediaFile;
import lombok.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface MediaFileGetterRepository {
    MediaFile getByUuid(@NonNull UUID uuid) throws MediaFileNotFoundException;

    Optional<MediaFile> findByUuid(@NonNull UUID uuid);
}
