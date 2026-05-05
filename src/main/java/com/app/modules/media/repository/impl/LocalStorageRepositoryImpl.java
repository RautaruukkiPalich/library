package com.app.modules.media.repository.impl;

import com.app.modules.media.exceptions.FileNotFoundException;
import com.app.modules.media.repository.FileRepository;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Repository
@Slf4j
public class LocalStorageRepositoryImpl implements FileRepository {
    private final Path rootLocation;

    @PostConstruct
    public void init() {
        try {
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
                log.info("storage directory: {}", rootLocation.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("could not initialize storage location", e);
        }
    }

    public LocalStorageRepositoryImpl(
            @Value("${storage.local.path:local_media_storage}") String localStoragePath
    ) {
        this.rootLocation = Paths.get(localStoragePath).toAbsolutePath().normalize();
    }



    @Override
    public void delete(@NonNull String path) throws FileNotFoundException, IOException {
        Path filePath = rootLocation.resolve(path).normalize();

        if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
            throw FileNotFoundException.path(path);
        }

        Files.delete(filePath);
    }

    @Override
    public Optional<InputStream> find(@NonNull String path) {
        try {
            Path filePath = rootLocation.resolve(path).normalize();

            if (!filePath.startsWith(rootLocation)) {
                log.warn("path traversal attempt: {}", path);
                return Optional.empty();
            }

            if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
                log.warn("file not found or not readable: {}", path);
                return Optional.empty();
            }

            return Optional.of(Files.newInputStream(filePath));
        } catch (IOException e) {
            log.error("error accessing file: {}", path, e);
            return Optional.empty();
        }
    }

    @Override
    public Long fileSize(@NonNull String path) {
        try {
            Path filePath = rootLocation.resolve(path).normalize();
            if (Files.exists(filePath)) {
                return Files.size(filePath);
            }
        } catch (IOException e) {
            log.error("failed to get file size: {}", path, e);
        }
        return 0L;
    }

    @Override
    public Path save(@NonNull InputStream inputStream,
                     @NonNull String pathPrefix,
                     @NonNull String filename
    ) throws IOException {
        Path directoryPath = generatePathAndCreateDirectories(pathPrefix);

        Path filePath = directoryPath.resolve(filename);

        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

        Path path = rootLocation.relativize(filePath);

        log.info("file saved: {}", filePath);

        return path;
    }

    private Path generatePathAndCreateDirectories(@NonNull String pathPrefix) throws IOException {
        if (pathPrefix.length() < 4) {
            throw new IllegalArgumentException("path prefix cannot be shorter 4 characters, present %s: '%s'"
                    .formatted(pathPrefix.length(), pathPrefix));
        }
        Path path = generatePath(rootLocation, pathPrefix);
        Files.createDirectories(path);
        return path;
    }

    private Path generatePath(Path root, String pathPrefix) {
        String firstLevel = pathPrefix.substring(0, 2);
        String secondLevel = pathPrefix.substring(2, 4);

        return root
                .resolve(firstLevel)
                .resolve(secondLevel)
                .resolve(pathPrefix);
    }
}
