package com.app.modules.genre.controller;

import com.app.modules.book.dto.BookControllerDTO;
import com.app.modules.genre.api.GenreService;
import com.app.modules.genre.dto.ControllerGenreDTO;
import com.app.modules.genre.dto.GenreDTO;
import com.app.modules.genre.dto.GenreFilter;
import com.app.modules.genre.dto.GenreQueryParamsDTO;
import com.app.modules.genre.mapper.GenreFilterMapper;
import com.app.modules.genre.mapper.GenreMapper;
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
    private final GenreService genreService;

    public GenreController(
            GenreService genreService
    ) {
        this.genreService = genreService;
    }

    @GetMapping("/")
    @Operation(summary = "get all genres")
    @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = ControllerGenreDTO.ListResponse.class)))
    public ResponseEntity<ControllerGenreDTO.ListResponse> getAll() {
        List<GenreDTO> genres = this.genreService.getAll();
        List<ControllerGenreDTO.Response> response = GenreMapper.toResponse(genres);
        return ResponseEntity.ok().body(new ControllerGenreDTO.ListResponse(response));
    }

    @GetMapping("/search")
    @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = BookControllerDTO.ListResponse.class)))
    public ResponseEntity<ControllerGenreDTO.ListResponse> search(@ModelAttribute GenreQueryParamsDTO params) {
        GenreFilter filter = GenreFilterMapper.toFilter(params);
        List<GenreDTO> genres = this.genreService.getAll(filter);
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
        GenreDTO genre = this.genreService.getByID(id);
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
