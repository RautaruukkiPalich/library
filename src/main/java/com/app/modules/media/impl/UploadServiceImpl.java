package com.app.modules.media.impl;

import com.app.modules.media.api.FileService;
import com.app.modules.media.api.MediaService;
import com.app.modules.media.api.TaskService;
import com.app.modules.media.api.UploadService;
import com.app.modules.media.dto.MediaDTO;
import com.app.modules.media.dto.TaskStatusDTO;
import com.app.modules.media.dto.UploadMediaDTO;
import com.app.modules.media.enums.MediaSize;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class UploadServiceImpl implements UploadService {

    private final TaskService taskService;
    private final MediaService mediaService;
    private final FileService fileService;

    @Override
    public TaskStatusDTO upload(@NonNull UploadMediaDTO dto) {
        String filePath = "";

        try {
            FileOperations.validateFile(dto.file());
            String originalFilename = dto.file().getOriginalFilename();
            Objects.requireNonNull(originalFilename, "must not be null");

            String filename = FileOperations.extractFilename(originalFilename);
            String extension = FileOperations.extractExtension(originalFilename);
            String contentType = dto.file().getContentType();

            MediaDTO m = mediaService.createMedia(
                    dto.userId(),
                    originalFilename,
                    dto.type(),
                    dto.purpose());

            filePath = fileService.upload(dto.file(), m.uuid());

            mediaService.createMediaFile(
                    m.uuid(),
                    contentType,
                    filename,
                    extension,
                    filePath,
                    dto.file().getSize(),
                    MediaSize.ORIGINAL);

            TaskStatusDTO task = taskService.create(dto.userId(), m.uuid());

            log.info("upload completed: mediaId={}, taskId={}, filepath={}",
                    task.mediaUUID(), task.taskUUID(), filePath);

            return task;
        } catch (Exception e) {
            log.error("upload failed: {}", e.getMessage(), e);
            fileService.delete(filePath);
            throw e;
        }
    }


}
