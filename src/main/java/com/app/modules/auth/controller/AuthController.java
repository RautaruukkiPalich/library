package com.app.modules.auth.controller;

import com.app.core.annotation.public_endpoint.PublicEndpoint;
import com.app.modules.auth.api.AuthService;
import com.app.modules.auth.dto.AuthControllerDTO;
import com.app.modules.auth.dto.AuthMapper;
import com.app.modules.auth.dto.TokenPairDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "auth", description = "auth api methods")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @PublicEndpoint
    @Operation(summary = "register new user")
    @ApiResponse(responseCode = "201", description = "success")
    @ApiResponse(responseCode = "400", description = "validation error")
    @ApiResponse(responseCode = "409", description = "duplicate")
    public ResponseEntity<Void> register(
            @Valid @RequestBody AuthControllerDTO.Register body
    ) {
        this.authService.register(AuthMapper.toRegisterDTO(body));
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/login")
    @PublicEndpoint
    @Operation(summary = "login user")
    @ApiResponse(responseCode = "200", description = "success")
    @ApiResponse(responseCode = "400", description = "validation error")
    @ApiResponse(responseCode = "401", description = "unauthorized")
    public ResponseEntity<AuthControllerDTO.TokenPairResponse> login(
            @Valid @RequestBody AuthControllerDTO.Login body
    ) {
        TokenPairDTO tokens = this.authService.login(AuthMapper.toLoginDTO(body));
        var resp = new AuthControllerDTO.TokenPairResponse(tokens.access(), tokens.refresh());
        return ResponseEntity.ok().body(resp);
    }

    @PostMapping("/refresh-tokens")
    @PublicEndpoint
    @Operation(summary = "refresh token pair")
    @ApiResponse(responseCode = "200", description = "success")
    @ApiResponse(responseCode = "400", description = "validation error")
    public ResponseEntity<AuthControllerDTO.TokenPairResponse> refreshTokens(
            @Valid @RequestBody AuthControllerDTO.RefreshTokens body
    ) {
        TokenPairDTO tokens = this.authService.refreshTokens(body.getToken());
        var resp = new AuthControllerDTO.TokenPairResponse(tokens.access(), tokens.refresh());
        return ResponseEntity.ok().body(resp);
    }

    @PostMapping("/reset-password")
    @PublicEndpoint
    @Operation(summary = "reset password")
    @ApiResponse(responseCode = "200", description = "success")
    @ApiResponse(responseCode = "400", description = "validation error")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody AuthControllerDTO.Email body
    ){
        authService.resetPassword(body.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/revoke-tokens")
    @Operation(summary = "revoke all refresh tokens")
    @ApiResponse(responseCode = "200", description = "success")
    @ApiResponse(responseCode = "")
    public ResponseEntity<Void> revokeAllTokens(
            @AuthenticationPrincipal Long userId
    ){
        authService.revokeAllRefreshTokens(userId);
        return ResponseEntity.ok().build();
    }
}
