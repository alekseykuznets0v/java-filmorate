package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    void setUp(){
        StringBuilder bigDescription = new StringBuilder();
        while(bigDescription.length() <= 201){
            bigDescription.append("a");
        }
        film = new Film("Hobbit", "epic saga", LocalDate.of(2001, 1, 1), 200);
        blankNameFilm = new Film("", "epic saga", LocalDate.of(2001, 1, 1), 200);
        blankDescriptionFilm = new Film("Hobbit", "", LocalDate.of(2001, 1, 1), 200);
        bigDescriptionFilm = new Film("Hobbit", bigDescription.toString(), LocalDate.of(2001, 1, 1), 200);
        nullDateFilm = new Film("Hobbit", "epic saga", null, 200);
        wrongDateFilm = new Film("Hobbit", "epic saga", LocalDate.of(1600, 1, 1), 200);
        negativeDurationfilm = new Film("Hobbit", "epic saga", LocalDate.of(2001, 1, 1), -200);
        alreadyExistingFilm = new Film("Hobbit", "epic saga", LocalDate.of(2001, 1, 1), 200);
        alreadyExistingFilm.setId(1);
        notExistingFilm = new Film("Hobbit", "epic saga", LocalDate.of(2001, 1, 1), 200);
        notExistingFilm.setId(69);
        updatedFilm = new Film(film.getName(), "super epic saga", film.getReleaseDate(), film.getDuration());
        updatedFilm.setId(1);
    }

    @AfterEach
    void cleanStorage(){
        filmController.getFilms().clear();
        filmController.setIdentifier(0);
    }

    @Test
    void shouldAddFilm_Endpoint_PostFilms() throws Exception {
        final String jsonFilm = objectMapper.writeValueAsString(film);
        film.setId(1);
        final String expectedJsonFilm = objectMapper.writeValueAsString(film);

        this.mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .content(jsonFilm))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("addFilm"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJsonFilm))
                .andExpect(jsonPath("$.id").exists());

        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(1, filmsSize, String.format("Ожидался размер списка 1, а получен %s", filmsSize));

        final Film savedFilm = filmController.getFilms().get(1);
        assertEquals(1, savedFilm.getId(), String.format("Ожидался id=1, а получен id=%s", savedFilm.getId()));
    }

    @Test
    void shouldThrowException_EmptyContent_Endpoint_PostFilms() throws Exception {
        MvcResult result = mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(handler().methodName("addFilm"))
                .andReturn();

        String exception = Objects.requireNonNull(result.getResolvedException()).getClass().toString();
        final int filmsSize = filmController.getAllFilms().size();

        assertEquals(0, filmsSize, String.format("Ожидался размер списка 0, а получен %s", filmsSize));
        assertEquals("class org.springframework.http.converter.HttpMessageNotReadableException", exception);
    }

    @Test
    void shouldNotAddFilm_EmptyName_Endpoint_PostFilms() throws Exception {
        final String jsonFilm = objectMapper.writeValueAsString(blankNameFilm);

        MvcResult result = mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .content(jsonFilm))
                .andExpect(status().isBadRequest())
                .andReturn();

        String errorMessage = result.getResponse().getErrorMessage();
        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(0, filmsSize, String.format("Ожидался размер списка 0, а получен %s", filmsSize));
        assertEquals("Invalid request content.", errorMessage);
    }

    @Test
    void shouldNotAddFilm_EmptyDescription_Endpoint_PostFilms() throws Exception {
        final String jsonFilm = objectMapper.writeValueAsString(blankDescriptionFilm);

        MvcResult result = mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .content(jsonFilm))
                .andExpect(status().isBadRequest())
                .andReturn();

        String errorMessage = result.getResponse().getErrorMessage();
        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(0, filmsSize, String.format("Ожидался размер списка 0, а получен %s", filmsSize));
        assertEquals("Invalid request content.", errorMessage);
    }

    @Test
    void shouldNotAddFilm_BigDescription_Endpoint_PostFilms() throws Exception {
        final String jsonFilm = objectMapper.writeValueAsString(bigDescriptionFilm);

        MvcResult result = mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .content(jsonFilm))
                .andExpect(status().isBadRequest())
                .andReturn();

        String errorMessage = result.getResponse().getErrorMessage();
        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(0, filmsSize, String.format("Ожидался размер списка 0, а получен %s", filmsSize));
        assertEquals("Invalid request content.", errorMessage);
    }

    @Test
    void shouldNotAddFilm_NullDate_Endpoint_PostFilms() throws Exception {
        final String jsonFilm = objectMapper.writeValueAsString(nullDateFilm);

        MvcResult result = mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .content(jsonFilm))
                .andExpect(status().isBadRequest())
                .andReturn();

        String errorMessage = result.getResponse().getErrorMessage();
        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(0, filmsSize, String.format("Ожидался размер списка 0, а получен %s", filmsSize));
        assertEquals("Invalid request content.", errorMessage);
    }

    @Test
    void shouldNotAddFilm_WrongDate_Endpoint_PostFilms() throws Exception {
        final String jsonFilm = objectMapper.writeValueAsString(wrongDateFilm);

        MvcResult result = mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .content(jsonFilm))
                .andExpect(status().isBadRequest())
                .andReturn();

        String errorMessage = result.getResponse().getErrorMessage();
        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(0, filmsSize, String.format("Ожидался размер списка 0, а получен %s", filmsSize));
        assertEquals("Invalid request content.", errorMessage);
    }

    @Test
    void shouldNotAddFilm_NegativeDuration_Endpoint_PostFilms() throws Exception {
        final String jsonFilm = objectMapper.writeValueAsString(negativeDurationfilm);

        MvcResult result = mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .content(jsonFilm))
                .andExpect(status().isBadRequest())
                .andReturn();

        String errorMessage = result.getResponse().getErrorMessage();
        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(0, filmsSize, String.format("Ожидался размер списка 0, а получен %s", filmsSize));
        assertEquals("Invalid request content.", errorMessage);
    }

    @Test
    void shouldNotAddFilmAndThrowException_AlreadyExistingFilm_Endpoint_PostFilms() throws Exception {
        final String jsonFilm = objectMapper.writeValueAsString(film);
        final String jsonFilm1 = objectMapper.writeValueAsString(alreadyExistingFilm);

        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(jsonFilm));

        ServletException exception = assertThrows(ServletException.class,
                () -> mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(jsonFilm1)));

        String exceptionMessage = exception.getMessage();
        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(1, filmsSize, String.format("Ожидался размер списка 1, а получен %s", filmsSize));
        assertEquals("Request processing failed: ru.yandex.practicum.filmorate.exception.ValidationException: " +
                "Фильм с id=1 уже существует", exceptionMessage);
    }

    @Test
    void shouldNotUpdateFilmAndThrowException_NotExistingFilm_Endpoint_PutFilms() throws Exception {
        final String jsonFilm1 = objectMapper.writeValueAsString(notExistingFilm);

        ServletException exception = assertThrows(ServletException.class,
                () -> mockMvc.perform(put("/films").contentType(MediaType.APPLICATION_JSON).content(jsonFilm1)));

        String exceptionMessage = exception.getMessage();
        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(0, filmsSize, String.format("Ожидался размер списка 0, а получен %s", filmsSize));
        assertEquals("Request processing failed: ru.yandex.practicum.filmorate.exception.ValidationException: " +
                "В библиотеке нет фильма с id=69", exceptionMessage);
    }

    @Test
    void shouldUpdateFilm_Endpoint_PutFilms() throws Exception {
        final String jsonFilm = objectMapper.writeValueAsString(film);
        final String jsonFilm1 = objectMapper.writeValueAsString(updatedFilm);

        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(jsonFilm));

        mockMvc.perform(put("/films").contentType(MediaType.APPLICATION_JSON).content(jsonFilm1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(handler().methodName("updateFilm"))
                .andExpect(content().json(jsonFilm1))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("super epic saga"));

        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(1, filmsSize, String.format("Ожидался размер списка 1, а получен %s", filmsSize));
    }
}