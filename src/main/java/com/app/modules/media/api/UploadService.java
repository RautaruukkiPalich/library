package com.app.modules.media.api;

import com.app.modules.media.dto.UploadMediaDTO;
import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UploadService {
    UUID upload(@NonNull UploadMediaDTO dto);

    UUID upload(@NonNull MultipartFile file,
                @NonNull Long userId);
}
