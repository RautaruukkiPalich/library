package com.app.modules.media.usecase;

import com.app.core.usecase.BaseCommandUseCase;
import com.app.modules.media.api.MediaService;
import com.app.modules.media.model.Media;
import com.app.modules.media.repository.MediaGetterRepository;
import com.app.modules.media.service.CheckPermissionService;
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
public class DeleteMediaUseCase extends BaseCommandUseCase<DeleteMediaUseCase.Input, Void> {
    private MediaService mediaService;
    private CheckPermissionService checkPermissionService;
    private MediaGetterRepository mediaGetterRepository;


    @Override
    public Void execute(@NonNull Input input) {
        Media media = mediaGetterRepository.getByUuid(input.mediaUuid());
        checkPermissionService.checkCanEdit(media, input.userId());
        mediaService.delete(media);

        return null;
    }

    public record Input(
            @NotNull Long userId,
            @NotNull UUID mediaUuid
    ) {
    }
}
