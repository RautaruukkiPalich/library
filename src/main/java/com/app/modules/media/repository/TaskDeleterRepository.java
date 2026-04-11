package com.app.modules.media.repository;

import com.app.modules.media.model.MediaTask;
import lombok.NonNull;

import java.util.UUID;

public interface TaskDeleterRepository {
    void delete(@NonNull UUID taskUuid);

    void delete(@NonNull MediaTask task);
}
