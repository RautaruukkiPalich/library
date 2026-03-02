package com.app.service;

import com.app.dto.AuthorDTO;
import com.app.exception.notfound.AuthorNotFoundException;
import com.app.exception.validation.AuthorValidationException;
import com.app.exception.validation.ValidationException;
import com.app.mapper.AuthorMapper;
import com.app.model.Author;
import com.app.repository.IAuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private IAuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    private Author author1;
    private Author author2;
    private AuthorDTO authorDTO;
    private AuthorDTO invalidAuthorDTO;

    @BeforeEach
    void setUp() {
        author1 = new Author(
                "Alexander",
                "Pushkin",
                "Sergeevich"
        );
        author1.setId(1L);

        author2 = new Author(
                "Fedor",
                "Dostoevskii",
                "Mihailovich"
        );
        author2.setId(2L);

        authorDTO = AuthorDTO.builder()
                .firstname("Fedor")
                .surname("Mihailovich")
                .lastname("Dostoevskii")
                .build();

        invalidAuthorDTO = AuthorDTO.builder().firstname("").build();
    }

    @Test
    void getAll_withoutFilters_shouldReturnAllAuthors() {
        List<Author> expectedAuthors = Arrays.asList(author1, author2);
        when(authorRepository.getAll()).thenReturn(expectedAuthors);

        List<AuthorDTO> res = authorService.getAll();

        assertThat(res)
                .isNotNull()
                .hasSize(2)
                .containsExactly(AuthorMapper.toDTO(author1), AuthorMapper.toDTO(author2));

        verify(authorRepository, times(1)).getAll();
        verifyNoMoreInteractions(authorRepository);
    }

    @Test
    void getByID_shouldReturnAuthor() {
        Author expectedAuthor = author1;
        when(authorRepository.getByID(1L)).thenReturn(expectedAuthor);

        AuthorDTO res = authorService.getByID(1L);

        assertThat(res)
                .isNotNull()
                .satisfies(author -> {
                    assertThat(author.id()).isEqualTo(author1.getId());
                    assertThat(author.firstname()).isEqualTo(author1.getFirstname());
                    assertThat(author.lastname()).isEqualTo(author1.getLastname());
                    assertThat(author.surname()).isEqualTo(author1.getSurname());
                });

        verify(authorRepository, times(1)).getByID(1L);
        verifyNoMoreInteractions(authorRepository);
    }

    @Test
    void getByID_withNullId_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> authorService.getByID(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("must not be null");
    }

    @Test
    void getByID_withInvalidId_shouldThrowAuthorValidationException() {
        Long id = -5L;
        assertThatThrownBy(() -> authorService.getByID(id))
                .isInstanceOf(AuthorValidationException.class)
                .hasMessageContaining("validation error")
                .satisfies(exception -> {
                    ValidationException ex = (ValidationException) exception;
                    assertThat(ex.getErrorsMap().containsKey("id"));
                });
    }

    @Test
    void getByID_whenAuthorNotFound_shouldThrowAuthorNotFoundException() {
        Long id = 999L;
        AuthorNotFoundException expectedException = new AuthorNotFoundException(id);
        when(authorRepository.getByID(id)).thenThrow(expectedException);

        assertThatThrownBy(() -> authorService.getByID(id))
                .isInstanceOf(AuthorNotFoundException.class)
                .hasMessageContaining("author not found with id: ")
                .isSameAs(expectedException);

        verify(authorRepository).getByID(id);
    }

    @Test
    void add_shouldSaveAuthorAndReturnId() {
        Author savedAuthor = new Author(authorDTO);
        savedAuthor.setId(1L);

        when(authorRepository.save(any(Author.class))).thenReturn(savedAuthor);

        assertThat(authorService.add(authorDTO)).isNotNull().isEqualTo(1L);

        ArgumentCaptor<Author> authorCaptor = ArgumentCaptor.forClass(Author.class);
        verify(authorRepository, times(1)).save(any(Author.class));
        verify(authorRepository).save(authorCaptor.capture());

        Author actualAuthor = authorCaptor.getValue();
        assertAll("check returning author",
                () -> assertNull(actualAuthor.getId()),
                () -> assertEquals(authorDTO.firstname(), actualAuthor.getFirstname()),
                () -> assertEquals(authorDTO.lastname(), actualAuthor.getLastname()),
                () -> assertEquals(authorDTO.surname(), actualAuthor.getSurname())
        );

        verifyNoMoreInteractions(authorRepository);
    }

    @Test
    void add_withInvalidDto_shouldThrowAuthorValidationException() {
        assertThatThrownBy(() -> authorService.add(invalidAuthorDTO))
                .isInstanceOf(AuthorValidationException.class)
                .hasMessageContaining("validation error")
                .satisfies(exception -> {
                    ValidationException ex = (ValidationException) exception;
                    assertThat(ex.getErrorsMap().containsKey("firstname"));
                });
    }

    @Test
    void add_withNullDto_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> authorService.add(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("must not be null")
                .satisfies();
    }

    @Test
    void delete_shouldDeleteAuthor() {
        Long id = 1L;
        when(authorRepository.getByID(id)).thenReturn(author1);

        authorService.delete(id);

        verify(authorRepository, times(1)).getByID(id);
        verify(authorRepository, times(1)).delete(author1);
        verifyNoMoreInteractions(authorRepository);
    }

    @Test
    void delete_whenAuthorNotFound_shouldThrowAuthorNotFoundException() {
        Long id = 999L;
        AuthorNotFoundException expectedException = new AuthorNotFoundException(id);

        when(authorRepository.getByID(id)).thenThrow(expectedException);

        assertThatThrownBy(() -> authorService.delete(id))
                .isInstanceOf(AuthorNotFoundException.class)
                .hasMessageContaining("author not found with id: ")
                .isSameAs(expectedException);

        verify(authorRepository, times(1)).getByID(id);
        verifyNoMoreInteractions(authorRepository);
    }

    @Test
    void delete_withNullId_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> authorService.delete(-5L))
                .isInstanceOf(AuthorValidationException.class)
                .hasMessageContaining("validation error")
                .satisfies(exception -> {
                    ValidationException ex = (ValidationException) exception;
                    assertThat(ex.getErrorsMap().containsKey("id"));
                    assertThat(ex.getErrorsMap().containsValue("cant be less than 1"));
                });
    }

    @Test
    void delete_serviceThrowsException_nullId() {
        assertThatThrownBy(() -> authorService.delete(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("must not be null")
                .satisfies();
    }
}
