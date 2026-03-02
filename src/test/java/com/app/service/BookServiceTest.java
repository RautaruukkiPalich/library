package com.app.service;

import com.app.dto.BookDTO;
import com.app.exception.notfound.AuthorNotFoundException;
import com.app.exception.notfound.BookNotFoundException;
import com.app.exception.notfound.GenreNotFoundException;
import com.app.exception.validation.BookValidationException;
import com.app.exception.validation.ValidationException;
import com.app.filter.BookFilter;
import com.app.model.Author;
import com.app.model.Book;
import com.app.model.Genre;
import com.app.repository.IBookRepository;
import com.app.repository.IAuthorRepository;
import com.app.repository.IGenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private IBookRepository bookRepository;

    @Mock
    private IAuthorRepository authorRepository;

    @Mock
    private IGenreRepository genreRepository;

    @InjectMocks
    private BookService bookService;

    private Book book1;
    private Book book2;
    private Author author;
    private Genre genre;
    private BookDTO bookDTO;
    private BookDTO invalidBookDTO;
    private BookDTO patchDTO;
    private BookFilter bookFilter;

    @BeforeEach
    void setUp() {
        author = new Author("Alexander", "Pushkin", "Sergeevich");
        author.setId(1L);

        genre = new Genre("Science Fiction");
        genre.setId(1L);

        book1 = new Book(
                "Matrix",
                author,
                genre,
                "978-5-127-12345-7",
                1999,
                150,
                true
        );
        book1.setId(1L);

        book2 = new Book(
                "Inception",
                author,
                genre,
                "978-5-127-12345-8",
                2010,
                200,
                true
        );
        book2.setId(2L);

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

        patchDTO = BookDTO.builder()
                .title("Updated Title")
                .build();

        bookFilter = new BookFilter();
        bookFilter.setTitle("Matrix");
    }

    @Test
    void getAll_withoutFilters_shouldReturnAllBooks() {
        List<Book> expectedBooks = Arrays.asList(book1, book2);
        when(bookRepository.getAll()).thenReturn(expectedBooks);

        List<Book> result = bookService.getAll();

        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .containsExactly(book1, book2);

        verify(bookRepository, times(1)).getAll();
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    void getAll_withFilter_shouldReturnFilteredBooks() {
        List<Book> expectedBooks = Collections.singletonList(book1);
        when(bookRepository.getAll(bookFilter)).thenReturn(expectedBooks);

        List<Book> result = bookService.getAll(bookFilter);

        assertThat(result)
                .isNotNull()
                .hasSize(1)
                .containsExactly(book1);

        verify(bookRepository, times(1)).getAll(bookFilter);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    void getAll_withNullFilter_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> bookService.getAll(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("filter must not be null");
    }

    @Test
    void getByID_shouldReturnBook() {
        when(bookRepository.getByID(1L)).thenReturn(book1);

        Book result = bookService.getByID(1L);

        assertThat(result)
                .isNotNull()
                .satisfies(book -> {
                    assertThat(book.getId()).isEqualTo(book1.getId());
                    assertThat(book.getTitle()).isEqualTo(book1.getTitle());
                });

        verify(bookRepository, times(1)).getByID(1L);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    void getByID_withNullId_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> bookService.getByID(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("id must not be null");
    }

    @Test
    void getByID_whenBookNotFound_shouldThrowBookNotFoundException() {
        Long id = 999L;
        when(bookRepository.getByID(id)).thenThrow(new BookNotFoundException(id));

        assertThatThrownBy(() -> bookService.getByID(id))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("book not found with id: " + id);

        verify(bookRepository, times(1)).getByID(id);
    }

    @Test
    void add_shouldSaveBookAndReturnId() {
        when(authorRepository.getByID(1L)).thenReturn(author);
        when(genreRepository.getByID(1L)).thenReturn(genre);

        Book savedBook = new Book(bookDTO, author, genre);
        savedBook.setId(1L);
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        Long resultId = bookService.add(bookDTO);

        assertThat(resultId).isEqualTo(1L);

        verify(authorRepository, times(1)).getByID(1L);
        verify(genreRepository, times(1)).getByID(1L);

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
        assertThatThrownBy(() -> bookService.add(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("dto must not be null");
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
                .hasMessageContaining("validation error")
                .satisfies(exception -> {
                    ValidationException ex = (ValidationException) exception;
                    assertThat(ex.getErrorsMap()).containsKey("authorId");
                });

        verify(authorRepository, never()).getByID(any());
        verify(genreRepository, never()).getByID(any());
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
                .hasMessageContaining("validation error")
                .satisfies(exception -> {
                    ValidationException ex = (ValidationException) exception;
                    assertThat(ex.getErrorsMap()).containsKey("genreId");
                });
    }

    @Test
    void add_whenAuthorNotFound_shouldThrowAuthorNotFoundException() {
        when(authorRepository.getByID(999L)).thenThrow(new AuthorNotFoundException(999L));

        assertThatThrownBy(() -> bookService.add(bookDTO))
                .isInstanceOf(AuthorNotFoundException.class)
                .hasMessageContaining("author not found with id: 999");

        verify(authorRepository, times(1)).getByID(999L);
        verify(genreRepository, never()).getByID(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void add_whenGenreNotFound_shouldThrowGenreNotFoundException() {
        when(authorRepository.getByID(1L)).thenReturn(author);
        when(genreRepository.getByID(999L)).thenThrow(new GenreNotFoundException(999L));

        assertThatThrownBy(() -> bookService.add(bookDTO))
                .isInstanceOf(GenreNotFoundException.class)
                .hasMessageContaining("genre not found with id: 999");

        verify(authorRepository, times(1)).getByID(1L);
        verify(genreRepository, times(1)).getByID(999L);
        verify(bookRepository, never()).save(any());
    }

    @Test
    void add_withInvalidDto_shouldThrowBookValidationException() {
        when(authorRepository.getByID(1L)).thenReturn(author);
        when(genreRepository.getByID(1L)).thenReturn(genre);

        assertThatThrownBy(() -> bookService.add(invalidBookDTO))
                .isInstanceOf(BookValidationException.class)
                .hasMessageContaining("validation error")
                .satisfies(exception -> {
                    ValidationException ex = (ValidationException) exception;
                    assertThat(ex.getErrorsMap()).containsKey("title");
                });

        verify(authorRepository, times(1)).getByID(1L);
        verify(genreRepository, times(1)).getByID(1L);
        verify(bookRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteBook() {
        Long id = 1L;
        when(bookRepository.getByID(id)).thenReturn(book1);

        bookService.delete(id);

        verify(bookRepository, times(1)).getByID(id);
        verify(bookRepository, times(1)).delete(book1);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    void delete_withNullId_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> bookService.delete(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("id must not be null");
    }

    @Test
    void delete_whenBookNotFound_shouldThrowBookNotFoundException() {
        Long id = 999L;
        when(bookRepository.getByID(id)).thenThrow(new BookNotFoundException(id));

        assertThatThrownBy(() -> bookService.delete(id))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("book not found with id: " + id);

        verify(bookRepository, times(1)).getByID(id);
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

        when(authorRepository.getByID(1L)).thenReturn(author);
        when(genreRepository.getByID(1L)).thenReturn(genre);
        when(bookRepository.getByID(1L)).thenReturn(book1);

        bookService.putByID(id, updateDTO);

        verify(authorRepository, times(1)).getByID(1L);
        verify(genreRepository, times(1)).getByID(1L);
        verify(bookRepository, times(1)).getByID(1L);

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
        assertThatThrownBy(() -> bookService.putByID(null, bookDTO))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("id must not be null");
    }

    @Test
    void putByID_withNullDto_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> bookService.putByID(1L, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("dto must not be null");
    }

    @Test
    void patchByID_shouldPartiallyUpdateBook() {
        Long id = 1L;
        when(bookRepository.getByID(id)).thenReturn(book1);

        bookService.patchByID(id, patchDTO);

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository, times(1)).save(bookCaptor.capture());

        Book updatedBook = bookCaptor.getValue();
        assertThat(updatedBook.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedBook.getPubYear()).isEqualTo(1999);
        assertThat(updatedBook.getIsbn()).isEqualTo("978-5-127-12345-7");

        verify(bookRepository, times(1)).getByID(id);
    }

    @Test
    void patchByID_withAuthorUpdate_shouldUpdateAuthor() {
        Long id = 1L;
        Author newAuthor = new Author("Fedor", "Dostoevskii", "Mihailovich");
        newAuthor.setId(2L);

        BookDTO patchWithAuthor = BookDTO.builder()
                .authorId(2L)
                .build();

        when(bookRepository.getByID(id)).thenReturn(book1);
        when(authorRepository.getByID(2L)).thenReturn(newAuthor);

        bookService.patchByID(id, patchWithAuthor);

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository, times(1)).save(bookCaptor.capture());

        Book updatedBook = bookCaptor.getValue();
        assertThat(updatedBook.getAuthor().getId()).isEqualTo(2L);

        verify(authorRepository, times(1)).getByID(2L);
    }

    @Test
    void patchByID_withNullId_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> bookService.patchByID(null, patchDTO))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("id must not be null");
    }

    @Test
    void patchByID_withNullDto_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> bookService.patchByID(1L, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("dto must not be null");
    }

    @Test
    void patchByID_whenBookNotFound_shouldThrowBookNotFoundException() {
        Long id = 999L;
        when(bookRepository.getByID(id)).thenThrow(new BookNotFoundException(id));

        assertThatThrownBy(() -> bookService.patchByID(id, patchDTO))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("book not found with id: " + id);

        verify(bookRepository, never()).save(any());
    }
}