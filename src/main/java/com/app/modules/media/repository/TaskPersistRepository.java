package com.app.modules.media.repository;

import com.app.modules.media.model.MediaTask;
import lombok.NonNull;

public interface TaskPersistRepository {
    MediaTask save(@NonNull MediaTask task);

    MediaTask saveNested(@NonNull MediaTask task);
}
