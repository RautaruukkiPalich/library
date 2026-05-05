package com.app.modules.media.repository;

import com.app.modules.media.enums.SortOrder;
import com.app.modules.media.enums.TaskStatus;
import com.app.modules.media.model.MediaTask;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskRepositoryCustom {
    long count(@NonNull Long userId,
               TaskStatus status);

    List<MediaTask> find(Long userId,
                         @NonNull Pageable pageable,
                         SortOrder sortOrder,
                         TaskStatus status);
}
