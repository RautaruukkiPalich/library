package com.app.modules.genre.repository.impl;

import com.app.modules.genre.dto.GenreFilter;
import com.app.modules.genre.exception.GenreNotFoundException;
import com.app.modules.genre.model.Genre;
import com.app.modules.genre.repository.GenreRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class PostgresGenreRepositoryImpl implements GenreRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Genre> findAll() {
        String stmt = "SELECT g from Genre g";
        return em.createQuery(stmt, Genre.class).getResultList();
    }

    public List<Genre> findAll(@NonNull GenreFilter filter) {
        Objects.requireNonNull(filter, "filter must not be null");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Genre> cq = cb.createQuery(Genre.class);
        List<Predicate> predicates = new ArrayList<>();
        Root<Genre> genres = cq.from(Genre.class);

        cq.where(predicates.toArray(new Predicate[0]));
        cq.distinct(true);
        cq.orderBy(cb.asc(genres.get("id")));

        TypedQuery<Genre> query = em.createQuery(cq);

        return query.getResultList();
    }

    public Genre getById(@NonNull Long id) throws GenreNotFoundException {
        return findById(id).orElseThrow(() -> new GenreNotFoundException(id));
    }

    public Optional<Genre> findById(@NonNull Long id) {
        Objects.requireNonNull(id, "id must not be null");
        return Optional.ofNullable(em.find(Genre.class, id));
    }

    public Genre save(@NonNull Genre genre) {
        Objects.requireNonNull(genre, "genre must not be null");

        if (genre.getId() == null) {
            em.persist(genre);
            return genre;
        } else {
            return em.merge(genre);
        }
    }

    public void delete(@NonNull Genre genre) {
        Objects.requireNonNull(genre, "genre must not be null");
        em.remove(genre);
    }
}
