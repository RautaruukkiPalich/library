package com.app.modules.media.repository;

import com.app.modules.media.enums.UploadStatus;
import com.app.modules.media.exceptions.MediaTaskNotFoundException;
import com.app.modules.media.model.MediaTask;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskGetterRepository {
    Optional<MediaTask> findByUUID(@NonNull UUID uuid);

    MediaTask getByUUID(@NonNull UUID uuid) throws MediaTaskNotFoundException;

    List<MediaTask> findByStatus(@NonNull UploadStatus status,
                                 @NonNull Integer limit);

    Optional<MediaTask> findByMediaUuid(@NonNull UUID uuid);

    MediaTask getByMediaUuid(@NonNull UUID uuid) throws MediaTaskNotFoundException;
}
