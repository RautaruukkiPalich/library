package com.app.modules.media.converter;

import com.app.modules.media.api.FileService;
import com.app.modules.media.dto.ConvertResultDTO;
import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.processor.ImageProcessor;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component
@AllArgsConstructor
@Slf4j
public class MediaConverterImpl implements MediaConverter {
    private final FileService fs;

    private final ImageProcessor imageProcessor;

    @Override
    public ConvertResultDTO convert(@NonNull UUID mediaUuid,
                                    @NonNull String pathToOriginal,
                                    @NonNull MediaSize targetSize,
                                    @NonNull String targetExtension) throws IOException {

        try (InputStream source = fs.download(pathToOriginal)) {
            String path = fs.upload(
                    imageProcessor.resize(source, targetSize, targetExtension),
                    mediaUuid, targetExtension);

            Long size = fs.fileSize(path);

            log.info("converted {} to {} size: {} bytes, path={}",
                    mediaUuid, targetSize, size, path);

            return ConvertResultDTO.builder()
                    .path(path)
                    .extension(targetExtension)
                    .size(size)
                    .build();
        } catch (IOException e) {
            log.error("failed to convert media={} cause {}", mediaUuid, e.getMessage());
            throw e;
        }
    }
}
