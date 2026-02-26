package com.app.repository;

import com.app.exception.GenreNotFoundException;
import com.app.filter.GenreFilter;
import com.app.model.Genre;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PostgresGenreRepository implements IGenreRepository{

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Genre> getAll() {
        String stmt = "SELECT g from Genre g";
        return em.createQuery(stmt, Genre.class).getResultList();
    }

    @Override
    public List<Genre> getAll(GenreFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Genre> cq = cb.createQuery(Genre.class);
        List<Predicate> predicates = new ArrayList<>();
        Root<Genre> genres = cq.from(Genre.class);

//        if (filter != null){
//
//        };

        cq.where(predicates.toArray(new Predicate[0]));
        cq.distinct(true);
        cq.orderBy(cb.asc(genres.get("id")));

        TypedQuery<Genre> query = em.createQuery(cq);

        return query.getResultList();
    }

    @Override
    public Genre getByID(Long id) throws GenreNotFoundException {
        return em.find(Genre.class, id);
    }

    @Override
    public Genre save(Genre genre) {
        if (genre.getId() == null){
            em.persist(genre);
            return genre;
        } else {
            return em.merge(genre);
        }
    }

    @Override
    public void deleteByID(Long id) {
        em.remove(getByID(id));
    }
}
