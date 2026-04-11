package com.app.modules.media.controller;

import com.app.modules.media.dto.MediaDTO;
import com.app.modules.media.dto.MediaFileDTO;
import lombok.NonNull;

public class MediaControllerMapper {
    private final static String DOWNLOAD_URL_TMPL = "api/media/%s/download?size=%s";

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
        return MediaControllerDTO.Response
                .MediaSizeInfo
                .builder()
                .contentType(dto.contentType())
                .fileSize(dto.fileSize())
                .size(dto.mediaSize())
                .downloadUrl(DOWNLOAD_URL_TMPL.formatted(dto.mediaUuid(), dto.mediaSize()))
                .build();
    }
}
