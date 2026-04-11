package com.app.modules.media.repository.postgres;

import com.app.modules.media.exceptions.MediaNotFoundException;
import com.app.modules.media.model.Media;
import com.app.modules.media.repository.MediaDeleterRepository;
import com.app.modules.media.repository.MediaGetterRepository;
import com.app.modules.media.repository.MediaPersistRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class PostgresMediaRepository implements MediaPersistRepository, MediaGetterRepository, MediaDeleterRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Media save(@NonNull Media media) {
        if (media.getCreatedAt() == null) {
            em.persist(media);
            return media;
        }
        return em.merge(media);
    }

    @Override
    public Optional<Media> findByUuid(@NonNull UUID uuid) {
        try {
            return Optional.of(em.createQuery(
                            "SELECT m FROM Media m LEFT JOIN FETCH m.files WHERE m.uuid = :uuid",
                            Media.class
                    )
                    .setParameter("uuid", uuid)
                    .getSingleResult());
        } catch (jakarta.persistence.NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Media getByUuid(@NonNull UUID uuid) {
        return findByUuid(uuid).orElseThrow(() -> MediaNotFoundException.uuid(uuid));
    }

    @Override
    public boolean exist(@NonNull UUID uuid) {
        Long count = em.createQuery(
                        "SELECT COUNT(m) FROM Media m WHERE m.uuid = :uuid",
                        Long.class)
                .setParameter("uuid", uuid)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public void delete(@NonNull Media media) {
        em.remove(media);
    }

    @Override
    public void delete(@NonNull UUID uuid) {
        findByUuid(uuid).ifPresent(this::delete);
    }
}
