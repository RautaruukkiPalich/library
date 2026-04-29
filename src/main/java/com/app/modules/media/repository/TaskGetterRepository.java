package com.app.modules.media.repository;

import com.app.modules.media.enums.SortOrder;
import com.app.modules.media.enums.TaskStatus;
import com.app.modules.media.exceptions.MediaTaskNotFoundException;
import com.app.modules.media.model.MediaTask;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskGetterRepository {
    Optional<MediaTask> findByUUID(@NonNull UUID uuid);

    MediaTask getByUUID(@NonNull UUID uuid) throws MediaTaskNotFoundException;

    List<MediaTask> findByStatus(@NonNull TaskStatus status,
                                 @NonNull Integer limit);

    List<MediaTask> findByMediaUuid(@NonNull UUID uuid);

    List<MediaTask> findByUserId(@NonNull Long userId);

    List<MediaTask> find(@NonNull Long userId,
                         @NonNull Pageable pageable,
                         @NonNull SortOrder order,
                         TaskStatus status);

    Long count(@NonNull Long userId,
               TaskStatus status);
}
