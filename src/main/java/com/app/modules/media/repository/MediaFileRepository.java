package com.app.modules.media.repository;

import com.app.modules.media.model.MediaFile;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface MediaFileRepository extends
        JpaRepository<MediaFile, UUID>{

    @Query("SELECT mf FROM MediaFile mf JOIN FETCH mf.media WHERE mf.uuid = :uuid")
    Optional<MediaFile> findByIdWithMedia(@Param("uuid") @NonNull UUID uuid);
}
