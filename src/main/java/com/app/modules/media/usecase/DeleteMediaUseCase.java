package com.app.modules.media.usecase;

import com.app.core.services.DeferredActionService;
import com.app.core.usecase.BaseCommandUseCase;
import com.app.modules.media.api.FileService;
import com.app.modules.media.api.MediaService;
import com.app.modules.media.model.Media;
import com.app.modules.media.model.MediaFile;
import com.app.modules.media.repository.MediaGetterRepository;
import com.app.modules.media.service.CheckPermissionService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@Validated
@AllArgsConstructor
public class DeleteMediaUseCase extends BaseCommandUseCase<DeleteMediaUseCase.Input, Void> {
    private final MediaService mediaService;
    private final FileService fileService;
    private final CheckPermissionService checkPermissionService;
    private final MediaGetterRepository mediaGetterRepository;
    private final DeferredActionService deferredActionService;


    @Override
    public Void execute(@NonNull Input input) {
        Media media = mediaGetterRepository.getByUuid(input.mediaUuid());
        checkPermissionService.checkCanEdit(media, input.userId());
        List<String> paths = media.getFiles().stream().map(MediaFile::getPath).toList();

        mediaService.delete(media);
        deferredActionService.afterCommit(() ->
                fileService.deleteFilesAsync(paths, input.mediaUuid()));

        return null;
    }

    public record Input(
            @NotNull Long userId,
            @NotNull UUID mediaUuid
    ) {
    }
}
