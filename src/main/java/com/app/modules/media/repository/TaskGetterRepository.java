package com.app.modules.media.repository;

import com.app.modules.media.exceptions.MediaTaskNotFound;
import com.app.modules.media.model.MediaTask;
import lombok.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface TaskGetterRepository {
    Optional<MediaTask> findByUUID(@NonNull UUID uuid);

    MediaTask getByUUID(@NonNull UUID uuid) throws MediaTaskNotFound;
}
