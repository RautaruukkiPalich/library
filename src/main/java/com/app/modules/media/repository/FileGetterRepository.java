package com.app.modules.media.repository;

import com.app.modules.media.exceptions.FileNotFoundException;
import lombok.NonNull;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface FileGetterRepository {
    Optional<Resource> findByRelativePath(@NonNull String path);
    Resource getByRelativePath(@NonNull String path) throws FileNotFoundException;
}
