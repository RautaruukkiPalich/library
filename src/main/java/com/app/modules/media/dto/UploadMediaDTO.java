package com.app.modules.media.dto;

import com.app.modules.media.enums.MediaContentType;
import com.app.modules.media.enums.MediaPurpose;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record UploadMediaDTO(
        Long userId,
        MediaContentType type,
        MediaPurpose purpose,
        MultipartFile file
) {

}

