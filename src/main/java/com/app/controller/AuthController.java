package com.app.controller;

import com.app.dto.TokenPairDTO;
import com.app.dto.controller.ControllerAuthDTO;
import com.app.mapper.AuthMapper;
import com.app.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "auth", description = "auth api methods")
public class AuthController {

    private final IAuthService authService;

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "register new user")
    @ApiResponse(responseCode = "201", description = "success")
    @ApiResponse(responseCode = "400", description = "validation error")
    @ApiResponse(responseCode = "409", description = "duplicate")
    public ResponseEntity<Void> register(
            @Valid @RequestBody ControllerAuthDTO.Register body
    ) {
        this.authService.register(AuthMapper.toDTO(body));
        return ResponseEntity.created(URI.create("/api/auth/login")).build();
    }

    @PostMapping("/login")
    @Operation(summary = "login user")
    @ApiResponse(responseCode = "200", description = "success")
    @ApiResponse(responseCode = "400", description = "validation error")
    @ApiResponse(responseCode = "401", description = "unauthorized")
    public ResponseEntity<ControllerAuthDTO.TokenPairResponse> login(
            @Valid @RequestBody ControllerAuthDTO.Login body
    ) {
        TokenPairDTO tokens = this.authService.login(AuthMapper.toDTO(body));
        var resp = new ControllerAuthDTO.TokenPairResponse(tokens.access(), tokens.refresh());
        return ResponseEntity.ok().body(resp);
    }

    @PostMapping("/refresh-tokens")
    @Operation(summary = "refresh token pair")
    @ApiResponse(responseCode = "200", description = "success")
    @ApiResponse(responseCode = "400", description = "validation error")
    public ResponseEntity<ControllerAuthDTO.TokenPairResponse> refreshTokens(
            @Valid @RequestBody ControllerAuthDTO.RefreshTokens body
    ) {
        TokenPairDTO tokens = this.authService.refreshTokens(body.getToken());
        var resp = new ControllerAuthDTO.TokenPairResponse(tokens.access(), tokens.refresh());
        return ResponseEntity.ok().body(resp);
    }
}
