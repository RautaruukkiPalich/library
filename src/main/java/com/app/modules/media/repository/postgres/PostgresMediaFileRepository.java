package com.app.modules.media.repository.postgres;

import com.app.modules.media.exceptions.MediaFileNotFoundException;
import com.app.modules.media.model.MediaFile;
import com.app.modules.media.repository.MediaFileGetterRepository;
import com.app.modules.media.repository.MediaFilePersistRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class PostgresMediaFileRepository implements MediaFilePersistRepository, MediaFileGetterRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public MediaFile save(@NonNull MediaFile mediaFile) {
        em.persist(mediaFile);
        return mediaFile;
    }

    @Override
    public MediaFile getByUuid(@NonNull UUID uuid) throws MediaFileNotFoundException {
        return findByUuid(uuid).orElseThrow(() -> new MediaFileNotFoundException(uuid));
    }

    @Override
    public Optional<MediaFile> findByUuid(@NonNull UUID uuid) {
        try {
            return Optional.of(em.createQuery(
                            "SELECT mf FROM MediaFile mf JOIN FETCH mf.media WHERE mf.uuid = :uuid",
                            MediaFile.class
                    )
                    .setParameter("uuid", uuid)
                    .getSingleResult());
        } catch (jakarta.persistence.NoResultException e) {
            return Optional.empty();
        }
    }
}
