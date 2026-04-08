package com.app.modules.media.repository;

import com.app.modules.media.model.Media;
import lombok.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface MediaGetterRepository {
    Optional<Media> findByUuid(@NonNull UUID uuid);
    Media getByUuid(@NonNull UUID uuid);
}
