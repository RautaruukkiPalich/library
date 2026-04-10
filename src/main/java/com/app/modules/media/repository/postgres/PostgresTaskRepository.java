package com.app.modules.media.repository.postgres;

import com.app.modules.media.enums.UploadStatusType;
import com.app.modules.media.exceptions.MediaTaskNotFoundException;
import com.app.modules.media.model.MediaTask;
import com.app.modules.media.repository.TaskGetterRepository;
import com.app.modules.media.repository.TaskPersistRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class PostgresTaskRepository implements TaskGetterRepository, TaskPersistRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public MediaTask save(@NonNull MediaTask task) {
        if (task.getCreatedAt() == null) {
            em.persist(task);
            return task;
        } else {
            return em.merge(task);
        }
    }

    @Override
    public Optional<MediaTask> findByUUID(@NonNull UUID uuid) {
        try {
            return Optional.of(
                    em.createQuery(
                                    "SELECT DISTINCT t FROM MediaTask t " +
                                            "LEFT JOIN FETCH t.media " +
                                            "WHERE t.uuid = :uuid", MediaTask.class)
                            .setParameter("uuid", uuid)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }


    @Override
    public MediaTask getByUUID(@NonNull UUID uuid) throws MediaTaskNotFoundException {
        return findByUUID(uuid).orElseThrow(() -> MediaTaskNotFoundException.uuid(uuid));
    }

    @Override
    public List<MediaTask> findByStatus(@NonNull UploadStatusType status, @NonNull Integer limit) {
        Pageable page = PageRequest.of(0, limit);
        List<UUID> ids = findOldestUuidsByStatus(status, page);

        return ids.isEmpty() ?
                List.of() :
                findTasksWithMedia(ids);
    }

    List<UUID> findOldestUuidsByStatus(@NonNull UploadStatusType status, @NonNull Pageable pageable) {
        return em.createQuery(
                        "SELECT t.uuid FROM MediaTask t " +
                                "WHERE t.status = :status " +
                                "ORDER BY t.createdAt ASC", UUID.class)
                .setParameter("status", status)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    List<MediaTask> findTasksWithMedia(@NonNull List<UUID> uuids) {
        return uuids.isEmpty() ?
                List.of() :
                em.createQuery(
                                """
                                        SELECT DISTINCT t FROM MediaTask t
                                        LEFT JOIN FETCH t.media
                                        WHERE t.uuid IN :uuids
                                        """, MediaTask.class
                        )
                        .setParameter("uuids", uuids)
                        .getResultList();
    }
}
