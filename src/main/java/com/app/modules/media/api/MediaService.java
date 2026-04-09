package com.app.modules.media.api;

import com.app.modules.media.dto.MediaDTO;
import com.app.modules.media.dto.MediaFileDTO;
import com.app.modules.media.enums.MediaContentType;
import com.app.modules.media.enums.MediaPurpose;
import com.app.modules.media.enums.MediaSize;
import lombok.NonNull;

import java.util.UUID;

public interface MediaService {
    MediaFileDTO createMediaFile(UUID mediaUuid,
                                 String contentType,
                                 String filename,
                                 String extension,
                                 String relativePath,
                                 Long fileSize,
                                 MediaSize size);

    MediaDTO createMedia(Long userId,
                         String originalFilename,
                         MediaContentType type,
                         MediaPurpose purpose);

    MediaDTO getMediaByUuid(@NonNull UUID mediaUuid);

    MediaDTO getMediaByUuid(@NonNull UUID mediaUuid,
                            @NonNull MediaSize size);
}
