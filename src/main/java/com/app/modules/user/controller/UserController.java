package com.app.modules.user.controller;

import com.app.core.annotation.PublicMethod;
import com.app.modules.user.api.UserService;
import com.app.modules.user.dto.ControllerUserDTO;
import com.app.modules.user.dto.UserDTO;
import com.app.modules.user.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Tag(name = "users", description = "users api methods")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @PublicMethod
    @Operation(summary = "get user by id")
    @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = ControllerUserDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "not found")
    public ResponseEntity<ControllerUserDTO.Response> getByID(@PathVariable Long id) {
        UserDTO user = this.userService.getByID(id);
        return ResponseEntity.ok().body(UserMapper.toResponse(user));
    }
}
