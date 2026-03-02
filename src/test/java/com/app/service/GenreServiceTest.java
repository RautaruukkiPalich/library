package com.app.service;

import com.app.dto.GenreDTO;
import com.app.exception.notfound.GenreNotFoundException;
import com.app.exception.validation.GenreValidationException;
import com.app.exception.validation.ValidationException;
import com.app.model.Genre;
import com.app.repository.IGenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    @Mock
    private IGenreRepository genreRepository;

    @InjectMocks
    private GenreService genreService;

    private Genre genre1;
    private Genre genre2;
    private GenreDTO genreDTO;
    private GenreDTO invalidGenreDTO;

    @BeforeEach
    void setUp() {
        genre1 = new Genre("Science Fiction");
        genre1.setId(1L);

        genre2 = new Genre("Fantasy");
        genre2.setId(2L);

        genreDTO = GenreDTO.builder()
                .name("Science Fiction")
                .build();

        invalidGenreDTO = GenreDTO.builder()
                .name("")
                .build();

    }

    @Test
    void getAll_withoutFilters_shouldReturnAllGenres() {
        List<Genre> expectedGenres = Arrays.asList(genre1, genre2);
        when(genreRepository.getAll()).thenReturn(expectedGenres);

        List<Genre> result = genreService.getAll();

        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .containsExactly(genre1, genre2);

        verify(genreRepository, times(1)).getAll();
        verifyNoMoreInteractions(genreRepository);
    }

    @Test
    void getByID_shouldReturnGenre() {
        when(genreRepository.getByID(1L)).thenReturn(genre1);

        Genre result = genreService.getByID(1L);

        assertThat(result)
                .isNotNull()
                .satisfies(genre -> {
                    assertThat(genre.getId()).isEqualTo(1L);
                    assertThat(genre.getName()).isEqualTo("Science Fiction");
                });

        verify(genreRepository, times(1)).getByID(1L);
        verifyNoMoreInteractions(genreRepository);
    }

    @Test
    void getByID_withNullId_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> genreService.getByID(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("id must not be null");
    }

    @Test
    void getByID_whenGenreNotFound_shouldThrowGenreNotFoundException() {
        Long id = 999L;
        when(genreRepository.getByID(id)).thenThrow(new GenreNotFoundException(id));

        assertThatThrownBy(() -> genreService.getByID(id))
                .isInstanceOf(GenreNotFoundException.class)
                .hasMessageContaining("genre not found with id: " + id);

        verify(genreRepository, times(1)).getByID(id);
    }

    @Test
    void add_shouldSaveGenreAndReturnId() {
        Genre savedGenre = new Genre(genreDTO);
        savedGenre.setId(1L);

        when(genreRepository.save(any(Genre.class))).thenReturn(savedGenre);

        assertThat(genreService.add(genreDTO)).isNotNull().isEqualTo(1L);

        ArgumentCaptor<Genre> genreCaptor = ArgumentCaptor.forClass(Genre.class);
        verify(genreRepository, times(1)).save(genreCaptor.capture());

        Genre actualGenre = genreCaptor.getValue();
        assertAll("check saved genre",
                () -> assertNull(actualGenre.getId()),
                () -> assertEquals(genreDTO.name(), actualGenre.getName())
        );

        verifyNoMoreInteractions(genreRepository);
    }

    @Test
    void add_withNullDto_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> genreService.add(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("dto must not be null");
    }

    @Test
    void add_withInvalidDto_shouldThrowGenreValidationException() {
        assertThatThrownBy(() -> genreService.add(invalidGenreDTO))
                .isInstanceOf(GenreValidationException.class)
                .hasMessageContaining("validation error")
                .satisfies(exception -> {
                    ValidationException ex = (ValidationException) exception;
                    assertThat(ex.getErrorsMap()).containsKey("name");
                });

        verify(genreRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteGenre() {
        Long id = 1L;
        when(genreRepository.getByID(id)).thenReturn(genre1);

        genreService.delete(id);

        verify(genreRepository, times(1)).getByID(id);
        verify(genreRepository, times(1)).delete(genre1);
        verifyNoMoreInteractions(genreRepository);
    }

    @Test
    void delete_withNullId_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> genreService.delete(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("id must not be null");
    }

    @Test
    void delete_whenGenreNotFound_shouldThrowGenreNotFoundException() {
        Long id = 999L;
        when(genreRepository.getByID(id)).thenThrow(new GenreNotFoundException(id));

        assertThatThrownBy(() -> genreService.delete(id))
                .isInstanceOf(GenreNotFoundException.class)
                .hasMessageContaining("genre not found with id: " + id);

        verify(genreRepository, times(1)).getByID(id);
        verify(genreRepository, never()).delete(any());
    }
}