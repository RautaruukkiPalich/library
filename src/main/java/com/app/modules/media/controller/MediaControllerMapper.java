package com.app.modules.media.controller;

import com.app.modules.media.converter.ConversionParams;
import com.app.modules.media.dto.MediaDTO;
import com.app.modules.media.dto.MediaFileDTO;
import com.app.modules.media.dto.TaskStatusDTO;
import com.app.modules.media.enums.MediaContent;
import com.app.modules.media.enums.MediaSize;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public class MediaControllerMapper {
    private final static String DOWNLOAD_URL_SIZE_TMPL = "/api/media/%s/download?size=%s";
    private final static String DOWNLOAD_URL_FILE_UUID_TMPL = "/api/media/%s/download?file_uuid=%s";

    public static MediaControllerDTO.Response.MediaItem convert(@NonNull MediaDTO dto) {
        return MediaControllerDTO.Response
                .MediaItem
                .builder()
                .mediaUuid(dto.uuid())
                .userId(dto.userId())
                .originalFilename(dto.originalFilename())
                .sizes(dto.files().stream().map(MediaControllerMapper::convert).toList())
                .totalCount(dto.files().size())
                .build();
    }

    public static MediaControllerDTO.Response.MediaSizeInfo convert(@NonNull MediaFileDTO dto) {
        List<String> urls = new ArrayList<>();
        urls.add(DOWNLOAD_URL_FILE_UUID_TMPL.formatted(dto.mediaUuid(), dto.uuid()));
        if (dto.mediaSize() != MediaSize.CUSTOM) {
            urls.add(DOWNLOAD_URL_SIZE_TMPL.formatted(dto.mediaUuid(), dto.mediaSize()));
        }

        return MediaControllerDTO.Response
                .MediaSizeInfo
                .builder()
                .contentType(dto.contentType())
                .metadata(dto.metadata())
                .size(dto.mediaSize())
                .downloadUrl(urls)
                .build();
    }


    private final static String STATUS_TASK_PATH = "/api/media/tasks/";

    public static MediaControllerDTO.Response.TaskStatus toResponse(TaskStatusDTO task) {
        return MediaControllerDTO.Response.TaskStatus.builder()
                .taskUuid(task.taskUUID())
                .mediaUuid(task.mediaUUID())
                .statusCheckUrl(STATUS_TASK_PATH + task.taskUUID())
                .status(task.status().toString())
                .failReason(task.failReason())
                .createdAt(task.createdAt())
                .updatedAt(task.updatedAt())
                .build();
    }

    public static ConversionParams convert(@NonNull MediaControllerDTO.Request.ConversionRequest req) {
        MediaContent type = MediaContent.fromName(req.getType());

        ConversionParams.ConversionParamsBuilder builder = ConversionParams.builder()
                .targetType(type)
                .targetExtension(req.getTargetExtension())
                .quality(req.getQuality() != null ? req.getQuality() : 100);

        if (req instanceof MediaControllerDTO.Request.ImageConversionRequest imageReq) {
            builder = builder
                    .width(imageReq.getWidth())
                    .height(imageReq.getHeight())
                    .keepAspectRatio(imageReq.getKeepAspectRatio())
                    .cropToSquare(imageReq.getCropToSquare());
        }

        return builder.build();
    }
}
