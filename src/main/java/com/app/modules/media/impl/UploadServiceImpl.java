package com.app.modules.media.impl;

import com.app.modules.media.api.FileService;
import com.app.modules.media.api.MediaService;
import com.app.modules.media.api.TaskService;
import com.app.modules.media.api.UploadService;
import com.app.modules.media.dto.MediaDTO;
import com.app.modules.media.dto.TaskStatusDTO;
import com.app.modules.media.dto.UploadMediaDTO;
import com.app.modules.media.enums.MediaContentType;
import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.utils.FileOperations;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
        MediaContentType type = dto.type();
        MultipartFile file = dto.file();

        try {
            type.validate(file);

            String contentType = file.getContentType();
            String originalFilename = file.getOriginalFilename();
            String filename = FileOperations.extractFilename(originalFilename);
            String extension = FileOperations.extractExtension(originalFilename);

            MediaDTO m = mediaService.createMedia(
                    dto.userId(),
                    originalFilename,
                    type,
                    dto.purpose());

            filePath = fileService.upload(file, m.uuid());

            mediaService.createMediaFile(
                    m.uuid(),
                    contentType,
                    filename,
                    extension,
                    filePath,
                    file.getSize(),
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
