package com.app.modules.media.repository.postgres;

import com.app.modules.media.enums.TaskStatus;
import com.app.modules.media.exceptions.MediaTaskNotFoundException;
import com.app.modules.media.model.MediaTask;
import com.app.modules.media.repository.TaskDeleterRepository;
import com.app.modules.media.repository.TaskGetterRepository;
import com.app.modules.media.repository.TaskPersistRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class PostgresTaskRepository implements TaskGetterRepository, TaskPersistRepository, TaskDeleterRepository {

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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public MediaTask saveNested(@NonNull MediaTask task) {
        return save(task);
    }

    @Override
    public Optional<MediaTask> findByUUID(@NonNull UUID uuid) {
        try {
            return Optional.of(
                    em.createQuery(
                                    """
                                            SELECT DISTINCT t FROM MediaTask t
                                            WHERE t.uuid = :uuid""", MediaTask.class)
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
    public List<MediaTask> findByStatus(@NonNull TaskStatus status, @NonNull Integer limit) {
        Pageable page = PageRequest.of(0, limit);
        List<UUID> ids = findOldestUuidsByStatus(status, page);

        return ids.isEmpty() ?
                List.of() :
                findTasksWithMedia(ids);
    }

    @Override
    public List<MediaTask> findByMediaUuid(@NonNull UUID mediaUuid) {
        return em.createQuery(
                        """
                                SELECT DISTINCT t FROM MediaTask t
                                WHERE t.mediaUuid = :media_uuid""", MediaTask.class)
                .setParameter("media_uuid", mediaUuid)
                .getResultList();


    }

    @Override
    public List<MediaTask> findByUserId(@NonNull Long userId) {
        return em.createQuery(
                        """
                                SELECT DISTINCT t FROM MediaTask t
                                WHERE t.userId = :userId""", MediaTask.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    List<UUID> findOldestUuidsByStatus(@NonNull TaskStatus status, @NonNull Pageable pageable) {
        return em.createQuery(
                        """
                                SELECT t.uuid FROM MediaTask t
                                WHERE t.status = :status
                                ORDER BY t.createdAt ASC""", UUID.class)
                .setParameter("status", status)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    //TODO: add pagination
//                                        LEFT JOIN FETCH t.media
    List<MediaTask> findTasksWithMedia(@NonNull List<UUID> uuids) {
        return uuids.isEmpty() ?
                List.of() :
                em.createQuery(
                                """
                                        SELECT DISTINCT t FROM MediaTask t
                                        WHERE t.uuid IN :uuids
                                        """, MediaTask.class
                        )
                        .setParameter("uuids", uuids)
                        .getResultList();
    }

    @Override
    public void delete(@NonNull UUID taskUuid) {
        em.remove(getByUUID(taskUuid));
    }

    @Override
    public void delete(@NonNull MediaTask task) {
        em.remove(task);
    }


    @Override
    public List<MediaTask> find(@NonNull Long userId, @NonNull Pageable pageable, TaskStatus status) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MediaTask> cq = cb.createQuery(MediaTask.class);
        Root<MediaTask> root = cq.from(MediaTask.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("userId"), userId));

        if (status != null) {
            predicates.add(cb.equal(root.get("status"), status));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.asc(root.get("createdAt")));

        return em.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    @Override
    public Long count(@NonNull Long userId, TaskStatus status) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<MediaTask> root = countQuery.from(MediaTask.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("userId"), userId));

        if (status != null) {
            predicates.add(cb.equal(root.get("status"), status));
        }

        countQuery.where(predicates.toArray(new Predicate[0]));
        countQuery.select(cb.countDistinct(root));

        return em.createQuery(countQuery).getSingleResult();
    }

}
