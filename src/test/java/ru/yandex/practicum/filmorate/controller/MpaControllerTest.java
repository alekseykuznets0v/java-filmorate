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
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;

    @Test
    void shouldReturnMpa_Endpoint_GetMpa() throws Exception {
        mockMvc.perform(get("/mpa/{id}", 1).accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getMpaById"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("G"));
    }

    @Test
    void shouldNotReturnMpa_Endpoint_GetMpa() throws Exception {
        int notExistingMpaId = 55;

        mockMvc.perform(get("/mpa/{id}", notExistingMpaId).accept(MediaType.ALL))
                .andExpect(status().isNotFound())
                .andExpect(handler().methodName("getMpaById"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error")
                        .value(String.format("MPA рейтинг с id=%s не найден", notExistingMpaId)));

    }

    @Test
    void shouldReturnAllMpa_Endpoint_GetMpa() throws Exception {
        List<MpaRating> expectedMpa = new ArrayList<>(List.of(
                new MpaRating(1L, "G"),
                new MpaRating(2L, "PG"),
                new MpaRating(3L, "PG-13"),
                new MpaRating(4L, "R"),
                new MpaRating(5L, "NC-17")));

        mockMvc.perform(get("/mpa").accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getAllMpa"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedMpa)));
    }
}