package com.app.modules.media.processor;

import com.app.modules.media.enums.MediaContent;
import com.app.modules.media.enums.MediaSize;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Set;

@Component
@Slf4j
public class ThumbnailImageProcessorImpl implements ImageProcessor {
    @Override
    public ByteArrayInputStream resize(@NonNull InputStream source,
                                       @NonNull MediaSize size,
                                       @NonNull String extension) throws IOException {
        ByteArrayOutputStream dst = new ByteArrayOutputStream();
        resize(source, dst, size, extension);
        return new ByteArrayInputStream(dst.toByteArray());
    }

    @Override
    public void resize(@NonNull InputStream source,
                       @NonNull OutputStream dst,
                       @NonNull MediaSize targetSize,
                       @NonNull String extension) throws IOException {
        Thumbnails.of(source)
                .size(targetSize.getWidth(), targetSize.getHeight())
                .keepAspectRatio(targetSize.isKeepAspectRatio())
                .outputFormat(extension)
                .toOutputStream(dst);
    }

    @Override
    public MediaContent getSupportedContentType() {
        return MediaContent.IMAGE;
    }

    @Override
    public Set<String> getSupportedOutputFormats() {
        return MediaContent.IMAGE.getExtensions();
    }

    @Override
    public boolean canProcess(String contentType, String extension) {
        return contentType != null && contentType.startsWith("image/") ||
                extension != null && MediaContent.IMAGE.getExtensions().contains(extension.toLowerCase());
    }
}
