package com.app.controller;

import com.app.dto.controller.ControllerBookDTO;
import com.app.dto.controller.ControllerGenreDTO;
import com.app.dto.queryparams.GenreQueryParamsDTO;
import com.app.filter.GenreFilter;
import com.app.mapper.filter.GenreFilterMapper;
import com.app.mapper.GenreMapper;
import com.app.model.Genre;
import com.app.service.IGenreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/genres")
@Tag(name = "genres", description = "genres api methods")
public class GenreController {
    private final IGenreService genreService;

    public GenreController(
            IGenreService genreService
    ) {
        this.genreService = genreService;
    }

    @GetMapping("/")
    @Operation(summary = "get all genres")
    @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = ControllerGenreDTO.ListResponse.class)))
    public ResponseEntity<ControllerGenreDTO.ListResponse> getAll() {
        List<Genre> genres = this.genreService.getAll();
        List<ControllerGenreDTO.Response> response = GenreMapper.toResponse(genres);
        return ResponseEntity.ok().body(new ControllerGenreDTO.ListResponse(response));
    }

    @GetMapping("/search")
    @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = ControllerBookDTO.ListResponse.class)))
    public ResponseEntity<ControllerGenreDTO.ListResponse> search(@ModelAttribute GenreQueryParamsDTO params) {
        GenreFilter filter = GenreFilterMapper.toFilter(params);
        List<Genre> genres = this.genreService.getAll(filter);
        List<ControllerGenreDTO.Response> response = GenreMapper.toResponse(genres);
        return ResponseEntity.ok().body(new ControllerGenreDTO.ListResponse(response));
    }

    @PostMapping("/")
    @Operation(summary = "create new genre")
    @ApiResponse(responseCode = "201", description = "success")
    @ApiResponse(responseCode = "400", description = "validation error")
    public ResponseEntity<Void> add(@Valid @RequestBody ControllerGenreDTO.Create body) {
        Long id = this.genreService.add(GenreMapper.toDTO(body));
        return ResponseEntity.created(URI.create("/api/genre/" + id)).build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "get genre by id")
    @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = ControllerGenreDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "not found")
    public ResponseEntity<ControllerGenreDTO.Response> getByID(@PathVariable Long id) {
        Genre genre = this.genreService.getByID(id);
        return ResponseEntity.ok().body(GenreMapper.toResponse(genre));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete book by id")
    @ApiResponse(responseCode = "204", description = "success")
    public ResponseEntity<Void> deleteByID(@PathVariable Long id) {
        this.genreService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
