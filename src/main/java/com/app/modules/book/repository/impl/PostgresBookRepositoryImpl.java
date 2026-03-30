package com.app.modules.book.repository.impl;

import com.app.modules.book.dto.BookFilter;
import com.app.modules.book.exception.BookNotFoundException;
import com.app.modules.book.model.Book;
import com.app.modules.book.repository.BookRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PostgresBookRepositoryImpl implements BookRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Book> getAll() {
        String jpql = "SELECT b FROM Book b JOIN FETCH b.author JOIN FETCH b.genre";
        return em.createQuery(jpql, Book.class).getResultList();
    }

    @Override
    public List<Book> getAll(BookFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        List<Predicate> predicates = new ArrayList<>();
        Root<Book> book = cq.from(Book.class);
        book.fetch("author", JoinType.LEFT);
        book.fetch("genre", JoinType.LEFT);

        if (filter != null) {
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
        }

        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }

        cq.distinct(true);
        cq.orderBy(cb.asc(book.get("title")));

        return em.createQuery(cq).getResultList();
    }

    @Override
    public Book getByID(Long id) throws BookNotFoundException {
        String jpql = """
                SELECT b FROM Book b
                JOIN FETCH b.author
                JOIN FETCH b.genre
                WHERE b.id = :id
                """;

        try {
            return em.createQuery(jpql, Book.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new BookNotFoundException(id);
        }
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == null) {
            em.persist(book);
            return book;
        } else {
            return em.merge(book);
        }
    }

    @Override
    public void delete(Book book) {
        em.remove(book);
    }
}
