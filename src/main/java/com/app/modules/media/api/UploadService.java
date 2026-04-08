package com.app.modules.media.api;

import com.app.modules.media.dto.TaskStatusDTO;
import com.app.modules.media.dto.UploadMediaDTO;
import lombok.NonNull;

import java.util.UUID;

public interface UploadService {
    TaskStatusDTO upload(@NonNull UploadMediaDTO dto);
    TaskStatusDTO taskStatus(@NonNull UUID taskUUID);
}
