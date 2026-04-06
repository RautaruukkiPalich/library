package com.app.modules.book.repository.impl;

import com.app.modules.book.dto.BookFilter;
import com.app.modules.book.exception.BookNotFoundException;
import com.app.modules.book.model.Book;
import com.app.modules.book.repository.BookRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PostgresBookRepositoryImpl implements BookRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Book> findAll() {
        String jpql = "SELECT b FROM Book b JOIN FETCH b.author JOIN FETCH b.genre";
        return em.createQuery(jpql, Book.class).getResultList();
    }

    public List<Book> findAll(@NonNull BookFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        List<Predicate> predicates = new ArrayList<>();
        Root<Book> book = cq.from(Book.class);
        book.fetch("author", JoinType.LEFT);
        book.fetch("genre", JoinType.LEFT);

        if (filter.getTitle() != null && !filter.getTitle().trim().isEmpty()) {
            predicates.add(
                    cb.like(
                            cb.lower(book.get("title")),
                            "%" + filter.getTitle().toLowerCase() + "%"
                    )
            );
        }

        if (filter.getGenre() != null && !filter.getGenre().trim().isEmpty()) {
            predicates.add(
                    cb.like(
                            cb.lower(book.get("genre").get("name")),
                            "%" + filter.getGenre().toLowerCase() + "%"
                    )
            );
        }

        if (filter.getIsAvailable() != null) {
            predicates.add(
                    cb.equal(book.get("isAvailable"), filter.getIsAvailable())
            );
        }

        if (filter.getPubYearFrom() != null) {
            predicates.add(
                    cb.greaterThanOrEqualTo(book.get("pubYear"), filter.getPubYearFrom())
            );
        }

        if (filter.getPubYearTo() != null) {
            predicates.add(
                    cb.lessThanOrEqualTo(book.get("pubYear"), filter.getPubYearTo())
            );
        }


        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }

        cq.distinct(true);
        cq.orderBy(cb.asc(book.get("title")));

        return em.createQuery(cq).getResultList();
    }

    public Book getById(@NonNull Long id) {
        return findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    public Optional<Book> findById(@NonNull Long id) {
        String jpql = """
                SELECT b FROM Book b
                JOIN FETCH b.author
                JOIN FETCH b.genre
                WHERE b.id = :id
                """;


        try {
            return Optional.of(em.createQuery(jpql, Book.class)
                    .setParameter("id", id)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Book save(@NonNull Book book) {
        if (book.getId() == null) {
            em.persist(book);
            return book;
        } else {
            return em.merge(book);
        }
    }

    public void delete(@NonNull Book book) {
        em.remove(book);
    }
}
