package com.app.modules.media.metadata;

import com.app.modules.media.converter.media.ConversionParams;
import com.app.modules.media.enums.MediaContent;
import com.app.modules.media.exceptions.MediaMetadataException;
import com.app.modules.media.service.FileService;
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

    public MediaMetadata getMetadata(@NonNull String filePath) {
        String extension = FileOperations.extractExtension(filePath);
        MediaContent content = MediaContent.fromExtension(extension);
        return getMetadata(content, filePath);
    }

    public MediaMetadata getMetadata(@NonNull MediaContent content,
                                     @NonNull String filePath) {
        return switch (content) {
            case IMAGE -> getImageMetadata(filePath);
            case VIDEO -> getVideoMetadata(filePath);
        };
    }

    private MediaMetadata getImageMetadata(@NonNull String filePath) {
        String extension = FileOperations.extractExtension(filePath);
        Dimension dimension = fileService.fileDimension(filePath, MediaContent.IMAGE);
        if (dimension == null || dimension.width() == null || dimension.height() == null) {
            log.error("failed to get dimensions for file: {}", filePath);
            throw new MediaMetadataException("cannot read image dimensions");
        }

        Long fileSize = fileService.fileSize(filePath);

        String contentType = MediaContent.IMAGE
                .getProps()
                .extensionToContentType()
                .get(extension);

        if (contentType == null) {
            throw new MediaMetadataException("cant cast image extension=%s to content type".formatted(extension));
        }

        ImageMetadataImpl md = new ImageMetadataImpl();
        md.setHeight(dimension.height());
        md.setWidth(dimension.width());
        md.setExtension(extension);
        md.setFileSize(fileSize);

        return md;
    }

    private MediaMetadata getVideoMetadata(@NonNull String filePath) {
        String extension = FileOperations.extractExtension(filePath);
        Dimension dimension = fileService.fileDimension(filePath, MediaContent.VIDEO);
        if (dimension == null || dimension.width() == null || dimension.height() == null) {
            log.error("failed to get dimensions for file: {}", filePath);
            throw new MediaMetadataException("cannot read video dimensions");
        }

        String contentType = MediaContent.VIDEO
                .getProps()
                .extensionToContentType()
                .get(extension);

        if (contentType == null) {
            throw new MediaMetadataException("cant cast video extension=%s to content type".formatted(extension));
        }

        Long fileSize = fileService.fileSize(filePath);

        VideoMetadataImpl md = new VideoMetadataImpl();
        md.setHeight(dimension.height());
        md.setWidth(dimension.width());
        md.setExtension(extension);
        md.setFileSize(fileSize);

        return md;
    }

    public boolean canConvert(@NonNull MediaMetadata md,
                              @NonNull ConversionParams cp) {
        return switch (cp.getTargetType()) {
            case IMAGE -> md instanceof ImageMetadataImpl metadata &&
                    metadata.getWidth() >= cp.getWidth() &&
                    metadata.getHeight() >= cp.getHeight();

            case VIDEO -> false;
        };
    }
}
