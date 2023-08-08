package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreControllerTest {
    private final GenreController genreController;
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;

    @Test
    void shouldReturnGenre_Endpoint_GetGenre() throws Exception {
        mockMvc.perform(get("/genres/{id}", 1).accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getGenreById"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Комедия"));
    }

    @Test
    void shouldNotReturnGenre_Endpoint_GetGenre() throws Exception {
        int notExistingGenreId = 55;

        mockMvc.perform(get("/genres/{id}", notExistingGenreId).accept(MediaType.ALL))
                .andExpect(status().isNotFound())
                .andExpect(handler().methodName("getGenreById"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error")
                        .value(String.format("Жанр с id=%s не найден", notExistingGenreId)));

    }

    @Test
    void shouldReturnAllGenres_Endpoint_GetGenres() throws Exception {
        List<Genre> expectedGenres = new ArrayList<>(List.of(
                new Genre(1, "Комедия"),
                new Genre(2, "Драма"),
                new Genre(3, "Мультфильм"),
                new Genre(4, "Триллер"),
                new Genre(5, "Документальный"),
                new Genre(6, "Боевик")));

        mockMvc.perform(get("/genres").accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getAllGenres"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedGenres)));
    }
}