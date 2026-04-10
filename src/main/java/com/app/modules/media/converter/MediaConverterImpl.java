package com.app.modules.media.converter;

import com.app.modules.media.dto.ConvertResultDTO;
import com.app.modules.media.dto.FileMetadata;
import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.model.MediaFile;
import com.app.modules.media.processor.ImageProcessor;
import com.app.modules.media.repository.FileGetterRepository;
import com.app.modules.media.repository.FilePersistRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.UUID;

@Component
@AllArgsConstructor
@Slf4j
public class MediaConverterImpl implements MediaConverter {
    private final FileGetterRepository fileGetterRepository;
    private final FilePersistRepository filePersistRepository;

    private final ImageProcessor imageProcessor;
    private final String targetExtension = "webp";
    private final String targetContentType = "image/webp";

    @Override
    public MediaFile convert(@NonNull MediaFile mf, @NonNull MediaSize targetSize) throws IOException {
        FileMetadata md = mf.getMetadata();

        try (InputStream originalStream = fileGetterRepository.getByRelativePath(md.relativePath())) {

            ConvertResultDTO res = imageProcessor.resize(originalStream, targetSize, targetExtension);

            Path path = filePersistRepository.save(
                    res.stream(),
                    mf.getMediaUuid(),
                    FileMetadata.builder()
                            .extension(targetExtension)
                            .build()
            );

            MediaFile newMf = createMediaFile(mf, targetSize, res.size(), path);

            log.info("converted {} to {} size: original={} bytes, new={} bytes, path={}",
                    mf.getUuid(),
                    targetSize,
                    mf.getFileSize(),
                    newMf.getFileSize(),
                    newMf.getPath()
            );

            return newMf;
        } catch (IOException e) {
            log.error("failed to convert media={} cause {}", mf.getUuid(), e.getMessage());
            throw e;
        }
    }

    public MediaFile createMediaFile(MediaFile original, MediaSize targetSize, Long fileSize, Path path) {
        FileMetadata md = original.getMetadata();

        MediaFile mf = new MediaFile();
        mf.setUuid(UUID.randomUUID());
        mf.setFilename(md.filename());
        mf.setExtension(targetExtension);
        mf.setContentType(targetContentType);
        mf.setMediaUuid(original.getMediaUuid());
        mf.setMediaSize(targetSize);
        mf.setFileSize(fileSize);
        mf.setPath(path.toString());
        return mf;
    }
}
