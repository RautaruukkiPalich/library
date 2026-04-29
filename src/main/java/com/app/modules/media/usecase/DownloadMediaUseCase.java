package com.app.modules.media.usecase;

import com.app.core.usecase.BaseQueryUseCase;
import com.app.modules.media.dto.DownloadMediaDTO;
import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.exceptions.MediaFileNotFoundException;
import com.app.modules.media.mapper.MediaMapper;
import com.app.modules.media.model.Media;
import com.app.modules.media.model.MediaFile;
import com.app.modules.media.repository.FileGetterRepository;
import com.app.modules.media.repository.MediaGetterRepository;
import com.app.modules.media.service.CheckPermissionService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.io.InputStream;
import java.util.UUID;

@Component
@Slf4j
@Validated
@AllArgsConstructor
public class DownloadMediaUseCase extends BaseQueryUseCase<DownloadMediaUseCase.Input, DownloadMediaDTO> {
    private final MediaGetterRepository mediaGetterRepository;
    private final FileGetterRepository fileGetterRepository;
    private final CheckPermissionService checkPermissionService;

    @Override
    public DownloadMediaDTO execute(@NonNull Input input) {
        Media media = mediaGetterRepository.getByUuid(input.mediaUuid());
        checkPermissionService.checkCanView(media, input.userId());

        MediaFile mediaFile = findMediaFile(media, input);

        InputStream stream = fileGetterRepository.getByRelativePath(mediaFile.getPath());

        return new DownloadMediaDTO(stream, MediaMapper.convert(mediaFile));
    }

    public record Input(
            @NotNull Long userId,
            @NotNull UUID mediaUuid,
            MediaSize size,
            UUID fileUuid
    ) {
        public Input {
            if (size == null && fileUuid == null) {
                throw new IllegalArgumentException("either size or fileUuid is required");
            }
            if (size != null && fileUuid != null) {
                throw new IllegalArgumentException("only one of 'size' or 'fileUuid' can be provided");
            }
            if (size != null && size.equals(MediaSize.CUSTOM)) {
                throw new IllegalArgumentException("use fileUuid instead of size=CUSTOM");
            }
        }
    }

    private MediaFile findMediaFile(Media media, Input input) {
        if (input.size() != null) {
            return media.getWithMediaSize(input.size())
                    .orElseThrow(() -> MediaFileNotFoundException.size(input.size()));
        }

        if (input.fileUuid() != null) {
            return media.getFiles().stream()
                    .filter(f -> f.getUuid().equals(input.fileUuid()))
                    .findFirst()
                    .orElseThrow(() -> MediaFileNotFoundException.uuid(input.fileUuid()));
        }

        throw new IllegalArgumentException("either size or fileUuid must be provided");
    }
}
