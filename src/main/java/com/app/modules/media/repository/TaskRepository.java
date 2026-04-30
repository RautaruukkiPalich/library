package com.app.modules.media.repository;

import com.app.modules.media.enums.TaskStatus;
import com.app.modules.media.model.MediaTask;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends
        JpaRepository<MediaTask, UUID>,
        TaskRepositoryCustom {

    @Query("SELECT t FROM MediaTask t WHERE t.mediaUuid = :media_uuid")
    List<MediaTask> findAllByMediaUuid(@NonNull @Param("media_uuid") UUID mediaUuid);

    @Query("SELECT t FROM MediaTask t WHERE t.status = :status")
    Page<MediaTask> findAllByStatus(@NonNull @Param("status") TaskStatus status, Pageable pageable);
}
