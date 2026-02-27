package com.app.controller;

import com.app.dto.queryparams.AuthorQueryParamsDTO;
import com.app.dto.controller.ControllerAuthorDTO;
import com.app.filter.AuthorFilter;
import com.app.mapper.filter.AuthorFilterMapper;
import com.app.mapper.AuthorMapper;
import com.app.model.Author;
import com.app.service.IAuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@Tag(name = "authors", description = "authors api methods")
public class AuthorController {
    private final IAuthorService authorService;

    public AuthorController(
            IAuthorService authorService
    ){
        this.authorService = authorService;
    }

    @GetMapping("/")
    @Operation(summary = "get all authors")
    @ApiResponse(responseCode = "200", description = "success",
            content = @Content(schema = @Schema(implementation = ControllerAuthorDTO.ListResponse.class)))
    public ResponseEntity<ControllerAuthorDTO.ListResponse> getAll() {
        List<Author> authors = this.authorService.getAll();
        List<ControllerAuthorDTO.Response> response = AuthorMapper.toResponse(authors);
        return ResponseEntity.ok().body(new ControllerAuthorDTO.ListResponse(response));
    }

    @GetMapping("/search")
    @ApiResponse(responseCode = "200", description = "success",
            content = @Content(schema = @Schema(implementation = ControllerAuthorDTO.ListResponse.class)))
    public ResponseEntity<ControllerAuthorDTO.ListResponse> search(@ModelAttribute AuthorQueryParamsDTO params){
        AuthorFilter filter = AuthorFilterMapper.toFilter(params);
        List<Author> authors = this.authorService.getAll(filter);
        List<ControllerAuthorDTO.Response> response = AuthorMapper.toResponse(authors);
        return ResponseEntity.ok().body(new ControllerAuthorDTO.ListResponse(response));
    }

    @PostMapping("/")
    @Operation(summary = "create new author")
    @ApiResponse(responseCode = "201", description = "success")
    @ApiResponse(responseCode = "400", description = "validation error")
    public ResponseEntity<Void> add(@Valid @RequestBody ControllerAuthorDTO.Create body) {
        this.authorService.add(AuthorMapper.toDTO(body));
        return ResponseEntity.created(null).build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "get author by id")
    @ApiResponse(responseCode = "200", description = "success",
            content = @Content(schema = @Schema(implementation = ControllerAuthorDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "not found")
    public ResponseEntity<ControllerAuthorDTO.Response> getByID(@PathVariable Long id) {
        Author author = this.authorService.getByID(id);
        return ResponseEntity.ok().body(AuthorMapper.toResponse(author));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete book by id")
    @ApiResponse(responseCode = "204", description = "success")
    public ResponseEntity<Void> deleteByID(@PathVariable Long id) {
        this.authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
