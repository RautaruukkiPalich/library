package com.app.modules.media.metadata;

import com.app.modules.media.api.FileService;
import com.app.modules.media.enums.MediaContent;
import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.utils.FileOperations;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class MediaMetadataService {
    private final FileService fileService;

    public MediaMetadata getMetadata(@NonNull MediaContent content,
                                     @NonNull String filePath) {
        return getMetadata(content, filePath, MediaSize.ORIGINAL);
    }

    public MediaMetadata getMetadata(@NonNull MediaContent content,
                                     @NonNull String filePath,
                                     @NonNull MediaSize size) {
        switch (content) {
            case IMAGE -> {
                return getImageMetadata(filePath, size);
            }
            case VIDEO -> {
                return getVideoMetadata(filePath, size);
            }
        }

        return null;
    }

    private MediaMetadata getImageMetadata(@NonNull String filePath,
                                           @NonNull MediaSize size) {
        String extension = FileOperations.extractExtension(filePath);
        Dimension dimension = fileService.fileDimension(filePath, MediaContent.IMAGE);
        String contentType = MediaContent.IMAGE
                .getProps()
                .extensionToContentType()
                .get(extension);

        ImageMetadataImpl md = new ImageMetadataImpl();
        md.setContentType(contentType);
        md.setHeight(dimension.height());
        md.setWidth(dimension.width());
        md.setExtension(extension);
        md.setMediaSize(size);

        return md;
    }

    private MediaMetadata getVideoMetadata(@NonNull String filePath,
                                           @NonNull MediaSize size) {
        String extension = FileOperations.extractExtension(filePath);
        Dimension dimension = fileService.fileDimension(filePath, MediaContent.VIDEO);
        String contentType = MediaContent.VIDEO
                .getProps()
                .extensionToContentType()
                .get(extension);

        VideoMetadataImpl md = new VideoMetadataImpl();
        md.setContentType(contentType);
        md.setHeight(dimension.height());
        md.setWidth(dimension.width());
        md.setExtension(extension);
        md.setMediaSize(size);

        return md;
    }
}
