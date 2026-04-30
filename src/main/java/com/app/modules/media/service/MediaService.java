package com.app.modules.media.service;

import com.app.modules.media.enums.MediaContent;
import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.exceptions.MediaFileNotFoundException;
import com.app.modules.media.exceptions.MediaNotFoundException;
import com.app.modules.media.metadata.MediaMetadata;
import com.app.modules.media.model.Media;
import com.app.modules.media.model.MediaFile;
import com.app.modules.media.repository.MediaFileRepository;
import com.app.modules.media.repository.MediaRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class MediaService {
    private final MediaRepository mediaRepository;
    private final MediaFileRepository mediaFileRepository;

    private final TaskService taskService;

    public Media createMedia(
            @NonNull Long userId,
            @NonNull String originalFilename,
            @NonNull MediaContent mediaType
    ) {
        Media m = Media.create(userId, originalFilename, mediaType);
        Media savedMedia = mediaRepository.save(m);

        log.debug("created media {} owner {}", savedMedia.getUuid(), savedMedia.getUserId());
        return savedMedia;
    }

    public Media getMedia(@NonNull UUID uuid) {
        return mediaRepository.findByIdWithFiles(uuid)
                .orElseThrow(() -> MediaNotFoundException.uuid(uuid));
    }

    public MediaFile createMediaFile(
            @NonNull String filename,
            @NonNull MediaSize size,
            @NonNull Media media,
            @NonNull String filePath,
            @NonNull MediaMetadata metadata
    ) {
        String contentType = MediaContent.fromExtension(metadata.getExtension())
                .getProps().getContentType(metadata.getExtension());
        MediaFile mf = MediaFile.create(
                filename,
                size,
                media,
                filePath,
                contentType,
                metadata
        );
        MediaFile savedMediaFile = mediaFileRepository.save(mf);
        log.debug("created media file {} media {}", savedMediaFile.getUuid(), savedMediaFile.getMediaUuid());
        return savedMediaFile;
    }

    public MediaFile getMediaFile(@NonNull UUID uuid) throws MediaFileNotFoundException {
        return mediaFileRepository.findById(uuid)
                .orElseThrow(() -> MediaFileNotFoundException.uuid(uuid));
    }

    public MediaFile getMediaFileWithMedia(@NonNull UUID uuid) throws MediaFileNotFoundException {
        return mediaFileRepository.findByIdWithMedia(uuid)
                .orElseThrow(() -> MediaFileNotFoundException.uuid(uuid));
    }

    public void delete(@NonNull Media media) {
        taskService.deleteByMediaUuid(media.getUuid());
        mediaRepository.delete(media);
    }
}
