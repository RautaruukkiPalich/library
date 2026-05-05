package com.app.modules.media.repository;

import com.app.modules.media.model.Media;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface MediaRepository extends
        JpaRepository<Media, UUID> {

    @Query("SELECT m FROM Media m LEFT JOIN FETCH m.files WHERE m.uuid = :uuid")
    Optional<Media> findByIdWithFiles(@Param("uuid") @NonNull UUID uuid);
}
