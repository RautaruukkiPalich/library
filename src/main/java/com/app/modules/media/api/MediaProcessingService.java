package com.app.modules.media.api;

import java.util.UUID;

public interface MediaProcessingService {
    void processTask(UUID taskUuid);
}
