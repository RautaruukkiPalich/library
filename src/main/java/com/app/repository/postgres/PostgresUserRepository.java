package com.app.repository.postgres;

import com.app.exception.notfound.UserNotFoundException;
import com.app.model.User;
import com.app.repository.IAuthRepository;
import com.app.repository.IUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PostgresUserRepository implements IUserRepository, IAuthRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<User> getByID(Long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    public Optional<User> getByEmail(String email) throws UserNotFoundException {
        String jpql = "SELECT u FROM User u WHERE u.email = :email";

        return Optional.ofNullable(em.createQuery(jpql, User.class)
                .setParameter("email", email)
                .getSingleResult());
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
