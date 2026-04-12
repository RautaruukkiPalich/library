package com.app.modules.media.impl;

import com.app.modules.media.api.FileService;
import com.app.modules.media.dto.FileMetadata;
import com.app.modules.media.repository.FileDeleteRepository;
import com.app.modules.media.repository.FileGetterRepository;
import com.app.modules.media.repository.FilePersistRepository;
import com.app.modules.media.source.MediaSource;
import com.app.modules.media.utils.FileOperations;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class FileServiceImpl implements FileService {
    private final FileGetterRepository fileGetterRepository;
    private final FilePersistRepository filePersistRepository;
    private final FileDeleteRepository fileDeleteRepository;

    @Override
    public InputStream download(@NonNull String relativePath) {
        return fileGetterRepository.getByRelativePath(relativePath);
    }

    @Override
    public String upload(
            @NonNull MediaSource mediaSource,
            @NonNull UUID mediaUuid) {
        var originalFilename = mediaSource.getOriginalFilename();

        try {
            return upload(
                    mediaSource.getInputStream(),
                    mediaUuid,
                    FileOperations.extractExtension(originalFilename)
            );
        } catch (IOException e) {
            log.error("failed to get input stream file={} for media={}",
                    originalFilename, mediaUuid);
            throw new RuntimeException(
                    "failed to get input stream from file %s".formatted(originalFilename), e);
        }
    }

    public String upload(@NonNull InputStream stream,
                         @NonNull UUID mediaUuid,
                         @NonNull String extension) {
        try {
            FileMetadata md = FileMetadata.builder()
                    .extension(extension)
                    .build();
            Path path = filePersistRepository.save(
                    stream,
                    mediaUuid,
                    md);
            log.debug("file saved for media={} saved to disk={}", mediaUuid, path);
            return path.toString();
        } catch (IOException e) {
            log.error("failed to save file for media={}", mediaUuid);
            throw new RuntimeException(
                    "failed to save file for media %s".formatted(
                            mediaUuid
                    ), e);
        }
    }

    @Override
    public void delete(@NonNull String relativePath) {
        if (!relativePath.isEmpty()) {
            try {
                fileDeleteRepository.delete(relativePath);
                log.info("file={} deleted", relativePath);
            } catch (Exception ex) {
                log.error("failed to delete file={}", relativePath, ex);
            }
        }
    }

    @Override
    public Long fileSize(@NonNull String relativePath) {
        return fileGetterRepository.getSize(relativePath).orElse(0L);
    }
}
