package com.app.repository.postgres;

import com.app.model.RefreshToken;
import com.app.repository.IRefreshTokenRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PostgresRefreshTokenRepository implements IRefreshTokenRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<RefreshToken> getByToken(String token) {
        String jpql = "SELECT rt FROM RefreshToken rt JOIN FETCH rt.user WHERE rt.token = :token";

        return Optional.ofNullable(em.createQuery(jpql, RefreshToken.class)
                .setParameter("token", token)
                .getSingleResult());
    }

    @Override
    public RefreshToken save(RefreshToken token) {
        if (token.getId() == null){
            em.persist(token);
            return token;
        }

        return em.merge(token);
    }

    @Override
    public void revokeAllUserTokens(Long userId) {
        String jpql = "UPDATE FROM RefreshToken rt SET rt.revoked = true WHERE rt.user.id = :id and rt.revoked = false";

        em.createQuery(jpql)
                .setParameter("user_id", userId)
                .executeUpdate();

    }
}
