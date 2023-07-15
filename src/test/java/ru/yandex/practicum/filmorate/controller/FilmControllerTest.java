package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
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
    @Autowired
    private FilmController filmController;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

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
    }

    @AfterEach
    void cleanStorage() {
        filmController.getFilmServiceImpl().deleteAllFilms();
        filmController.getFilmServiceImpl().resetIdentifier();
    }

    @Test
    void shouldAddFilm_Endpoint_PostFilms() throws Exception {
        final String jsonFilm = objectMapper.writeValueAsString(film);
        film.setId(1);
        final String expectedJsonFilm = objectMapper.writeValueAsString(film);

        this.mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .content(jsonFilm))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("add"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJsonFilm))
                .andExpect(jsonPath("$.id").exists());

        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(1, filmsSize, String.format("Ожидался размер списка 1, а получен %s", filmsSize));

        final Film savedFilm = filmController.getFilmServiceImpl().getFilmById(1L);
        assertEquals(1, savedFilm.getId(), String.format("Ожидался id=1, а получен id=%s", savedFilm.getId()));
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
                .andExpect(jsonPath("$.error").value("Такой фильм уже существует, id=1"));

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
                .andExpect(jsonPath("$.error").value("В базе данных отсутствует фильм с id=69"));

        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(0, filmsSize, String.format("Ожидался размер списка 0, а получен %s", filmsSize));
    }

    @Test
    void shouldUpdateFilm_Endpoint_PutFilms() throws Exception {
        final String jsonFilm = objectMapper.writeValueAsString(film);
        final String jsonFilm1 = objectMapper.writeValueAsString(updatedFilm);

        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(jsonFilm));

        mockMvc.perform(put("/films").contentType(MediaType.APPLICATION_JSON).content(jsonFilm1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(handler().methodName("update"))
                .andExpect(content().json(jsonFilm1))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("super epic saga"));

        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(1, filmsSize, String.format("Ожидался размер списка 1, а получен %s", filmsSize));
    }
}