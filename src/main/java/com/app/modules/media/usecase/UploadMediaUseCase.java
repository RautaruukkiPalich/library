package com.app.modules.media.usecase;

import com.app.core.usecase.BaseCommandUseCase;
import com.app.modules.media.enums.MediaContent;
import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.metadata.MediaMetadata;
import com.app.modules.media.metadata.MediaMetadataService;
import com.app.modules.media.model.Media;
import com.app.modules.media.service.FileService;
import com.app.modules.media.service.MediaService;
import com.app.modules.media.service.TaskService;
import com.app.modules.media.source.MediaSource;
import com.app.modules.media.utils.FileOperations;
import com.app.modules.media.validator.MediaValidationService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Component
@Slf4j
@Validated
@Transactional
@RequiredArgsConstructor
public class UploadMediaUseCase extends BaseCommandUseCase<UploadMediaUseCase.Input, UUID> {
    private final FileService fileService;
    private final MediaMetadataService mediaMetadataService;
    private final MediaService mediaService;
    private final MediaValidationService fileValidator;
    private final TaskService taskService;

    @Override
    public UUID execute(@NonNull Input input) {
        String filePath = null;
        MediaSource mediaSource = input.mediaSource();
        Long userId = input.userId();

        try {
            String contentType = mediaSource.getSanitizedContentType();
            MediaContent type = MediaContent.fromContentType(contentType);

            fileValidator.validate(mediaSource, type);

            String originalFilename = mediaSource.getOriginalFilename();

            Media media = mediaService.createMedia(userId, originalFilename, type);

            filePath = fileService.upload(mediaSource, media.getUuid());

            String filename = FileOperations.extractFilename(originalFilename);
            MediaMetadata md = mediaMetadataService.getMetadata(filePath);

            mediaService.createMediaFile(
                    filename, MediaSize.ORIGINAL, media, filePath, md);

            taskService.createBaseConverts(media);

            log.info("upload completed: mediaUuid={}, filepath={}",
                    media.getUuid(), filePath);

            return media.getUuid();
        } catch (
                Exception e) {
            log.error("upload failed: {}", e.getMessage(), e);
            if (filePath != null && !filePath.isBlank()) {
                fileService.delete(filePath);
            }
            throw e;
        }
    }

    public record Input(
            @NotNull @Min(1) Long userId,
            @NotNull MediaSource mediaSource
    ) {
    }
}
