package com.app.modules.refresh_token.repository;

import com.app.modules.refresh_token.exception.RefreshTokenNotFoundException;
import com.app.modules.refresh_token.model.RefreshToken;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public interface RefreshTokenRepository {
    Optional<RefreshToken> findByToken(@NonNull String token);

    RefreshToken getByToken(@NonNull String token) throws RefreshTokenNotFoundException;

    RefreshToken save(@NonNull RefreshToken token);

    void revokeAllUserTokens(@NonNull Long userId);
}
