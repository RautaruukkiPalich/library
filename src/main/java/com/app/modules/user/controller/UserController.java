package com.app.modules.user.controller;

import com.app.core.annotation.public_endpoint.PublicEndpoint;
import com.app.modules.user.api.UserProfileService;
import com.app.modules.user.api.UserService;
import com.app.modules.user.dto.ProfileDTO;
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
    private final UserProfileService userProfileService;

    @GetMapping("/{id:\\d+}")
    @PublicEndpoint
    @Operation(summary = "get user by id")
    @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = UserControllerDTO.Response.PublicProfile.class)))
    @ApiResponse(responseCode = "404", description = "not found")
    public ResponseEntity<UserControllerDTO.Response.PublicProfile> getByID(@PathVariable Long id) {
        UserDTO user = userService.getByID(id);
        return ResponseEntity.ok().body(UserMapper.toPublicResponse(user));
    }

    @GetMapping("/me")
    @SecurityRequirement(name = BEARER_SECURITY_SCHEME_NAME)
    @Operation(summary = "get profile")
    @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = UserControllerDTO.Response.PrivateProfile.class)))
    @ApiResponse(responseCode = "401")
    public ResponseEntity<UserControllerDTO.Response.PublicProfile> me(
            @AuthenticationPrincipal Long userId
    ) {
        UserDTO user = userService.getByID(userId);
        return ResponseEntity.ok().body(UserMapper.toPrivateResponse(user));
    }

    @PutMapping("/me/password")
    @SecurityRequirement(name = BEARER_SECURITY_SCHEME_NAME)
    @Operation(summary = "edit password")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "401")
    public ResponseEntity<Void> editPassword(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UserControllerDTO.Request.EditPassword body
    ) {
        userProfileService.editPassword(new ProfileDTO.EditPassword(null, userId, body.getOldPassword(), body.getPassword()));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/me/firstname")
    @SecurityRequirement(name = BEARER_SECURITY_SCHEME_NAME)
    @Operation(summary = "edit firstname")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "401")
    public ResponseEntity<Void> editFirstname(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UserControllerDTO.Request.EditFirstname body
    ) {
        userProfileService.editFirstname(new ProfileDTO.EditFirstname(null, userId, body.getFirstname()));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/me/lastname")
    @SecurityRequirement(name = BEARER_SECURITY_SCHEME_NAME)
    @Operation(summary = "edit lastname")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "401")
    public ResponseEntity<Void> editLastname(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UserControllerDTO.Request.EditLastname body
    ) {
        userProfileService.editLastname(new ProfileDTO.EditLastname(null, userId, body.getLastname()));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/me/surname")
    @SecurityRequirement(name = BEARER_SECURITY_SCHEME_NAME)
    @Operation(summary = "edit surname")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "401")
    public ResponseEntity<Void> editSurname(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UserControllerDTO.Request.EditSurname body
    ) {
        userProfileService.editSurname(new ProfileDTO.EditSurname(null, userId, body.getSurname()));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/me/email")
    @SecurityRequirement(name = BEARER_SECURITY_SCHEME_NAME)
    @Operation(summary = "edit email")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "401")
    public ResponseEntity<Void> editEmail(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UserControllerDTO.Request.EditEmail body
    ) {
        userProfileService.editEmail(new ProfileDTO.EditEmail(null, userId, body.getEmail()));
        return ResponseEntity.ok().build();
    }


}
