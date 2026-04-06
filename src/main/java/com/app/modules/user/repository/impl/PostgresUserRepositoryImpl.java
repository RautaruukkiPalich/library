package com.app.modules.user.repository.impl;

import com.app.modules.user.exception.UserNotFoundException;
import com.app.modules.user.model.User;
import com.app.modules.user.repository.UserDeleterRepository;
import com.app.modules.user.repository.UserGetterRepository;
import com.app.modules.user.repository.UserPersisterRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PostgresUserRepositoryImpl implements UserGetterRepository, UserPersisterRepository, UserDeleterRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<User> findById(@NonNull Long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    public User getById(@NonNull Long id) {
        return findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    public Optional<User> findByEmail(@NonNull String email) {
        String jpql = "SELECT u FROM User u WHERE u.email = :email";

        try {
            return Optional.of(em.createQuery(jpql, User.class)
                    .setParameter("email", email)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public User getByEmail(@NonNull String email) {
        return findByEmail(email).orElseThrow(() -> new UserNotFoundException("email", email));
    }

    public boolean existsByEmail(@NonNull String email) {
        String jpql = "SELECT COUNT(u) FROM User u WHERE u.email = :email";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("email", email)
                .getSingleResult();

        return count > 0;
    }

    public User save(@NonNull User user) {
        if (user.getId() == null) {
            em.persist(user);
            return user;
        } else {
            return em.merge(user);
        }
    }

    public void delete(@NonNull User user) {
        em.remove(user);
    }
}
