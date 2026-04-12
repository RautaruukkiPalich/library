package com.app.modules.media.api;

import com.app.modules.media.dto.UploadMediaDTO;
import com.app.modules.media.source.MediaSource;
import lombok.NonNull;

import java.util.UUID;

public interface UploadService {
    UUID upload(@NonNull UploadMediaDTO dto);

    UUID upload(@NonNull MediaSource mediaSource,
                @NonNull Long userId);
}
