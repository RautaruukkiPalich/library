package com.app.modules.media.repository.postgres;

import com.app.modules.media.exceptions.MediaTaskNotFound;
import com.app.modules.media.model.MediaTask;
import com.app.modules.media.repository.TaskGetterRepository;
import com.app.modules.media.repository.TaskPersistRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class PostgresTaskRepository implements TaskGetterRepository, TaskPersistRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<MediaTask> findByUUID(@NonNull UUID uuid) {
        return Optional.empty();
    }

    @Override
    public MediaTask getByUUID(@NonNull UUID uuid) throws MediaTaskNotFound {
        return em.find(MediaTask.class, uuid);
    }

    @Override
    public MediaTask save(@NonNull MediaTask task) {
        em.persist(task);
        return task;
    }
}
