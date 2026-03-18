package com.app.service;

import com.app.exception.notfound.UserNotFoundException;
import com.app.model.RefreshToken;
import com.app.model.User;
import com.app.repository.IRefreshTokenRepository;
import com.app.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;

@Service
@Transactional
public class RefreshTokenService implements IRefreshTokenService {

    private final IRefreshTokenRepository refreshTokenRepository;
    private final IUserRepository userRepository;

    @Value("${jwt.refresh-ttl}")
    private long refreshTokenTtl;

    public RefreshTokenService(
            IRefreshTokenRepository refreshTokenRepository,
            IUserRepository userRepository
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public RefreshToken create(Long userId) {
        User user = this.userRepository.getByID(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        RefreshToken refreshToken = createNewRefreshToken(user);

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken rotate(String oldToken) throws RuntimeException {
        return refreshTokenRepository.getByToken(oldToken)
                .filter(t -> !t.isRevoked())
                .filter(t -> t.getExpiresAt().isAfter(OffsetDateTime.now()))
                .map(t -> {
                    t.setRevoked(true);

                    RefreshToken rt = createNewRefreshToken(t.getUser());

                    this.refreshTokenRepository.save(t);
                    return this.refreshTokenRepository.save(rt);
                })
                .orElseThrow(RuntimeException::new);
    }

    @Override
    public void revokeAllUserTokens(Long id){
        this.refreshTokenRepository.revokeAllUserTokens(id);
    }

    private String generateSecureToken() {
        byte[] randomBytes = new byte[64];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private RefreshToken createNewRefreshToken(User user){
        RefreshToken rt = new RefreshToken();
        rt.setToken(generateSecureToken());
        rt.setUser(user);
        rt.setRevoked(false);
        rt.setExpiresAt(OffsetDateTime.now().plusSeconds(refreshTokenTtl));
        return rt;
    }
}
