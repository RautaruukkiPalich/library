package com.app.modules.media.usecase;

import com.app.core.usecase.BaseCommandUseCase;
import com.app.modules.media.api.FileService;
import com.app.modules.media.enums.MediaContent;
import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.model.Media;
import com.app.modules.media.model.MediaFile;
import com.app.modules.media.repository.MediaFilePersistRepository;
import com.app.modules.media.repository.MediaPersistRepository;
import com.app.modules.media.source.MediaSource;
import com.app.modules.media.utils.FileOperations;
import com.app.modules.media.validator.MediaValidationService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Component
@Slf4j
@Validated
@RequiredArgsConstructor
public class UploadMediaUseCase extends BaseCommandUseCase<UploadMediaUseCase.Input, UUID> {
    private final MediaPersistRepository mediaPersistRepository;
    private final MediaFilePersistRepository mediaFilePersistRepository;
    private final FileService fileService;
    private final MediaValidationService fileValidator;

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

            Media m = Media.create(userId, originalFilename, type);
            mediaPersistRepository.save(m);

            filePath = fileService.upload(mediaSource, m.getUuid());

            String filename = FileOperations.extractFilename(originalFilename);
            String extension = FileOperations.extractExtension(originalFilename);

            MediaFile mf = MediaFile.create(filename, extension, contentType, MediaSize.ORIGINAL,
                    m, mediaSource.getSize(), filePath);
            mediaFilePersistRepository.save(mf);

            log.info("upload completed: mediaUuid={}, filepath={}",
                    m.getUuid(), filePath);

            return m.getUuid();
        } catch (Exception e) {
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
