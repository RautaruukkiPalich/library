package com.app.modules.media.repository;

import com.app.modules.media.model.Media;

import java.util.UUID;

public interface MediaDeleterRepository {
    void delete(Media media);

    void delete(UUID mediaUUID);
}
