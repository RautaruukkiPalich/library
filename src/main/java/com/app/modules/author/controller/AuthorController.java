package com.app.modules.author.controller;

import com.app.core.annotation.PublicMethod;
import com.app.modules.author.api.AuthorService;
import com.app.modules.author.dto.AuthorControllerDTO;
import com.app.modules.author.dto.AuthorDTO;
import com.app.modules.author.dto.AuthorFilter;
import com.app.modules.author.dto.AuthorQueryParamsDTO;
import com.app.modules.author.mapper.AuthorFilterMapper;
import com.app.modules.author.mapper.AuthorMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/authors")
@Tag(name = "authors", description = "authors api methods")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping("/")
    @PublicMethod
    @Operation(summary = "get all authors")
    @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = AuthorControllerDTO.ListResponse.class)))
    public ResponseEntity<AuthorControllerDTO.ListResponse> getAll() {
        List<AuthorDTO> authors = this.authorService.getAll();
        List<AuthorControllerDTO.Response> response = AuthorMapper.toResponse(authors);
        return ResponseEntity.ok().body(new AuthorControllerDTO.ListResponse(response));
    }

    @GetMapping("/search")
    @PublicMethod
    @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = AuthorControllerDTO.ListResponse.class)))
    public ResponseEntity<AuthorControllerDTO.ListResponse> search(@ModelAttribute AuthorQueryParamsDTO params) {
        AuthorFilter filter = AuthorFilterMapper.toFilter(params);
        List<AuthorDTO> authors = this.authorService.getAll(filter);
        List<AuthorControllerDTO.Response> response = AuthorMapper.toResponse(authors);
        return ResponseEntity.ok().body(new AuthorControllerDTO.ListResponse(response));
    }

    @PostMapping("/")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "create new author")
    @ApiResponse(responseCode = "201", description = "success")
    @ApiResponse(responseCode = "400", description = "validation error")
    public ResponseEntity<Void> add(@Valid @RequestBody AuthorControllerDTO.Create body) {
        Long id = this.authorService.add(AuthorMapper.toDTO(body));
        return ResponseEntity.created(URI.create("/api/author/" + id)).build();
    }

    @GetMapping("/{id}")
    @PublicMethod
    @Operation(summary = "get author by id")
    @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = AuthorControllerDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "not found")
    public ResponseEntity<AuthorControllerDTO.Response> getByID(@PathVariable Long id) {
        AuthorDTO author = this.authorService.getByID(id);
        return ResponseEntity.ok().body(AuthorMapper.toResponse(author));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "delete book by id")
    @ApiResponse(responseCode = "204", description = "success")
    public ResponseEntity<Void> deleteByID(@PathVariable Long id) {
        this.authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
