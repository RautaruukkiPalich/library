package com.app.modules.media.impl;

import com.app.modules.media.api.MediaService;
import com.app.modules.media.dto.MediaDTO;
import com.app.modules.media.dto.MediaFileDTO;
import com.app.modules.media.enums.MediaContentType;
import com.app.modules.media.enums.MediaPurpose;
import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.exceptions.MediaNotFoundException;
import com.app.modules.media.mapper.MediaMapper;
import com.app.modules.media.model.Media;
import com.app.modules.media.model.MediaFile;
import com.app.modules.media.repository.MediaFilePersistRepository;
import com.app.modules.media.repository.MediaGetterRepository;
import com.app.modules.media.repository.MediaPersistRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class MediaServiceImpl implements MediaService {
    private final MediaGetterRepository mediaGetterRepository;
    private final MediaPersistRepository mediaPersistRepository;

    private final MediaFilePersistRepository mediaFilePersistRepository;

    @Override
    public MediaFileDTO createMediaFile(
            UUID mediaUuid,
            String contentType,
            String filename,
            String extension,
            String relativePath,
            Long fileSize,
            MediaSize size
    ) {
        MediaFile mf = new MediaFile();
        mf.setUuid(UUID.randomUUID());
        mf.setFilename(filename);
        mf.setExtension(extension);
        mf.setContentType(contentType);
        mf.setMediaSize(size);
        mf.setFileSize(fileSize);
        mf.setPath(relativePath);
        mf.setMediaUuid(mediaUuid);
        mf.validate();

        MediaFile smf = mediaFilePersistRepository.save(mf);
        log.debug("media file saved: uuid={}, path={}", smf.getUuid(), smf.getPath());

        return MediaMapper.convert(smf);
    }

    @Override
    public MediaDTO createMedia(Long userId,
                                String originalFilename,
                                MediaContentType type,
                                MediaPurpose purpose) {
        Media m = new Media();
        m.setUuid(UUID.randomUUID());
        m.setUserId(userId);
        m.setOriginalFilename(originalFilename);
        m.setIsPublic(false);
        m.setMediaType(type);
        m.setPurpose(purpose);
        m.validate();

        Media savedMedia = mediaPersistRepository.save(m);
        log.debug("media saved: uuid={}", savedMedia.getUuid());

        return MediaMapper.convert(savedMedia);
    }

    @Override
    public MediaDTO getMediaByUuid(@NonNull UUID mediaUuid) {
        return mediaGetterRepository
                .findByUuid(mediaUuid)
                .map(MediaMapper::convert)
                .orElseThrow(() -> new MediaNotFoundException(mediaUuid));
    }

    @Override
    public MediaDTO getMediaByUuid(@NonNull UUID mediaUuid, @NonNull MediaSize size) {
        return mediaGetterRepository
                .findByUuid(mediaUuid)
                .map(m -> MediaMapper.convert(m, mf -> mf.getMediaSize().equals(size)))
                .orElseThrow(() -> new MediaNotFoundException(mediaUuid));
    }
}
