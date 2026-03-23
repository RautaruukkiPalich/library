package com.app.modules.book.controller;

import com.app.core.annotation.public_endpoint.PublicEndpoint;
import com.app.modules.book.api.BookService;
import com.app.modules.book.dto.BookControllerDTO;
import com.app.modules.book.dto.BookDTO;
import com.app.modules.book.dto.BookFilter;
import com.app.modules.book.dto.BookQueryParamsDTO;
import com.app.modules.book.mapper.BookFilterMapper;
import com.app.modules.book.mapper.BookMapper;
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

import static com.app.core.config.OpenAPIConfig.BEARER_SECURITY_SCHEME_NAME;

@RestController
@RequestMapping("/api/books")
@Tag(name = "books", description = "books api methods")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/")
    @PublicEndpoint
    @Operation(summary = "get all books")
    @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = BookControllerDTO.ListResponse.class)))
    public ResponseEntity<BookControllerDTO.ListResponse> getAll() {
        List<BookDTO> books = this.bookService.getAll();
        List<BookControllerDTO.Response> response = BookMapper.toResponse(books);
        return ResponseEntity.ok().body(new BookControllerDTO.ListResponse(response));
    }

    @GetMapping("/search")
    @PublicEndpoint
    @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = BookControllerDTO.ListResponse.class)))
    public ResponseEntity<BookControllerDTO.ListResponse> search(
            @ModelAttribute BookQueryParamsDTO.TitleGenre params
    ) {
        BookFilter filter = BookFilterMapper.toFilter(params);
        List<BookDTO> books = this.bookService.getAll(filter);
        List<BookControllerDTO.Response> response = BookMapper.toResponse(books);
        return ResponseEntity.ok().body(new BookControllerDTO.ListResponse(response));
    }

    @GetMapping("/available")
    @PublicEndpoint
    @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = BookControllerDTO.ListResponse.class)))
    public ResponseEntity<BookControllerDTO.ListResponse> available() {
        BookFilter filter = BookFilterMapper.available();
        List<BookDTO> books = this.bookService.getAll(filter);
        List<BookControllerDTO.Response> response = BookMapper.toResponse(books);
        return ResponseEntity.ok().body(new BookControllerDTO.ListResponse(response));
    }

    @GetMapping("/year/{year}")
    @PublicEndpoint
    @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = BookControllerDTO.ListResponse.class)))
    public ResponseEntity<BookControllerDTO.ListResponse> year(
            @PathVariable Integer year
    ) {
        BookFilter filter = BookFilterMapper.year(year);
        List<BookDTO> books = this.bookService.getAll(filter);
        List<BookControllerDTO.Response> response = BookMapper.toResponse(books);
        return ResponseEntity.ok().body(new BookControllerDTO.ListResponse(response));
    }

    @GetMapping("/year-range")
    @PublicEndpoint
    @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = BookControllerDTO.ListResponse.class)))
    public ResponseEntity<BookControllerDTO.ListResponse> yearRange(
            @ModelAttribute BookQueryParamsDTO.PubYears params
    ) {
        BookFilter filter = BookFilterMapper.betweenYears(params.getFrom(), params.getTo());
        List<BookDTO> books = this.bookService.getAll(filter);
        List<BookControllerDTO.Response> response = BookMapper.toResponse(books);
        return ResponseEntity.ok().body(new BookControllerDTO.ListResponse(response));
    }

    // @GetMapping("/stats")
    // @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = ControllerBookDTO.Stats.class)))
    // public ResponseEntity<ControllerBookDTO.Stats> stats(){
    //     List<Book> books = this.bookService.GetAll();
    //     return ResponseEntity.ok().body(new ControllerBookDTO.Stats(books));
    // }

    @PostMapping("/")
    @SecurityRequirement(name = BEARER_SECURITY_SCHEME_NAME)
    @Operation(summary = "create new book")
    @ApiResponse(responseCode = "201", description = "success")
    @ApiResponse(responseCode = "400", description = "validation error")
    public ResponseEntity<Void> add(
            @Valid @RequestBody BookControllerDTO.Create body
    ) {
        Long id = this.bookService.add(BookMapper.toDTO(body));
        return ResponseEntity.created(URI.create("/api/books/" + id)).build();
    }

    @GetMapping("/{id}")
    @PublicEndpoint
    @Operation(summary = "get book by id")
    @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = BookControllerDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "not found")
    public ResponseEntity<BookControllerDTO.Response> getByID(@PathVariable Long id) {
        BookDTO book = this.bookService.getByID(id);
        return ResponseEntity.ok().body(BookMapper.toResponse(book));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = BEARER_SECURITY_SCHEME_NAME)
    @Operation(summary = "put book by id")
    @ApiResponse(responseCode = "200", description = "success")
    @ApiResponse(responseCode = "400", description = "validation error")
    @ApiResponse(responseCode = "404", description = "not found")
    public ResponseEntity<Void> putByID(
            @PathVariable Long id,
            @Valid @RequestBody BookControllerDTO.Create body) {
        this.bookService.putByID(id, BookMapper.toDTO(body));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    @SecurityRequirement(name = BEARER_SECURITY_SCHEME_NAME)
    @Operation(summary = "patch book by id")
    @ApiResponse(responseCode = "200", description = "success")
    @ApiResponse(responseCode = "404", description = "not found")
    public ResponseEntity<Void> patchByID(
            @PathVariable Long id,
            @Valid @RequestBody BookControllerDTO.Patch body) {
        this.bookService.patchByID(id, BookMapper.toDTO(body));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = BEARER_SECURITY_SCHEME_NAME)
    @Operation(summary = "delete book by id")
    @ApiResponse(responseCode = "204", description = "success")
    public ResponseEntity<Void> deleteByID(@PathVariable Long id) {
        this.bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

}