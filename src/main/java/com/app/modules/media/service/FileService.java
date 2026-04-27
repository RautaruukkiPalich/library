package com.app.modules.media.service;

import com.app.core.config.AsyncConfig;
import com.app.core.exception.NotFoundException;
import com.app.modules.media.enums.MediaContent;
import com.app.modules.media.metadata.Dimension;
import com.app.modules.media.repository.FileDeleteRepository;
import com.app.modules.media.repository.FileGetterRepository;
import com.app.modules.media.repository.FilePersistRepository;
import com.app.modules.media.source.MediaSource;
import com.app.modules.media.utils.FileOperations;
import com.app.modules.media.utils.ImageResolutionUtil;
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
public class FileService {
    private final FileGetterRepository fileGetterRepository;
    private final FilePersistRepository filePersistRepository;
    private final FileDeleteRepository fileDeleteRepository;

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
            log.error("failed to get input stream file {} for media {}",
                    originalFilename, mediaUuid);
            throw new RuntimeException(
                    "failed to get input stream from file %s".formatted(originalFilename), e);
        }
    }

    public String upload(@NonNull InputStream stream,
                         @NonNull UUID mediaUuid,
                         @NonNull String extension) {
        try {
            Path path = filePersistRepository.save(
                    stream,
                    mediaUuid,
                    extension);
            log.debug("file saved for media {} saved storage {}", mediaUuid, path);
            return path.toString();
        } catch (IOException e) {
            log.error("failed to save file for media {} cause {}", mediaUuid, e.getMessage());
            throw new RuntimeException(
                    "failed to save file for media %s".formatted(
                            mediaUuid
                    ), e);
        }
    }

    public void delete(@NonNull String relativePath) {
        if (relativePath.isEmpty()) {
            log.info("empty file path");
        }

        try {
            fileDeleteRepository.delete(relativePath);
            log.info("file {} deleted", relativePath);
        } catch (Exception ex) {
            log.error("failed to delete file {}, try delete manually", relativePath, ex);
        }

    }

    @Async(AsyncConfig.FILE_DELETION)
    public void deleteFilesAsync(@NonNull List<String> paths, @NonNull UUID mediaUuid) {
        deleteFiles(paths, mediaUuid);
    }

    public void deleteFiles(@NonNull List<String> paths, @NonNull UUID mediaUuid) {
        if (paths.isEmpty()) {
            log.debug("no files to delete for media {}", mediaUuid);
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

        log.info("deleted {} of {} files for media {}", successCount, nonNullPaths.size(), mediaUuid);
    }

    private boolean deleteFile(@NonNull String path, @NonNull UUID mediaUuid) {
        try {
            log.info("try delete path {} media {}", path, mediaUuid);
            fileDeleteRepository.delete(path);
            log.info("file path {} deleted success", path);
            return true;
        } catch (NotFoundException e) {
            log.error("failed to delete file path {} for media {}. file does not exist", path, mediaUuid);
        } catch (IOException e) {
            log.error("failed to delete file path {} for media {} cause {}; try delete manually", path, mediaUuid, e.getMessage());
        }
        return false;
    }

    public Long fileSize(@NonNull String relativePath) {
        return fileGetterRepository.getSize(relativePath);
    }

    public Dimension fileDimension(@NonNull String relativePath,
                                   @NonNull MediaContent type) {
        InputStream source = fileGetterRepository.getByRelativePath(relativePath);
        switch (type) {
            case IMAGE -> {
                return ImageResolutionUtil.getImageDimension(source);
            }
            case VIDEO -> {//TODO: video dimension};
            }
        }
        return null;
    }
}