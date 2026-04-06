package com.app.service;

import com.app.core.exception.ValidationException;
import com.app.modules.author.exception.AuthorNotFoundException;
import com.app.modules.author.model.Author;
import com.app.modules.author.repository.AuthorRepository;
import com.app.modules.book.dto.BookDTO;
import com.app.modules.book.dto.BookFilter;
import com.app.modules.book.exception.BookNotFoundException;
import com.app.modules.book.exception.BookValidationException;
import com.app.modules.book.impl.BookServiceImpl;
import com.app.modules.book.mapper.BookMapper;
import com.app.modules.book.model.Book;
import com.app.modules.book.repository.BookRepository;
import com.app.modules.genre.exception.GenreNotFoundException;
import com.app.modules.genre.model.Genre;
import com.app.modules.genre.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book1;
    private Book book2;
    private Author author;
    private Genre genre;
    private BookDTO bookDTO;
    private BookDTO invalidBookDTO;
    private BookDTO invalidBookDTO_invalidAuthorId;
    private BookDTO invalidBookDTO_invalidGenreId;
    private BookDTO patchDTO;
    private BookFilter bookFilter;

    private final String VALIDATION_ERROR = "validation error";

    private static Book createValidBook(Book book, Long id) {
        book.setId(id);
        book.setCreatedAt(OffsetDateTime.now());
        book.setUpdatedAt(OffsetDateTime.now());
        return book;
    }

    @BeforeEach
    void setUp() {

        author = new Author("Alexander", "Pushkin", "Sergeevich");
        author.setId(1L);

        genre = new Genre("Science Fiction");
        genre.setId(1L);

        book1 = createValidBook(new Book(
                "Matrix",
                author,
                genre,
                "978-5-127-12345-7",
                1999,
                150,
                true
        ), 1L);

        book2 = createValidBook(new Book(
                "Inception",
                author,
                genre,
                "978-5-127-12345-8",
                2010,
                200,
                true
        ), 2L);

        bookDTO = BookDTO.builder()
                .title("Matrix")
                .authorId(1L)
                .genreId(1L)
                .pubYear(1999)
                .isbn("978-5-127-12345-7")
                .pageCount(150)
                .isAvailable(true)
                .build();

        invalidBookDTO = BookDTO.builder()
                .title("")
                .authorId(1L)
                .genreId(1L)
                .pubYear(1999)
                .isbn("invalid-isbn")
                .pageCount(150)
                .isAvailable(true)
                .build();

        invalidBookDTO_invalidAuthorId = BookDTO.builder()
                .title("dsadsad")
                .authorId(999L)
                .genreId(1L)
                .pubYear(1999)
                .isbn("invalid-isbn")
                .pageCount(150)
                .isAvailable(true)
                .build();

        invalidBookDTO_invalidGenreId = BookDTO.builder()
                .title("dasdsad")
                .authorId(1L)
                .genreId(999L)
                .pubYear(1999)
                .isbn("invalid-isbn")
                .pageCount(150)
                .isAvailable(true)
                .build();

        patchDTO = BookDTO.builder()
                .title("Updated Title")
                .build();

        bookFilter = new BookFilter();
        bookFilter.setTitle("Matrix");
    }

    @Test
    void getAll_withoutFilters_shouldReturnAllBooks() {
        List<Book> expectedBooks = Arrays.asList(book1, book2);
        when(bookRepository.findAll()).thenReturn(expectedBooks);

        List<BookDTO> result = bookService.getAll();

        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .containsExactly(BookMapper.toDTO(book1), BookMapper.toDTO(book2));

        verify(bookRepository, times(1)).findAll();
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    void getAll_withFilter_shouldReturnFilteredBooks() {
        List<Book> expectedBooks = Collections.singletonList(book1);
        when(bookRepository.findAll(bookFilter)).thenReturn(expectedBooks);

        List<BookDTO> result = bookService.getAll(bookFilter);

        assertThat(result)
                .isNotNull()
                .hasSize(1)
                .containsExactly(BookMapper.toDTO(book1));

        verify(bookRepository, times(1)).findAll(bookFilter);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    void getAll_withNullFilter_shouldThrowNullPointerException() {

        final String EXPECT_MESSAGE = "filter must not be null";

        assertThatThrownBy(() -> bookService.getAll(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining(EXPECT_MESSAGE);
    }

    @Test
    void getByID_shouldReturnBook() {
        when(bookRepository.getById(1L)).thenReturn(book1);

        BookDTO result = bookService.getByID(1L);

        assertThat(result)
                .isNotNull()
                .satisfies(book -> {
                    assertThat(book.id()).isEqualTo(book1.getId());
                    assertThat(book.title()).isEqualTo(book1.getTitle());
                });

        verify(bookRepository, times(1)).getById(1L);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    void getByID_withNullId_shouldThrowNullPointerException() {
        final String EXPECT_MESSAGE = "id must not be null";

        assertThatThrownBy(() -> bookService.getByID(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining(EXPECT_MESSAGE);
    }

    @Test
    void getByID_whenBookNotFound_shouldThrowBookNotFoundException() {
        Long id = 999L;
        when(bookRepository.getById(id)).thenThrow(new BookNotFoundException(id));
        final String EXPECT_MESSAGE = "book not found with id: " + id;


        assertThatThrownBy(() -> bookService.getByID(id))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining(EXPECT_MESSAGE);

        verify(bookRepository, times(1)).getById(id);
    }

    @Test
    void add_shouldSaveBookAndReturnId() {
        when(authorRepository.getById(1L)).thenReturn(author);
        when(genreRepository.getById(1L)).thenReturn(genre);

        Book savedBook = new Book(bookDTO, author, genre);
        savedBook.setId(1L);
        savedBook.setCreatedAt(OffsetDateTime.now());
        savedBook.setUpdatedAt(OffsetDateTime.now());
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        Long resultId = bookService.add(bookDTO);

        assertThat(resultId).isEqualTo(1L);

        verify(authorRepository, times(1)).getById(1L);
        verify(genreRepository, times(1)).getById(1L);

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository, times(1)).save(bookCaptor.capture());

        Book actualBook = bookCaptor.getValue();
        assertAll("check saved book",
                () -> assertNull(actualBook.getId()),
                () -> assertEquals(bookDTO.title(), actualBook.getTitle()),
                () -> assertEquals(bookDTO.authorId(), actualBook.getAuthor().getId()),
                () -> assertEquals(bookDTO.genreId(), actualBook.getGenre().getId())
        );

        verifyNoMoreInteractions(bookRepository, authorRepository, genreRepository);
    }

    @Test
    void add_withNullDto_shouldThrowNullPointerException() {
        final String EXPECT_MESSAGE = "dto must not be null";

        assertThatThrownBy(() -> bookService.add(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining(EXPECT_MESSAGE);
    }

    @Test
    void add_withMissingAuthorId_shouldThrowBookValidationException() {
        BookDTO dtoWithoutAuthor = BookDTO.builder()
                .title("Matrix")
                .genreId(1L)
                .pubYear(1999)
                .isbn("978-5-127-12345-7")
                .pageCount(150)
                .isAvailable(true)
                .build();

        assertThatThrownBy(() -> bookService.add(dtoWithoutAuthor))
                .isInstanceOf(BookValidationException.class)
                .hasMessageContaining(VALIDATION_ERROR)
                .satisfies(exception -> {
                    ValidationException ex = (ValidationException) exception;
                    assertThat(ex.getErrorsMap()).containsKey("authorId");
                });

        verify(authorRepository, never()).getById(any());
        verify(genreRepository, never()).getById(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void add_withMissingGenreId_shouldThrowBookValidationException() {
        BookDTO dtoWithoutGenre = BookDTO.builder()
                .title("Matrix")
                .authorId(1L)
                .pubYear(1999)
                .isbn("978-5-127-12345-7")
                .pageCount(150)
                .isAvailable(true)
                .build();

        assertThatThrownBy(() -> bookService.add(dtoWithoutGenre))
                .isInstanceOf(BookValidationException.class)
                .hasMessageContaining(VALIDATION_ERROR)
                .satisfies(exception -> {
                    ValidationException ex = (ValidationException) exception;
                    assertThat(ex.getErrorsMap()).containsKey("genreId");
                });
    }

    @Test
    void add_whenAuthorNotFound_shouldThrowAuthorNotFoundException() {
        final Long id = 999L;
        final String EXPECT_MESSAGE = "author not found with id: " + id;

        when(authorRepository.getById(id)).thenThrow(new AuthorNotFoundException(id));

        assertThatThrownBy(() -> bookService.add(invalidBookDTO_invalidAuthorId))
                .isInstanceOf(AuthorNotFoundException.class)
                .hasMessageContaining(EXPECT_MESSAGE);

        verify(authorRepository, times(1)).getById(id);
        verify(genreRepository, never()).getById(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void add_whenGenreNotFound_shouldThrowGenreNotFoundException() {
        final Long id = 999L;
        final String EXPECT_MESSAGE = "genre not found with id: " + id;

        when(authorRepository.getById(1L)).thenReturn(author);
        when(genreRepository.getById(id)).thenThrow(new GenreNotFoundException(id));

        assertThatThrownBy(() -> bookService.add(invalidBookDTO_invalidGenreId))
                .isInstanceOf(GenreNotFoundException.class)
                .hasMessageContaining(EXPECT_MESSAGE);

        verify(authorRepository, times(1)).getById(1L);
        verify(genreRepository, times(1)).getById(999L);
        verify(bookRepository, never()).save(any());
    }

    @Test
    void add_withInvalidDto_shouldThrowBookValidationException() {
        final Long id = 1L;

        when(authorRepository.getById(id)).thenReturn(author);
        when(genreRepository.getById(id)).thenReturn(genre);

        assertThatThrownBy(() -> bookService.add(invalidBookDTO))
                .isInstanceOf(BookValidationException.class)
                .hasMessageContaining(VALIDATION_ERROR)
                .satisfies(exception -> {
                    ValidationException ex = (ValidationException) exception;
                    assertThat(ex.getErrorsMap()).containsKey("title");
                });

        verify(authorRepository, times(1)).getById(id);
        verify(genreRepository, times(1)).getById(id);
        verify(bookRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteBook() {
        final Long id = 1L;
        when(bookRepository.getById(id)).thenReturn(book1);

        bookService.delete(id);

        verify(bookRepository, times(1)).getById(id);
        verify(bookRepository, times(1)).delete(book1);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    void delete_withNullId_shouldThrowNullPointerException() {
        final String EXPECT_MESSAGE = "id must not be null";

        assertThatThrownBy(() -> bookService.delete(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining(EXPECT_MESSAGE);
    }

    @Test
    void delete_whenBookNotFound_shouldThrowBookNotFoundException() {
        final Long id = 999L;
        final String EXPECT_MESSAGE = "book not found with id: " + id;
        when(bookRepository.getById(id)).thenThrow(new BookNotFoundException(id));

        assertThatThrownBy(() -> bookService.delete(id))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining(EXPECT_MESSAGE);

        verify(bookRepository, times(1)).getById(id);
        verify(bookRepository, never()).delete(any());
    }

    @Test
    void putByID_shouldUpdateBook() {
        Long id = 1L;
        BookDTO updateDTO = BookDTO.builder()
                .id(1L)
                .title("Updated Matrix")
                .authorId(1L)
                .genreId(1L)
                .pubYear(2000)
                .isbn("978-5-127-12345-9")
                .pageCount(200)
                .isAvailable(false)
                .build();

        when(authorRepository.getById(1L)).thenReturn(author);
        when(genreRepository.getById(1L)).thenReturn(genre);
        when(bookRepository.getById(1L)).thenReturn(book1);

        bookService.putByID(id, updateDTO);

        verify(authorRepository, times(1)).getById(1L);
        verify(genreRepository, times(1)).getById(1L);
        verify(bookRepository, times(1)).getById(1L);

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository, times(1)).save(bookCaptor.capture());

        Book updatedBook = bookCaptor.getValue();
        assertAll("check updated book",
                () -> assertEquals(updateDTO.title(), updatedBook.getTitle()),
                () -> assertEquals(updateDTO.pubYear(), updatedBook.getPubYear()),
                () -> assertEquals(updateDTO.isbn(), updatedBook.getIsbn()),
                () -> assertEquals(updateDTO.pageCount(), updatedBook.getPageCount()),
                () -> assertEquals(updateDTO.isAvailable(), updatedBook.isAvailable())
        );
    }

    @Test
    void putByID_withNullId_shouldThrowNullPointerException() {
        final String EXPECT_MESSAGE = "id must not be null";

        assertThatThrownBy(() -> bookService.putByID(null, bookDTO))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining(EXPECT_MESSAGE);
    }

    @Test
    void putByID_withNullDto_shouldThrowNullPointerException() {
        final String EXPECT_MESSAGE = "dto must not be null";

        assertThatThrownBy(() -> bookService.putByID(1L, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining(EXPECT_MESSAGE);
    }

    @Test
    void patchByID_shouldPartiallyUpdateBook() {
        Long id = 1L;
        when(bookRepository.getById(id)).thenReturn(book1);

        bookService.patchByID(id, patchDTO);

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository, times(1)).save(bookCaptor.capture());

        Book updatedBook = bookCaptor.getValue();
        assertThat(updatedBook.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedBook.getPubYear()).isEqualTo(1999);
        assertThat(updatedBook.getIsbn()).isEqualTo("978-5-127-12345-7");

        verify(bookRepository, times(1)).getById(id);
    }

    @Test
    void patchByID_withAuthorUpdate_shouldUpdateAuthor() {
        Long id = 1L;
        Author newAuthor = new Author("Fedor", "Dostoevskii", "Mihailovich");
        newAuthor.setId(2L);

        BookDTO patchWithAuthor = BookDTO.builder()
                .authorId(2L)
                .build();

        when(bookRepository.getById(id)).thenReturn(book1);
        when(authorRepository.getById(2L)).thenReturn(newAuthor);

        bookService.patchByID(id, patchWithAuthor);

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository, times(1)).save(bookCaptor.capture());

        Book updatedBook = bookCaptor.getValue();
        assertThat(updatedBook.getAuthor().getId()).isEqualTo(2L);

        verify(authorRepository, times(1)).getById(2L);
    }

    @Test
    void patchByID_withNullId_shouldThrowNullPointerException() {
        final String EXPECT_MESSAGE = "id must not be null";

        assertThatThrownBy(() -> bookService.patchByID(null, patchDTO))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining(EXPECT_MESSAGE);
    }

    @Test
    void patchByID_withNullDto_shouldThrowNullPointerException() {
        final String EXPECT_MESSAGE = "dto must not be null";

        assertThatThrownBy(() -> bookService.patchByID(1L, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining(EXPECT_MESSAGE);
    }

    @Test
    void patchByID_whenBookNotFound_shouldThrowBookNotFoundException() {
        Long id = 999L;
        final String EXPECT_MESSAGE = "book not found with id: " + id;
        when(bookRepository.getById(id)).thenThrow(new BookNotFoundException(id));

        assertThatThrownBy(() -> bookService.patchByID(id, patchDTO))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining(EXPECT_MESSAGE);

        verify(bookRepository, never()).save(any());
    }
}