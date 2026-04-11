package com.app.modules.media.dto;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record UploadMediaDTO(
        Long userId,
        MultipartFile file
) {

}

