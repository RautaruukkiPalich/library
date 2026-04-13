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

        MediaFile mf = media.getWithMediaSize(input.size());
        if (mf == null) {
            log.warn("file not found: media={} size={}", media.getUuid(), input.size());
            throw MediaFileNotFoundException.size(input.size());
        }

        InputStream stream = fileGetterRepository.getByRelativePath(mf.getPath());

        return new DownloadMediaDTO(stream, MediaMapper.convert(mf));
    }

    public record Input(
            @NotNull Long userId,
            @NotNull UUID mediaUuid,
            @NotNull MediaSize size
    ) {
    }
}
