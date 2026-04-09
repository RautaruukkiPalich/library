package com.app.modules.media.repository.storage;

import com.app.modules.media.exceptions.FileNotFoundException;
import com.app.modules.media.repository.FileDeleteRepository;
import com.app.modules.media.repository.FileGetterRepository;
import com.app.modules.media.repository.FilePersistRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Repository
@Slf4j
public class LocalStorageRepository implements FilePersistRepository, FileDeleteRepository, FileGetterRepository {
    private static final String LOCAL_STORAGE_PATH = "local_media_storage";
    private final Path rootLocation;

    public LocalStorageRepository() {
        this.rootLocation = Paths.get(LOCAL_STORAGE_PATH);
        init();
    }

    private void init() {
        try {
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
                log.info("created storage directory: {}", rootLocation.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("could not initialize storage location", e);
        }
    }

    @Override
    public void delete(@NonNull String path) throws FileNotFoundException {

    }

    @Override
    public Optional<Resource> findByRelativePath(@NonNull String path) {
        try {
            Path filePath = rootLocation.resolve(path);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return Optional.of(resource);
            }

            log.warn("file not found or not readable: {}", path);
            return Optional.empty();
        } catch (IOException e) {
            log.error("error accessing file: {}", path, e);
            return Optional.empty();
        }
    }

    @Override
    public Resource getByRelativePath(@NonNull String path) throws FileNotFoundException {
        return findByRelativePath(path).orElseThrow(() -> new FileNotFoundException("file not found by path: %s".formatted(path)));
    }

    @Override
    public Path save(@NonNull InputStream inputStream,
                     @NonNull UUID mediaUuid,
                     @NonNull String extension
    ) throws IOException {
        Path directoryPath = generatePathAndCreateDirectories(mediaUuid);

        String filename = extension.isBlank() ?
                "%s".formatted(UUID.randomUUID()) :
                "%s.%s".formatted(UUID.randomUUID(), extension);

        Path filePath = directoryPath.resolve(filename);

        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

        Path path = rootLocation.relativize(filePath);

        log.info("file saved: {}", filePath);

        return path;
    }

    private Path generatePathAndCreateDirectories(UUID mediaUuid) throws IOException {
        Path path = generatePathByUUID(rootLocation, mediaUuid);
        Files.createDirectories(path);
        return path;
    }

    private Path generatePathByUUID(Path root, UUID mediaUuid) {
        String uuid = mediaUuid.toString();
        String firstLevel = uuid.substring(0, 2);
        String secondLevel = uuid.substring(2, 4);

        return root
                .resolve(firstLevel)
                .resolve(secondLevel)
                .resolve(uuid);
    }
}
