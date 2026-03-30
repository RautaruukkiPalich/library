package com.app.modules.refresh_token.repository.impl;

import com.app.modules.refresh_token.model.RefreshToken;
import com.app.modules.refresh_token.repository.RefreshTokenRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PostgresRefreshTokenRepositoryImpl implements RefreshTokenRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<RefreshToken> getByToken(String token) {
        String jpql = "SELECT rt FROM RefreshToken rt WHERE rt.token = :token";

        return Optional.ofNullable(em.createQuery(jpql, RefreshToken.class)
                .setParameter("token", token)
                .getSingleResult());
    }

    @Override
    public RefreshToken save(RefreshToken token) {
        if (token.getId() == null) {
            em.persist(token);
            return token;
        }

        return em.merge(token);
    }

    @Override
    public void revokeAllUserTokens(Long userId) {
        String jpql = "UPDATE FROM RefreshToken rt SET rt.revoked = true WHERE rt.user_id = :user_id and rt.revoked = false";

        em.createQuery(jpql)
                .setParameter("user_id", userId)
                .executeUpdate();

    }
}
