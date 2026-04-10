package com.app.modules.media.repository;

import com.app.modules.media.enums.UploadStatusType;
import com.app.modules.media.exceptions.MediaTaskNotFoundException;
import com.app.modules.media.model.MediaTask;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskGetterRepository {
    Optional<MediaTask> findByUUID(@NonNull UUID uuid);

    MediaTask getByUUID(@NonNull UUID uuid) throws MediaTaskNotFoundException;

    List<MediaTask> findByStatus(@NonNull UploadStatusType status,
                                 @NonNull Integer limit);
}
