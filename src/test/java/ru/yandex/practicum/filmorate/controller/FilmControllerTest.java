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
import org.springframework.web.util.NestedServletException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        alreadyExistingFilm = film.toBuilder().id(1).build();
        notExistingFilm = film.toBuilder().id(69).build();
        updatedFilm = film.toBuilder().id(1).description("super epic saga").build();
    }

    @AfterEach
    void cleanStorage() {
        filmController.getStorage().clear();
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
                .andExpect(handler().methodName("add"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJsonFilm))
                .andExpect(jsonPath("$.id").exists());

        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(1, filmsSize, String.format("Ожидался размер списка 1, а получен %s", filmsSize));

        final Film savedFilm = filmController.getStorage().get(1);
        assertEquals(1, savedFilm.getId(), String.format("Ожидался id=1, а получен id=%s", savedFilm.getId()));
    }

    @Test
    void shouldThrowException_EmptyContent_Endpoint_PostFilms() throws Exception {
        MvcResult result = mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest())
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

        this.mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .content(jsonFilm))
                .andExpect(status().isBadRequest())
                .andReturn();

        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(0, filmsSize, String.format("Ожидался размер списка 0, а получен %s", filmsSize));
    }

    @Test
    void shouldNotAddFilm_EmptyDescription_Endpoint_PostFilms() throws Exception {
        final String jsonFilm = objectMapper.writeValueAsString(blankDescriptionFilm);

        this.mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .content(jsonFilm))
                .andExpect(status().isBadRequest())
                .andReturn();

        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(0, filmsSize, String.format("Ожидался размер списка 0, а получен %s", filmsSize));
    }

    @Test
    void shouldNotAddFilm_BigDescription_Endpoint_PostFilms() throws Exception {
        final String jsonFilm = objectMapper.writeValueAsString(bigDescriptionFilm);

        this.mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .content(jsonFilm))
                .andExpect(status().isBadRequest())
                .andReturn();

        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(0, filmsSize, String.format("Ожидался размер списка 0, а получен %s", filmsSize));
    }

    @Test
    void shouldNotAddFilm_NullDate_Endpoint_PostFilms() throws Exception {
        final String jsonFilm = objectMapper.writeValueAsString(nullDateFilm);

        this.mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .content(jsonFilm))
                .andExpect(status().isBadRequest())
                .andReturn();

        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(0, filmsSize, String.format("Ожидался размер списка 0, а получен %s", filmsSize));
    }

    @Test
    void shouldNotAddFilm_WrongDate_Endpoint_PostFilms() throws Exception {
        final String jsonFilm = objectMapper.writeValueAsString(wrongDateFilm);

        this.mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .content(jsonFilm))
                .andExpect(status().isBadRequest())
                .andReturn();

        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(0, filmsSize, String.format("Ожидался размер списка 0, а получен %s", filmsSize));
    }

    @Test
    void shouldNotAddFilm_NegativeDuration_Endpoint_PostFilms() throws Exception {
        final String jsonFilm = objectMapper.writeValueAsString(negativeDurationfilm);

        this.mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .content(jsonFilm))
                .andExpect(status().isBadRequest())
                .andReturn();

        final int filmsSize = filmController.getAllFilms().size();
        assertEquals(0, filmsSize, String.format("Ожидался размер списка 0, а получен %s", filmsSize));
    }

    @Test
    void shouldNotAddFilmAndThrowException_AlreadyExistingFilm_Endpoint_PostFilms() throws Exception {
        final String jsonFilm = objectMapper.writeValueAsString(film);
        final String jsonFilm1 = objectMapper.writeValueAsString(alreadyExistingFilm);

        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(jsonFilm));

        NestedServletException exception = assertThrows(NestedServletException.class,
                () -> mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(jsonFilm1)));

        String exceptionMessage = exception.getCause().getMessage();
        final int filmsSize = filmController.getAllFilms().size();

        assertEquals(1, filmsSize, String.format("Ожидался размер списка 1, а получен %s", filmsSize));
        assertEquals("Такой фильм уже существует, id=1", exceptionMessage);
    }

    @Test
    void shouldNotUpdateFilmAndThrowException_NotExistingFilm_Endpoint_PutFilms() throws Exception {
        final String jsonFilm1 = objectMapper.writeValueAsString(notExistingFilm);

        NestedServletException exception = assertThrows(NestedServletException.class,
                () -> mockMvc.perform(put("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(jsonFilm1)));

        String exceptionMessage = exception.getCause().getMessage();
        final int filmsSize = filmController.getAllFilms().size();

        assertEquals(0, filmsSize, String.format("Ожидался размер списка 0, а получен %s", filmsSize));
        assertEquals("В базе данных нет фильма с id=69", exceptionMessage);
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