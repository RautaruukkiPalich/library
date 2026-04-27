package com.app.modules.media.service;

import com.app.modules.media.enums.MediaContent;
import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.metadata.MediaMetadata;
import com.app.modules.media.model.Media;
import com.app.modules.media.model.MediaFile;
import com.app.modules.media.repository.MediaDeleterRepository;
import com.app.modules.media.repository.MediaFilePersistRepository;
import com.app.modules.media.repository.MediaPersistRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class MediaService {
    private MediaPersistRepository mediaPersistRepository;
    private MediaFilePersistRepository mediaFilePersistRepository;
    private final MediaDeleterRepository mediaDeleterRepository;

    private final TaskService ts;

    public Media createMedia(
            @NonNull Long userId,
            @NonNull String originalFilename,
            @NonNull MediaContent mediaType
    ) {
        Media m = Media.create(userId, originalFilename, mediaType);
        Media savedMedia = mediaPersistRepository.save(m);

        log.debug("created media {} owner {}", savedMedia.getUuid(), savedMedia.getUserId());
        return savedMedia;
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
        MediaFile savedMediaFile = mediaFilePersistRepository.save(mf);
        log.debug("created media file {} media {}", savedMediaFile.getUuid(), savedMediaFile.getMediaUuid());
        return savedMediaFile;
    }

    public void delete(@NonNull Media media) {
        ts.deleteByMediaUuid(media.getUuid());
        mediaDeleterRepository.delete(media);
    }
}
