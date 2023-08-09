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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.dao.like.LikeDao;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private Film secondFilm;
    private final FilmController filmController;
    private final FilmService filmService;
    private final UserService userService;
    private final LikeDao likeDao;
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
        secondFilm = Film.builder()
                .name("Lord of the Rings")
                .description("epic fantasy")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(228)
                .mpa(new MpaRating(2))
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

        final int filmsSize = filmService.getAllFilms().size();
        final Film returnedFilm = objectMapper.readValue(result.getResponse().getContentAsString(), Film.class);
        Long returnedFilmId = returnedFilm.getId();
        final Film savedFilm = filmService.getFilmById(returnedFilmId);
        Long savedFilmId = savedFilm.getId();

        assertEquals(1, filmsSize, String.format("Ожидался размер списка 1, а получен %s", filmsSize));
        assertEquals(returnedFilmId, savedFilmId, String.format("Ожидался id=%s, а получен id=%s", returnedFilmId, savedFilmId));
    }

    @Test
    void shouldThrowException_EmptyContent_Endpoint_PostFilms() throws Exception {
        MvcResult result = mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(""))
                .andExpect(status().isInternalServerError())
                .andExpect(handler().methodName("add"))
                .andReturn();

        String exception = Objects.requireNonNull(result.getResolvedException()).getClass().toString();
        final int filmsSize = filmService.getAllFilms().size();

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

        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(jsonFilm))
                .andExpect(status().isInternalServerError())
                .andExpect(handler().methodName("add"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").exists());

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

        final int filmsSize = filmService.getAllFilms().size();
        assertEquals(1, filmsSize, String.format("Ожидался размер списка 1, а получен %s", filmsSize));
    }

    @Test
    void shouldReturnListOfPopularFilms_Endpoint_GetPopular() throws Exception {
        filmService.addFilm(film);
        filmService.addFilm(secondFilm);

        MvcResult result = mockMvc.perform(get("/films/popular").accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getPopularFilms"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<Film> popularFilms = objectMapper.readValue(result.getResponse().getContentAsString(),
                objectMapper.readerForListOf(Film.class).getValueType());

        assertEquals(2, popularFilms.size(), String.format("Ожидался размер списка 2, а получен %s", popularFilms.size()));
    }

    @Test
    void shouldReturnEmptyListOfPopularFilms_Endpoint_GetPopular() throws Exception {
        MvcResult result = mockMvc.perform(get("/films/popular").accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getPopularFilms"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<Film> popularFilms = objectMapper.readValue(result.getResponse().getContentAsString(),
                objectMapper.readerForListOf(Film.class).getValueType());

        assertEquals(Collections.emptyList(), popularFilms, String.format("Ожидался пустой список, а получен %s", popularFilms));
    }

    @Test
    void shouldReturnEmptyList_Endpoint_GetFilms() throws Exception {
        MvcResult result = mockMvc.perform(get("/films").accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getAllFilms"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<Film> films = objectMapper.readValue(result.getResponse().getContentAsString(),
                objectMapper.readerForListOf(Film.class).getValueType());

        assertEquals(Collections.emptyList(), films, String.format("Ожидался пустой список, а получен %s", films));
    }

    @Test
    void shouldReturnListOfAllFilms_Endpoint_GetFilms() throws Exception {
        filmService.addFilm(film);
        filmService.addFilm(secondFilm);

        MvcResult result = mockMvc.perform(get("/films").accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getAllFilms"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<Film> films = objectMapper.readValue(result.getResponse().getContentAsString(),
                objectMapper.readerForListOf(Film.class).getValueType());

        assertEquals(2, films.size(), String.format("Ожидался размер списка 2, а получен %s", films.size()));
    }

    @Test
    void shouldReturnFilm_Endpoint_GetFilm() throws Exception {
        final String jsonFilm = objectMapper.writeValueAsString(film);

        MvcResult result = this.mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(jsonFilm)).andReturn();

        final Film returnedFilm = objectMapper.readValue(result.getResponse().getContentAsString(), Film.class);
        Long returnedFilmId = returnedFilm.getId();

        mockMvc.perform(get("/films/{id}", returnedFilmId).accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getFilmById"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(returnedFilmId));
    }

    @Test
    void shouldAddLike_EndPoint_PutLike() throws Exception {
        User user = User.builder()
                .email("45@yandex.ru")
                .login("user")
                .birthday(LocalDate.now())
                .build();
        User newUser = userService.addUser(user);
        Film newFilm = filmService.addFilm(film);
        Long filmId = newFilm.getId();
        Long userId = newUser.getId();

        mockMvc.perform(put("/films/{id}/like/{userId}", filmId, userId).accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("addLike"));

        Film filmWithLike = filmService.getFilmById(filmId);
        Set<Long> likes = filmWithLike.getLikes();
        Set<Long> expectedLikes = Set.of(userId);
        int likesNumber = filmWithLike.getLikesNumber();

        assertEquals(1, likesNumber, String.format("Ожидалось количество лайков = 1, а получено: %s", likesNumber));
        assertEquals(expectedLikes, likes, String.format("Ожидался один лайк от пользователя с id=%s", userId));
    }

    @Test
    void shouldDeleteLike_EndPoint_DeleteLike() throws Exception {
        User user = User.builder()
                .email("1@yandex.ru")
                .login("user")
                .birthday(LocalDate.now())
                .build();
        User newUser = userService.addUser(user);
        Film newFilm = filmService.addFilm(film);
        Long filmId = newFilm.getId();
        Long userId = newUser.getId();

        likeDao.addLike(filmId, userId);

        mockMvc.perform(delete("/films/{id}/like/{userId}", filmId, userId).accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("deleteLike"));

        Film filmWithoutLike = filmService.getFilmById(filmId);
        Set<Long> likes = filmWithoutLike.getLikes();
        Set<Long> expectedLikes = Collections.emptySet();
        int likesNumber = filmWithoutLike.getLikesNumber();

        assertEquals(0, likesNumber, String.format("Ожидалось количество лайков = 0, а получено: %s", likesNumber));
        assertEquals(expectedLikes, likes, String.format("Ожидался пустой список лайков, а получен %s", likes));
    }
}