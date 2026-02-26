package com.app.repository;

import com.app.exception.BookNotFoundException;
import com.app.filter.BookFilter;
import com.app.model.Author;
import com.app.model.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PostgresBookRepository implements IBookRepository {

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

            if (filter.isAvailable() != null) {
                predicates.add(
                    cb.equal(book.get("isAvailable"), filter.isAvailable())
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
        } catch (Exception e) {
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
    public void deleteByID(Long id) {
        Book book = em.find(Book.class, id);
        if (book != null) {
            em.remove(book);
        } else {
            throw new BookNotFoundException(id);
        }
    }
}
