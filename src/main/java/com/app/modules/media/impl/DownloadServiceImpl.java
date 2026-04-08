package com.app.modules.media.impl;

import com.app.core.exception.ForbiddenException;
import com.app.modules.media.api.DownloadService;
import com.app.modules.media.dto.DownloadedMediaDTO;
import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.exceptions.MediaFileNotFoundException;
import com.app.modules.media.model.Media;
import com.app.modules.media.model.MediaFile;
import com.app.modules.media.repository.FileGetterRepository;
import com.app.modules.media.repository.MediaGetterRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class DownloadServiceImpl implements DownloadService {
    private final FileGetterRepository fileGetterRepository;
    private final MediaGetterRepository mediaGetterRepository;

    @Override
    public DownloadedMediaDTO download(@NonNull UUID uuid, @NonNull Long requesterId, @NonNull MediaSize size) {
        Media m = mediaGetterRepository.getByUuid(uuid);

        if (!m.getIsPublic() && !m.getUserId().equals(requesterId)) {
            throw ForbiddenException.insufficientPermissions();
        }

        MediaFile mf = m.getWithMediaSize(size);
        if (mf == null) {
            throw new MediaFileNotFoundException("media file with size %s not found".formatted(size));
        }

        return DownloadedMediaDTO.builder()
                .ownerId(m.getUserId())
                .contentType(mf.getContentType())
                .file(fileGetterRepository.getByRelativePath(mf.getPath()))
                .fileSize(mf.getFileSize())
                .extension(mf.getExtension())
                .build();
    }
}
