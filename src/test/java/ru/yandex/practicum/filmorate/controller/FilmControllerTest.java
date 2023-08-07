package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmControllerTest {
    private Film film;
    private Film blankNameFilm;
    private Film blankDescriptionFilm;
    private Film bigDescriptionFilm;
    private Film nullDateFilm;
    private Film wrongDateFilm;
    private Film negativeDurationfilm;
    private Film alreadyExistingFilm;
    private Film notExistingFilm;
    private Film updatedFilm;
    private Film wrongMpaFilm;
    private final FilmController filmController;
    private final FilmService filmService;
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        StringBuilder bigDescription = new StringBuilder();
        while (bigDescription.length() <= 201) {
            bigDescription.append("a");
        }
        film = Film.builder()
                .name("Hobbit")
                .description("epic saga")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(200)
                .mpa(new MpaRating(1))
                .build();
        blankNameFilm = film.toBuilder().name("").build();
        blankDescriptionFilm = film.toBuilder().description("").build();
        bigDescriptionFilm = film.toBuilder().description(bigDescription.toString()).build();
        nullDateFilm = film.toBuilder().releaseDate(null).build();
        wrongDateFilm = film.toBuilder().releaseDate(LocalDate.of(1600, 1, 1)).build();
        negativeDurationfilm = film.toBuilder().duration(-200).build();
        alreadyExistingFilm = film.toBuilder().id(1L).build();
        notExistingFilm = film.toBuilder().id(69L).build();
        updatedFilm = film.toBuilder().id(1L).description("super epic saga").build();
        wrongMpaFilm = film.toBuilder().mpa(new MpaRating(12)).build();
    }

    @AfterEach
    void cleanStorage() {
        filmService.deleteAllFilms();
    }

    @Test
    void shouldAddFilm_Endpoint_PostFilms() throws Exception {
        final String jsonFilm = objectMapper.writeValueAsString(film);

        MvcResult result = this.mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .content(jsonFilm))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("add"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists()).andReturn();

        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(1, filmsSize, String.format("Ожидался размер списка 1, а получен %s", filmsSize));

        final Film returnedFilm = objectMapper.readValue(result.getResponse().getContentAsString(), Film.class);

        final Film savedFilm = filmController.getFilmById(returnedFilm.getId());
        assertEquals(returnedFilm.getId(), savedFilm.getId(), String.format("Ожидался id=1, а получен id=%s", savedFilm.getId()));
    }

    @Test
    void shouldThrowException_EmptyContent_Endpoint_PostFilms() throws Exception {
        MvcResult result = mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(""))
                .andExpect(status().isInternalServerError())
                .andExpect(handler().methodName("add"))
                .andReturn();

        String exception = Objects.requireNonNull(result.getResolvedException()).getClass().toString();
        final int filmsSize = filmController.getAllFilms().size();

        assertEquals(0, filmsSize, String.format("Ожидался размер списка 0, а получен %s", filmsSize));
        assertEquals("class org.springframework.http.converter.HttpMessageNotReadableException", exception);
    }

    @Test
    void shouldNotAddFilm_EmptyName_Endpoint_PostFilms() throws Exception {
        final String jsonFilm = objectMapper.writeValueAsString(blankNameFilm);

        this.mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(jsonFilm))
                .andExpect(status().isBadRequest())
                .andExpect(handler().methodName("add"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Название фильма не может быть пустым"));

        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(0, filmsSize, String.format("Ожидался размер списка 0, а получен %s", filmsSize));
    }

    @Test
    void shouldNotAddFilm_EmptyDescription_Endpoint_PostFilms() throws Exception {
        final String jsonFilm = objectMapper.writeValueAsString(blankDescriptionFilm);

        this.mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(jsonFilm))
                .andExpect(status().isBadRequest())
                .andExpect(handler().methodName("add"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Описание фильма не может быть пустым"));

        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(0, filmsSize, String.format("Ожидался размер списка 0, а получен %s", filmsSize));
    }

    @Test
    void shouldNotAddFilm_WrongMpa_Endpoint_PostFilms() throws Exception {
        final String jsonFilm = objectMapper.writeValueAsString(wrongMpaFilm);

        MvcResult result = this.mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(jsonFilm))
                .andExpect(status().isInternalServerError())
                .andExpect(handler().methodName("add"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").exists())
                .andReturn();

        final int filmsSize = filmController.getAllFilms().size();
        assertTrue(Objects.requireNonNull(result.getResolvedException()).toString().contains("Нарушение ссылочной целостности"));
        assertEquals(0, filmsSize, String.format("Ожидался размер списка 0, а получен %s", filmsSize));
    }

    @Test
    void shouldNotAddFilm_BigDescription_Endpoint_PostFilms() throws Exception {
        final String jsonFilm = objectMapper.writeValueAsString(bigDescriptionFilm);

        this.mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .content(jsonFilm))
                .andExpect(status().isBadRequest())
                .andExpect(handler().methodName("add"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error")
                        .value("Превышено ограничение максимальной длины текста в 200 символов"));

        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(0, filmsSize, String.format("Ожидался размер списка 0, а получен %s", filmsSize));
    }

    @Test
    void shouldNotAddFilm_NullDate_Endpoint_PostFilms() throws Exception {
        final String jsonFilm = objectMapper.writeValueAsString(nullDateFilm);

        this.mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .content(jsonFilm))
                .andExpect(status().isBadRequest())
                .andExpect(handler().methodName("add"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Дата выхода фильма не может быть null"));

        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(0, filmsSize, String.format("Ожидался размер списка 0, а получен %s", filmsSize));
    }

    @Test
    void shouldNotAddFilm_WrongDate_Endpoint_PostFilms() throws Exception {
        final String jsonFilm = objectMapper.writeValueAsString(wrongDateFilm);

        this.mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .content(jsonFilm))
                .andExpect(status().isBadRequest())
                .andExpect(handler().methodName("add"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Дата не может быть ранее 28.12.1895"));

        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(0, filmsSize, String.format("Ожидался размер списка 0, а получен %s", filmsSize));
    }

    @Test
    void shouldNotAddFilm_NegativeDuration_Endpoint_PostFilms() throws Exception {
        final String jsonFilm = objectMapper.writeValueAsString(negativeDurationfilm);

        this.mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .content(jsonFilm))
                .andExpect(status().isBadRequest())
                .andExpect(handler().methodName("add"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error")
                        .value("Продолжительность фильма не может быть отрицательной"));

        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(0, filmsSize, String.format("Ожидался размер списка 0, а получен %s", filmsSize));
    }

    @Test
    void shouldNotAddFilm_AlreadyExistingFilm_Endpoint_PostFilms() throws Exception {
        final String jsonFilm = objectMapper.writeValueAsString(film);
        final String jsonFilm1 = objectMapper.writeValueAsString(alreadyExistingFilm);

        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(jsonFilm));

        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(jsonFilm1))
                .andExpect(status().isBadRequest())
                .andExpect(handler().methodName("add"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Такой фильм уже существует в БД"));

        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(1, filmsSize, String.format("Ожидался размер списка 1, а получен %s", filmsSize));
    }

    @Test
    void shouldNotUpdateFilm_NotExistingFilm_Endpoint_PutFilms() throws Exception {
        final String jsonFilm1 = objectMapper.writeValueAsString(notExistingFilm);

        mockMvc.perform(put("/films").contentType(MediaType.APPLICATION_JSON).content(jsonFilm1))
                .andExpect(status().isNotFound())
                .andExpect(handler().methodName("update"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Фильм с id=69 не найден"));

        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(0, filmsSize, String.format("Ожидался размер списка 0, а получен %s", filmsSize));
    }

    @Test
    void shouldUpdateFilm_Endpoint_PutFilms() throws Exception {
        final String jsonFilm = objectMapper.writeValueAsString(film);
        MvcResult result = mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonFilm))
                .andReturn();
        final Film returnedFilm = objectMapper.readValue(result.getResponse().getContentAsString(), Film.class);
        final String jsonFilm1 = objectMapper.writeValueAsString(updatedFilm.toBuilder().id(returnedFilm.getId()).build());

        mockMvc.perform(put("/films").contentType(MediaType.APPLICATION_JSON).content(jsonFilm1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(handler().methodName("update"))
                .andExpect(jsonPath("$.id").value(returnedFilm.getId()))
                .andExpect(jsonPath("$.description").value("super epic saga"));

        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(1, filmsSize, String.format("Ожидался размер списка 1, а получен %s", filmsSize));
    }
}