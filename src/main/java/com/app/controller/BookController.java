package com.app.controller;

import java.util.List;

import com.app.dto.queryparams.BookQueryParamsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.app.filter.BookFilter;
import com.app.dto.controller.ControllerBookDTO;
import com.app.mapper.filter.BookFilterMapper;
import com.app.mapper.BookMapper;
import com.app.model.Book;
import com.app.service.IBookService;

@RestController
@RequestMapping("/api/books")
@Tag(name = "books", description = "books api methods")
public class BookController {

    private final IBookService bookService;

    public BookController(IBookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/")
    @Operation(summary = "get all books")
    @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = ControllerBookDTO.ListResponse.class)))
    public ResponseEntity<ControllerBookDTO.ListResponse> getAll() {
        List<Book> books = this.bookService.GetAll();
        List<ControllerBookDTO.Response> response = BookMapper.toResponse(books);
        return ResponseEntity.ok().body(new ControllerBookDTO.ListResponse(response));
    }

    @GetMapping("/search")
    @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = ControllerBookDTO.ListResponse.class)))
    public ResponseEntity<ControllerBookDTO.ListResponse> search(
        @ModelAttribute BookQueryParamsDTO.TitleGenre params
    ){
        BookFilter filter = BookFilterMapper.toFilter(params);
        List<Book> books = this.bookService.GetAll(filter);
        List<ControllerBookDTO.Response> response = BookMapper.toResponse(books);
        return ResponseEntity.ok().body(new ControllerBookDTO.ListResponse(response));
    }

    @GetMapping("/available")
    @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = ControllerBookDTO.ListResponse.class)))
    public ResponseEntity<ControllerBookDTO.ListResponse> available(){
        BookFilter filter = BookFilterMapper.available();
        List<Book> books = this.bookService.GetAll(filter);
        List<ControllerBookDTO.Response> response = BookMapper.toResponse(books);
        return ResponseEntity.ok().body(new ControllerBookDTO.ListResponse(response));
    }

    @GetMapping("/year/{year}")
    @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = ControllerBookDTO.ListResponse.class)))
    public ResponseEntity<ControllerBookDTO.ListResponse> year(
        @PathVariable Integer year
    ){
        BookFilter filter = BookFilterMapper.year(year);
        List<Book> books = this.bookService.GetAll(filter);
        List<ControllerBookDTO.Response> response = BookMapper.toResponse(books);
        return ResponseEntity.ok().body(new ControllerBookDTO.ListResponse(response));
    }

    @GetMapping("/year-range")
    @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = ControllerBookDTO.ListResponse.class)))
    public ResponseEntity<ControllerBookDTO.ListResponse> yearRange(
        @ModelAttribute BookQueryParamsDTO.PubYears params
    ){
        BookFilter filter = BookFilterMapper.betweenYears(params.getFrom(), params.getTo());
        List<Book> books = this.bookService.GetAll(filter);
        List<ControllerBookDTO.Response> response = BookMapper.toResponse(books);
        return ResponseEntity.ok().body(new ControllerBookDTO.ListResponse(response));
    }

    // @GetMapping("/stats")
    // @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = ControllerBookDTO.Stats.class)))
    // public ResponseEntity<ControllerBookDTO.Stats> stats(){
    //     List<Book> books = this.bookService.GetAll();
    //     return ResponseEntity.ok().body(new ControllerBookDTO.Stats(books));
    // }
    
    @PostMapping("/")
    @Operation(summary = "create new book")
    @ApiResponse(responseCode = "201", description = "success")
    @ApiResponse(responseCode = "400", description = "validation error")
    public ResponseEntity<Void> add(
        @Valid @RequestBody ControllerBookDTO.Create body
    ) {
        this.bookService.Add(BookMapper.toDTO(body));
        return ResponseEntity.created(null).build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "get book by id")
    @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = ControllerBookDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "not found")
    public ResponseEntity<ControllerBookDTO.Response> getByID(@PathVariable Long id) {
        Book book = this.bookService.GetByID(id);
        return ResponseEntity.ok().body(BookMapper.toResponse(book));
    }

    @PutMapping("/{id}")
    @Operation(summary = "put book by id")
    @ApiResponse(responseCode = "200", description = "success")
    @ApiResponse(responseCode = "400", description = "validation error")
    @ApiResponse(responseCode = "404", description = "not found")
    public ResponseEntity<Void> putByID(
            @PathVariable Long id,
            @Valid @RequestBody ControllerBookDTO.Create body) {
        this.bookService.PutByID(id, BookMapper.toDTO(body));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "patch book by id")
    @ApiResponse(responseCode = "200", description = "success")
    @ApiResponse(responseCode = "404", description = "not found")
    public ResponseEntity<Void> patchByID(
            @PathVariable Long id,
            @Valid @RequestBody ControllerBookDTO.Patch body) {
        this.bookService.PatchByID(id, BookMapper.toDTO(body));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete book by id")
    @ApiResponse(responseCode = "204", description = "success")
    public ResponseEntity<Void> deleteByID(@PathVariable Long id) {
        this.bookService.Delete(id);
        return ResponseEntity.noContent().build();
    }

}