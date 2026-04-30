package com.app.modules.media.usecase;

import com.app.core.usecase.BaseQueryUseCase;
import com.app.modules.media.dto.MediaDTO;
import com.app.modules.media.mapper.MediaMapper;
import com.app.modules.media.model.Media;
import com.app.modules.media.service.CheckPermissionService;
import com.app.modules.media.service.MediaService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Component
@Slf4j
@Validated
@AllArgsConstructor
public class GetMediaUseCase extends BaseQueryUseCase<GetMediaUseCase.Input, MediaDTO> {
    private final MediaService mediaService;
    private final CheckPermissionService checkPermissionService;

    @Override
    public MediaDTO execute(@NonNull Input input) {
        Media media = mediaService.getMedia(input.mediaUuid());
        checkPermissionService.checkCanView(media, input.userId());

        return MediaMapper.convert(media);
    }

    public record Input(
            @NotNull Long userId,
            @NotNull UUID mediaUuid
    ) {
    }
}
