package com.app.modules.user.repository.impl;

import com.app.modules.user.model.User;
import com.app.modules.user.repository.UserDeleterRepository;
import com.app.modules.user.repository.UserGetterRepository;
import com.app.modules.user.repository.UserPersisterRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PostgresUserRepositoryImpl implements UserGetterRepository, UserPersisterRepository, UserDeleterRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<User> getByID(Long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    public Optional<User> getByEmail(String email) {
        String jpql = "SELECT u FROM User u WHERE u.email = :email";

        try {
            return Optional.of(em.createQuery(jpql, User.class)
                    .setParameter("email", email)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        String jpql = "SELECT COUNT(u) FROM User u WHERE u.email = :email";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("email", email)
                .getSingleResult();

        return count > 0;
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            em.persist(user);
            return user;
        } else {
            return em.merge(user);
        }
    }

    @Override
    public void delete(User user) {
        em.remove(user);
    }
}
