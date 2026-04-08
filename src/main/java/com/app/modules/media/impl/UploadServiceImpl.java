package com.app.modules.media.impl;

import com.app.modules.media.api.UploadService;
import com.app.modules.media.dto.TaskStatusDTO;
import com.app.modules.media.dto.UploadMediaDTO;
import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.enums.UploadStatusType;
import com.app.modules.media.model.Media;
import com.app.modules.media.model.MediaFile;
import com.app.modules.media.model.MediaTask;
import com.app.modules.media.repository.*;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class UploadServiceImpl implements UploadService {

    private final TaskGetterRepository taskGetterRepository;
    private final TaskPersistRepository taskPersistRepository;

    private final FilePersistRepository filePersistRepository;
    private final FileDeleteRepository fileDeleteRepository;

    private final MediaFilePersistRepository mediaFilePersistRepository;
    private final MediaPersistRepository mediaPersistRepository;

    @Override
    public TaskStatusDTO upload(@NonNull UploadMediaDTO dto) {
        String filePath = null;

        try {
            validateUploadRequest(dto);

            Media media = createAndSaveMedia(dto);

            filePath = saveFileToDisk(dto.file(), media.getUuid());

            createAndSaveMediaFile(dto.file(), media, MediaSize.ORIGINAL, filePath);
            MediaTask task = createAndSaveTask(dto.userId(), media);

            log.info("Upload completed: mediaId={}, taskId={}, file={}, filepath={}",
                    media.getUuid(), task.getUuid(), dto.file().getOriginalFilename(), filePath);

            return TaskStatusDTO.builder()
                    .taskUUID(task.getUuid())
                    .mediaUUID(task.getMedia().getUuid())
                    .userId(task.getUserId())
                    .status(UploadStatusType.PENDING)
                    .build();

        } catch (Exception e) {
            log.error("Upload failed: {}", e.getMessage(), e);
            cleanupFile(filePath);
            throw e;
        }
    }

    @Override
    public TaskStatusDTO taskStatus(@NonNull UUID taskUUID) {
        MediaTask task = taskGetterRepository.getByUUID(taskUUID);
        return TaskStatusDTO.builder()
                .taskUUID(task.getUuid())
                .mediaUUID(task.getMedia().getUuid())
                .userId(task.getUserId())
                .status(task.getStatus())
                .build();

    }

    @Scheduled(fixedDelay = 5000, initialDelay = 10000)
    private void uploadProcessor() {
        System.out.println("convert files");
    }

    private void validateUploadRequest(UploadMediaDTO dto) {
        FileOperations.validateFile(dto.file());

        String originalFilename = dto.file().getOriginalFilename();
        Objects.requireNonNull(originalFilename, "original filename is null");
    }

    private String saveFileToDisk(@NonNull MultipartFile file, @NonNull UUID mediaUuid) {
        try {
            Path path = filePersistRepository.save(file, mediaUuid);
            log.debug("file saved to disk: {}", path);
            return path.toString();
        } catch (IOException e) {
            log.error("failed to save file {}", file.getOriginalFilename());
            throw new RuntimeException(
                    "failed to save file %s for media %s".formatted(
                            file.getOriginalFilename(),
                            mediaUuid
                    ), e);
        }
    }

    private Media createAndSaveMedia(UploadMediaDTO dto) {
        String originalFilename = dto.file().getOriginalFilename();
        Objects.requireNonNull(originalFilename, "original filename is null");

        Media media = new Media();
        media.setUuid(UUID.randomUUID());
        media.setUserId(dto.userId());
        media.setOriginalFilename(originalFilename);
        media.setIsPublic(false);
        media.setMediaType(dto.type());
        media.setPurpose(dto.purpose());
        media.validate();

        Media savedMedia = mediaPersistRepository.save(media);
        log.debug("Media saved: id={}", savedMedia.getUuid());

        return savedMedia;
    }

    private MediaFile createAndSaveMediaFile(
            @NonNull MultipartFile file, @NonNull Media media,
            @NonNull MediaSize size, @NonNull String filePath) {
        String originalFilename = file.getOriginalFilename();
        Objects.requireNonNull(originalFilename, "original filename is null");

        MediaFile mediaFile = new MediaFile();
        mediaFile.setUuid(UUID.randomUUID());
        mediaFile.setFilename(FileOperations.extractFilename(originalFilename));
        mediaFile.setExtension(FileOperations.extractExtension(originalFilename));
        mediaFile.setContentType(file.getContentType());
        mediaFile.setMediaSize(size);
        mediaFile.setFileSize(file.getSize());
        mediaFile.setPath(filePath);
        mediaFile.setMedia(media);
        mediaFile.validate();

        MediaFile savedMediaFile = mediaFilePersistRepository.save(mediaFile);
        log.debug("MediaFile saved: id={}, path={}", savedMediaFile.getUuid(), filePath);

        return savedMediaFile;
    }

    private MediaTask createAndSaveTask(@NonNull Long userId, @NonNull Media media) {
        MediaTask task = new MediaTask();
        task.setUuid(UUID.randomUUID());
        task.setUserId(userId);
        task.setStatus(UploadStatusType.PENDING);
        task.setMedia(media);
        task.validate();

        MediaTask savedTask = taskPersistRepository.save(task);
        log.debug("Task created: id={}, status={}", savedTask.getUuid(), savedTask.getStatus());

        return savedTask;
    }

    private void cleanupFile(String localFilePath) {
        if (localFilePath != null && !localFilePath.isEmpty()) {
            try {
                fileDeleteRepository.delete(localFilePath);
                log.info("Cleaned up file: {}", localFilePath);
            } catch (Exception ex) {
                log.error("Failed to cleanup file: {}", localFilePath, ex);
            }
        }
    }

}
