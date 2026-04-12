package com.app.modules.media.dto;

import com.app.modules.media.source.MediaSource;
import lombok.Builder;

@Builder
public record UploadMediaDTO(
        Long userId,
        MediaSource mediaSource
) {

}

