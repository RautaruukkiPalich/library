package com.app.repository;

import com.app.exception.AuthorNotFoundException;
import com.app.filter.AuthorFilter;
import com.app.model.Author;
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
public class PostgresAuthorRepository implements IAuthorRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Author> getAll() {
        String stmt = "SELECT a from Author a";
        return em.createQuery(stmt, Author.class).getResultList();
    }

    @Override
    public List<Author> getAll(AuthorFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Author> cq = cb.createQuery(Author.class);
        List<Predicate> predicates = new ArrayList<>();
        Root<Author> authors = cq.from(Author.class);

//        if (filter != null){
//
//        };

        cq.where(predicates.toArray(new Predicate[0]));
        cq.distinct(true);
        cq.orderBy(cb.asc(authors.get("id")));

        TypedQuery<Author> query = em.createQuery(cq);

        return query.getResultList();
    }

    @Override
    public Author getByID(Long id) throws AuthorNotFoundException {
        return em.find(Author.class, id);
    }

    @Override
    public Author save(Author author) {
        if (author.getId() == null){
            em.persist(author);
            return author;
        } else {
            return em.merge(author);
        }
    }

    @Override
    public void deleteByID(Long id) {
        em.remove(getByID(id));
    }
}
