package com.app.modules.media.api;

import com.app.modules.media.dto.TaskStatusDTO;
import com.app.modules.media.dto.UploadMediaDTO;
import lombok.NonNull;

public interface UploadService {
    TaskStatusDTO upload(@NonNull UploadMediaDTO dto);
}
