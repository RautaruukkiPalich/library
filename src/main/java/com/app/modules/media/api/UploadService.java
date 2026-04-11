package com.app.modules.media.api;

import com.app.modules.media.dto.TaskStatusDTO;
import com.app.modules.media.dto.UploadMediaDTO;
import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    TaskStatusDTO upload(@NonNull UploadMediaDTO dto);

    TaskStatusDTO upload(@NonNull MultipartFile file,
                         @NonNull Long userId);
}
