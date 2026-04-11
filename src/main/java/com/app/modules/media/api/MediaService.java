package com.app.modules.media.api;

import com.app.modules.media.dto.MediaDTO;
import com.app.modules.media.dto.MediaFileDTO;
import com.app.modules.media.enums.MediaContent;
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
                         MediaContent type);

    MediaDTO getMediaByUuid(@NonNull UUID mediaUuid);

    MediaDTO getMediaByUuid(@NonNull UUID mediaUuid,
                            @NonNull MediaSize size);

    void delete(@NonNull UUID mediaUuid);
}
