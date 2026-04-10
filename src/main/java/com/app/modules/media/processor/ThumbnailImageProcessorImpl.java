package com.app.modules.media.processor;

import com.app.modules.media.dto.ConvertResultDTO;
import com.app.modules.media.enums.MediaContentType;
import com.app.modules.media.enums.MediaSize;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
@Slf4j
public class ThumbnailImageProcessorImpl implements ImageProcessor {
    @Override
    public ConvertResultDTO resize(@NonNull InputStream stream,
                                   @NonNull MediaSize targetSize,
                                   @NonNull String extension) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(stream)
                .size(targetSize.getWidth(), targetSize.getHeight())
                .keepAspectRatio(targetSize.isKeepAspectRatio())
                .outputFormat(extension)
                .toOutputStream(outputStream);

        return new ConvertResultDTO(
                new ByteArrayInputStream(outputStream.toByteArray()),
                (long) outputStream.size(),
                targetSize.getWidth(),
                targetSize.getHeight()
        );
    }

    @Override
    public MediaContentType getSupportedContentType() {
        return MediaContentType.IMAGE;
    }

    @Override
    public List<String> getSupportedOutputFormats() {
        return List.of("jpg", "jpeg", "png", "webp", "bmp");
    }

    @Override
    public boolean canProcess(String contentType, String extension) {
        return contentType != null && contentType.startsWith("image/") ||
                extension != null && List.of("jpg", "jpeg", "png", "gif", "bmp", "webp").contains(extension.toLowerCase());
    }
}
