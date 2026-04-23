package com.app.modules.media.impl;

import com.app.modules.media.api.FileService;
import com.app.modules.media.api.MediaProcessingService;
import com.app.modules.media.converter.ImageConverter;
import com.app.modules.media.model.Media;
import com.app.modules.media.model.MediaFile;
import com.app.modules.media.model.MediaTask;
import com.app.modules.media.properties.task.MediaConvertProperties;
import com.app.modules.media.repository.*;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class MediaProcessingServiceImpl implements MediaProcessingService {
    private final TaskGetterRepository taskGetterRepository;
    private final TaskPersistRepository taskPersistRepository;

    private final MediaGetterRepository mediaGetterRepository;
    private final MediaFilePersistRepository mediaFilePersistRepository;

    private final FileGetterRepository fileGetterRepository;

    private final FileService fileService;
    private final ImageConverter imageConverter;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processTask(@NonNull UUID taskUuid) {
        MediaTask task = taskGetterRepository.getByUUID(taskUuid);
        String filePath = null;

        try {
            Media media = mediaGetterRepository.getByUuid(task.getMediaUuid());
            MediaFile original = media.getOriginal();

            MediaConvertProperties props = task.getConvertProperties();

            log.debug("props load {}", props);

            InputStream source = fileGetterRepository.getByRelativePath(original.getPath());
            InputStream res = imageConverter.convert(
                    source,
                    props);

            log.debug("file converted");

            filePath = fileService.upload(
                    res,
                    media.getUuid(),
                    props.getExtension());

            log.debug("file upload");

            Long fileSize = fileService.fileSize(filePath);

            MediaFile mf = MediaFile.create(
                    original.getFilename(),
                    props.getExtension(),
                    props.getContentType(),
                    props.getMediaSize(),
                    media,
                    fileSize,
                    filePath
            );

            log.debug("media file {} created for media={}, props={}", mf.getUuid(), media.getUuid(), props.getMediaSize());

            mf.setWidth(props.getWidth());
            mf.setHeight(props.getHeight());

            mediaFilePersistRepository.save(mf);

            log.debug("media file {} saved", mf.getUuid());

            task.setStatusCompleted();
            taskPersistRepository.save(task);
        } catch (Exception e) {
            log.error("failed execute process task cause {}", e.getMessage());

            task.setStatusFailed(e.getMessage());

            taskPersistRepository.save(task);
            if (filePath != null) {
                fileService.delete(filePath);
                log.debug("file path {} for task={} deleted cause exception", filePath, taskUuid);
            }
            log.error(e.toString());
        }
    }
}
