package com.app.modules.media.mapper;

import com.app.modules.media.dto.MediaDTO;
import com.app.modules.media.dto.MediaFileDTO;
import com.app.modules.media.model.Media;
import com.app.modules.media.model.MediaFile;
import lombok.NonNull;

import java.util.function.Predicate;

public class MediaMapper {
    public static MediaDTO convert(@NonNull Media m) {
        return builder(m)
                .files(
                        m.getFiles().stream()
                                .map(MediaMapper::convert)
                                .toList()
                )
                .build();
    }

    public static MediaDTO convert(@NonNull Media m, Predicate<MediaFile> filter) {
        return builder(m)
                .files(
                        m.getFiles().stream()
                                .filter(filter)
                                .map(MediaMapper::convert)
                                .toList()
                )
                .build();
    }

    private static MediaDTO.MediaDTOBuilder builder(@NonNull Media m) {
        return MediaDTO.builder()
                .uuid(m.getUuid())
                .userId(m.getUserId())
                .originalFilename(m.getOriginalFilename())
                .isPublic(m.getIsPublic())
                .mediaContent(m.getMediaContent())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt());
    }

    public static MediaFileDTO convert(@NonNull MediaFile mf) {
        return MediaFileDTO.builder()
                .uuid(mf.getUuid())
                .filename(mf.getFilename())
                .metadata(mf.getMetadata())
                .mediaUuid(mf.getMediaUuid())
                .mediaSize(mf.getMediaSize())
                .path(mf.getPath())
                .contentType(mf.getContentType())
                .createdAt(mf.getCreatedAt())
                .updatedAt(mf.getUpdatedAt())
                .build();
    }
}
