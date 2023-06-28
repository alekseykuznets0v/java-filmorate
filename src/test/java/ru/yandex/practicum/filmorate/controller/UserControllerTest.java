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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class UserControllerTest {
    private User user;
    @Autowired
    private UserController userController;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        user = new User("1@yandex.ru", "bilbo", LocalDate.of(2001,1,1));
    }
    @AfterEach
    void cleanStorage(){
        userController.getUsers().clear();
    }

    @Test
    void shouldAddUser_Endpoint_PostUsers() throws Exception {
        final String jsonUser = objectMapper.writeValueAsString(user);
        user.setId(1);
        user.setName(user.getLogin());
        final String expectedJsonUser = objectMapper.writeValueAsString(user);

        this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJsonUser))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists());

        final int usersSize = userController.getAllUsers().size();
        assertEquals(1, usersSize, String.format("Ожидался размер списка 1, а получен %s", usersSize));

        final User savedUser = userController.getUsers().get(1);
        assertEquals(1, savedUser.getId(), String.format("Ожидался id=1, а получен id=%s", savedUser.getId()));
    }
}