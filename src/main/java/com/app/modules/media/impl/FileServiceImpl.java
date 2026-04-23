package com.app.modules.media.impl;

import com.app.core.config.AsyncConfig;
import com.app.core.exception.NotFoundException;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
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
                log.error("failed to delete file={}, try delete manually", relativePath, ex);
            }
        }
    }

    @Async(AsyncConfig.FILE_DELETION)
    @Override
    public void deleteFilesAsync(@NonNull List<String> paths, @NonNull UUID mediaUuid) {
        deleteFiles(paths, mediaUuid);
    }

    @Override
    public void deleteFiles(@NonNull List<String> paths, @NonNull UUID mediaUuid) {
        if (paths.isEmpty()) {
            log.debug("no files to delete for media={}", mediaUuid);
            return;
        }

        List<String> nonNullPaths = paths.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        long successCount = nonNullPaths
                .stream()
                .filter(path -> deleteFile(path, mediaUuid))
                .count();

        log.info("deleted {} of {} files for media={}", successCount, nonNullPaths.size(), mediaUuid);
    }

    private boolean deleteFile(@NonNull String path, @NonNull UUID mediaUuid) {
        try {
            log.info("try delete path={} media={}", path, mediaUuid);
            fileDeleteRepository.delete(path);
            log.info("file path={} deleted success", path);
            return true;
        } catch (NotFoundException e) {
            log.error("failed to delete file path={} for media={}. file does not exist", path, mediaUuid);
        } catch (IOException e) {
            log.error("failed to delete file path={} for media={} cause={}; try delete manually", path, mediaUuid, e.getMessage());
        }
        return false;
    }

    @Override
    public Long fileSize(@NonNull String relativePath) {
        return fileGetterRepository.getSize(relativePath);
    }
}
