package com.app.modules.refresh_token.repository.impl;

import com.app.modules.refresh_token.exception.RefreshTokenNotFoundException;
import com.app.modules.refresh_token.model.RefreshToken;
import com.app.modules.refresh_token.repository.RefreshTokenRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;

@Repository
public class PostgresRefreshTokenRepositoryImpl implements RefreshTokenRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<RefreshToken> findByToken(@NonNull String token) {
        Objects.requireNonNull(token, "token must not be null");

        String jpql = "SELECT rt FROM RefreshToken rt WHERE rt.token = :token";

        return Optional.ofNullable(em.createQuery(jpql, RefreshToken.class)
                .setParameter("token", token)
                .getSingleResult());
    }

    public RefreshToken getByToken(@NonNull String token) throws RefreshTokenNotFoundException {
        return findByToken(token).orElseThrow(RefreshTokenNotFoundException::new);
    }

    public RefreshToken save(@NonNull RefreshToken token) {
        Objects.requireNonNull(token, "token must not be null");

        if (token.getId() == null) {
            em.persist(token);
            return token;
        }

        return em.merge(token);
    }

    public void revokeAllUserTokens(@NonNull Long userId) {
        Objects.requireNonNull(userId, "user id must not be null");

        String jpql = "UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.userId = :userId and rt.revoked = false";

        em.createQuery(jpql)
                .setParameter("userId", userId)
                .executeUpdate();

    }
}
