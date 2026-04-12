package com.app.modules.media.impl;

import com.app.modules.media.api.FileService;
import com.app.modules.media.api.MediaService;
import com.app.modules.media.api.UploadService;
import com.app.modules.media.dto.MediaDTO;
import com.app.modules.media.dto.UploadMediaDTO;
import com.app.modules.media.enums.MediaContent;
import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.source.MediaSource;
import com.app.modules.media.utils.FileOperations;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class UploadServiceImpl implements UploadService {
    private final MediaService mediaService;
    private final FileService fileService;

    @Override
    public UUID upload(@NonNull UploadMediaDTO dto) {
        return upload(dto.mediaSource(), dto.userId());
    }

    @Override
    public UUID upload(@NonNull MediaSource mediaSource,
                       @NonNull Long userId) {
        String filePath = null;

        try {
            String contentType = mediaSource.getSanitizedContentType();
            MediaContent type = MediaContent.fromContentType(contentType);

            type.validate(mediaSource);

            String originalFilename = mediaSource.getOriginalFilename();
            String filename = FileOperations.extractFilename(originalFilename);
            String extension = FileOperations.extractExtension(originalFilename);

            MediaDTO m = mediaService.createMedia(
                    userId,
                    originalFilename,
                    type);

            filePath = fileService.upload(mediaSource, m.uuid());

            mediaService.createMediaFile(
                    m.uuid(),
                    contentType,
                    filename,
                    extension,
                    filePath,
                    mediaSource.getSize(),
                    MediaSize.ORIGINAL);

            log.info("upload completed: mediaUuid={}, filepath={}",
                    m.uuid(), filePath);

            return m.uuid();
        } catch (Exception e) {
            log.error("upload failed: {}", e.getMessage(), e);
            if (filePath != null && !filePath.isBlank()) {
                log.info("try delete uploaded file={}", filePath);
                fileService.delete(filePath);
            }
            throw e;
        }
    }


}
