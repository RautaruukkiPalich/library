package com.app.modules.user.controller;

import com.app.core.annotation.public_endpoint.PublicEndpoint;
import com.app.modules.user.api.UserService;
import com.app.modules.user.dto.UserControllerDTO;
import com.app.modules.user.dto.UserDTO;
import com.app.modules.user.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.app.core.config.OpenAPIConfig.BEARER_SECURITY_SCHEME_NAME;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@Tag(name = "users", description = "users api methods")
public class UserController {
    private final UserService userService;

    @GetMapping("/{id:\\d+}")
    @PublicEndpoint
    @Operation(summary = "get user by id")
    @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = UserControllerDTO.PublicResponse.class)))
    @ApiResponse(responseCode = "404", description = "not found")
    public ResponseEntity<UserControllerDTO.PublicResponse> getByID(@PathVariable Long id) {
        UserDTO user = userService.getByID(id);
        return ResponseEntity.ok().body(UserMapper.toPublicResponse(user));
    }

    @GetMapping("/me")
    @SecurityRequirement(name = BEARER_SECURITY_SCHEME_NAME)
    @Operation(summary = "get profile")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "401")
    public ResponseEntity<UserControllerDTO.PrivateResponse> me(
            @AuthenticationPrincipal Long userId
    ) {
        UserDTO user = userService.getByID(userId);
        return ResponseEntity.ok().body(UserMapper.toPrivateResponse(user));
    }

    @PostMapping("/me/change-password")
    @SecurityRequirement(name = BEARER_SECURITY_SCHEME_NAME)
    @Operation(summary = "change password")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "401")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UserControllerDTO.ChangePassword body
    ) {
        userService.changePassword(userId, body.getOldPassword(), body.getPassword());
        return ResponseEntity.ok().build();
    }
}
