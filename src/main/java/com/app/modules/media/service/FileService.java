package com.app.modules.media.service;

import com.app.core.config.AsyncConfig;
import com.app.core.exception.NotFoundException;
import com.app.modules.media.enums.MediaContent;
import com.app.modules.media.exceptions.FileNotFoundException;
import com.app.modules.media.exceptions.FileUploadException;
import com.app.modules.media.exceptions.FileValidationException;
import com.app.modules.media.metadata.Dimension;
import com.app.modules.media.repository.FileRepository;
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
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class FileService {
    private final FileRepository fileRepository;

    public String upload(@NonNull MediaSource mediaSource,
                         @NonNull UUID mediaUuid) throws IOException {
        var originalFilename = mediaSource.getOriginalFilename();
        if (originalFilename == null) {
            throw new FileValidationException("filename", "empty or null");
        }

        String extension = FileOperations.extractExtension(originalFilename);

        try (InputStream source = mediaSource.getInputStream()) {
            return upload(source, mediaUuid, extension);
        }
    }

    public String upload(@NonNull InputStream source,
                         @NonNull UUID mediaUuid,
                         String extension) {

        String generatedFileUuid = UUID.randomUUID().toString();
        String generatedFilename = extension.isBlank() ?
                generatedFileUuid :
                generatedFileUuid + "." + extension;

        try {
            return fileRepository.save(source, mediaUuid.toString(), generatedFilename).toString();
        } catch (IOException | IllegalArgumentException e) {
            log.error("failed to upload file for media {}: {}", mediaUuid, e.getMessage(), e);
            throw new FileUploadException("failed to upload file: " + e.getMessage(), e);
        }
    }

    public InputStream getByPath(@NonNull String path) throws FileNotFoundException {
        return fileRepository.find(path).orElseThrow(
                () -> FileNotFoundException.path(path));
    }

    public void delete(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            log.info("empty file path");
            return;
        }

        try {
            fileRepository.delete(relativePath);
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
            log.info("try delete file path {} for media {}", path, mediaUuid);
            fileRepository.delete(path);
            log.info("file path {} for media {} deleted success", path, mediaUuid);
            return true;
        } catch (NotFoundException e) {
            log.error("failed to delete file path {} for media {}. file does not exist", path, mediaUuid);
        } catch (IOException e) {
            log.error("failed to delete file path {} for media {} cause {}; try delete manually", path, mediaUuid, e.getMessage());
        }
        return false;
    }

    public Long fileSize(@NonNull String relativePath) {
        return fileRepository.fileSize(relativePath);
    }

    public Dimension fileDimension(@NonNull String relativePath,
                                   @NonNull MediaContent type) {
        InputStream source = fileRepository.find(relativePath).orElseThrow(
                () -> FileNotFoundException.path(relativePath));

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