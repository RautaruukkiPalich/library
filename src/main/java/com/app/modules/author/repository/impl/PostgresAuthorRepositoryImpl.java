package com.app.modules.author.repository.impl;

import com.app.modules.author.dto.AuthorFilter;
import com.app.modules.author.exception.AuthorNotFoundException;
import com.app.modules.author.model.Author;
import com.app.modules.author.repository.AuthorRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@Slf4j
public class PostgresAuthorRepositoryImpl implements AuthorRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Author> findAll() {
        String stmt = "SELECT a from Author a";
        return em.createQuery(stmt, Author.class).getResultList();
    }

    public List<Author> findAll(@NonNull AuthorFilter filter) {
        Objects.requireNonNull(filter, "filter must not be null");

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

    public Author getById(@NonNull Long id) throws AuthorNotFoundException {
        return findById(id).orElseThrow(() -> new AuthorNotFoundException(id));
    }

    public Optional<Author> findById(@NonNull Long id) {
        Objects.requireNonNull(id, "id must not be null");
        return Optional.ofNullable(em.find(Author.class, id));
    }


    @Override
    @Transactional
    public Author save(@NonNull Author author) {
        Objects.requireNonNull(author, "author must not be null");

        if (author.getId() == null) {
            em.persist(author);
            return author;
        } else {
            return em.merge(author);
        }
    }

    @Override
    @Transactional
    public void delete(@NonNull Author author) {
        Objects.requireNonNull(author, "author must not be null");
        em.remove(author);
    }
}
