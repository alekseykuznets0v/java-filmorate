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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class UserControllerTest {
    private User user;
    private User invalidEmailUser;
    private User wrongEmailFormatUser;
    private User blankLoginUser;
    private User spacesLoginUser;
    private User unbornUser;
    private User notExistingUser;
    private User alreadyExistingUser;
    private User updatedUser;
    @Autowired
    private UserController userController;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalDate testDate = LocalDate.of(2001, 1, 1);
        user = new User("1@yandex.ru", "bilbo", testDate);
        invalidEmailUser = new User("", "bilbo", testDate);
        wrongEmailFormatUser = new User(".@.@d", "bilbo", testDate);
        blankLoginUser = new User("1@yandex.ru", "", testDate);
        spacesLoginUser = new User("1@yandex.ru", "bilbo baggins", testDate);
        unbornUser = new User("1@yandex.ru", "bilbo", LocalDate.now().plusDays(1));
        notExistingUser = new User("69@yandex.ru", "sam", testDate);
        notExistingUser.setId(69);
        notExistingUser.setName(notExistingUser.getLogin());
        alreadyExistingUser = new User("1@yandex.ru", "bilbo", testDate);
        alreadyExistingUser.setId(1);
        alreadyExistingUser.setName(alreadyExistingUser.getLogin());
        updatedUser = new User("1@yandex.ru", "bilbo", testDate);
        updatedUser.setId(1);
        updatedUser.setName("awesome_bilbo");
    }

    @AfterEach
    void cleanStorage() {
        userController.getUsers().clear();
        userController.setIdentifier(0);
    }

    @Test
    void shouldAddUser_Endpoint_PostUsers() throws Exception {
        final String jsonUser = objectMapper.writeValueAsString(user);
        user.setId(1);
        user.setName(user.getLogin());
        final String expectedJsonUser = objectMapper.writeValueAsString(user);

        this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("addUser"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJsonUser))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists());

        final int usersSize = userController.getAllUsers().size();
        assertEquals(1, usersSize, String.format("Ожидался размер списка 1, а получен %s", usersSize));

        final User savedUser = userController.getUsers().get(1);
        assertEquals(1, savedUser.getId(), String.format("Ожидался id=1, а получен id=%s", savedUser.getId()));
    }

    @Test
    void shouldThrowException_EmptyContent_Endpoint_PostUsers() throws Exception {
        MvcResult result = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(""))
                .andExpect(status().isBadRequest())
                .andExpect(handler().methodName("addUser"))
                .andReturn();

        String exception = Objects.requireNonNull(result.getResolvedException()).getClass().toString();
        final int usersSize = userController.getAllUsers().size();

        assertEquals(0, usersSize, String.format("Ожидался размер списка 0, а получен %s", usersSize));
        assertEquals("class org.springframework.http.converter.HttpMessageNotReadableException", exception);
    }

    @Test
    void shouldNotAddUser_EmptyEmail_Endpoint_PostUsers() throws Exception {
        final String jsonUser = objectMapper.writeValueAsString(invalidEmailUser);

        MvcResult result = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser))
                .andExpect(status().isBadRequest())
                .andReturn();

        String errorMessage = result.getResponse().getErrorMessage();
        final int usersSize = userController.getAllUsers().size();
        assertEquals(0, usersSize, String.format("Ожидался размер списка 0, а получен %s", usersSize));
        assertEquals("Invalid request content.", errorMessage);
    }

    @Test
    void shouldNotAddUser_WrongEmail_Endpoint_PostUsers() throws Exception {
        final String jsonUser = objectMapper.writeValueAsString(wrongEmailFormatUser);

        MvcResult result = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser))
                .andExpect(status().isBadRequest())
                .andReturn();

        String errorMessage = result.getResponse().getErrorMessage();
        final int usersSize = userController.getAllUsers().size();
        assertEquals(0, usersSize, String.format("Ожидался размер списка 0, а получен %s", usersSize));
        assertEquals("Invalid request content.", errorMessage);
    }

    @Test
    void shouldNotAddUser_SpacesLogin_Endpoint_PostUsers() throws Exception {
        final String jsonUser = objectMapper.writeValueAsString(spacesLoginUser);

        MvcResult result = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser))
                .andExpect(status().isBadRequest())
                .andReturn();

        String errorMessage = result.getResponse().getErrorMessage();
        final int usersSize = userController.getAllUsers().size();
        assertEquals(0, usersSize, String.format("Ожидался размер списка 0, а получен %s", usersSize));
        assertEquals("Invalid request content.", errorMessage);
    }

    @Test
    void shouldNotAddUser_EmptyLogin_Endpoint_PostUsers() throws Exception {
        final String jsonUser = objectMapper.writeValueAsString(blankLoginUser);

        MvcResult result = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser))
                .andExpect(status().isBadRequest())
                .andReturn();

        String errorMessage = result.getResponse().getErrorMessage();
        final int usersSize = userController.getAllUsers().size();
        assertEquals(0, usersSize, String.format("Ожидался размер списка 0, а получен %s", usersSize));
        assertEquals("Invalid request content.", errorMessage);
    }

    @Test
    void shouldNotAddUser_WrongBirthday_Endpoint_PostUsers() throws Exception {
        final String jsonUser = objectMapper.writeValueAsString(unbornUser);

        MvcResult result = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser))
                .andExpect(status().isBadRequest())
                .andReturn();

        String errorMessage = result.getResponse().getErrorMessage();
        final int usersSize = userController.getAllUsers().size();
        assertEquals(0, usersSize, String.format("Ожидался размер списка 0, а получен %s", usersSize));
        assertEquals("Invalid request content.", errorMessage);
    }

    @Test
    void shouldNotAddUserAndThrowException_AlreadyExistingUser_Endpoint_PostUsers() throws Exception {
        final String jsonUser = objectMapper.writeValueAsString(user);
        final String jsonUser1 = objectMapper.writeValueAsString(alreadyExistingUser);

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser));

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser1));


        final int usersSize = userController.getAllUsers().size();
        assertEquals(1, usersSize, String.format("Ожидался размер списка 1, а получен %s", usersSize));

    }

    @Test
    void shouldNotUpdateUserAndThrowException_NotExistingUser_Endpoint_PutUsers() throws Exception {
        final String jsonUser1 = objectMapper.writeValueAsString(notExistingUser);

        mockMvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser1));


        final int usersSize = userController.getAllUsers().size();
        assertEquals(0, usersSize, String.format("Ожидался размер списка 0, а получен %s", usersSize));

    }

    @Test
    void shouldUpdateUser_Endpoint_PutUsers() throws Exception {
        final String jsonUser = objectMapper.writeValueAsString(user);
        final String jsonUser1 = objectMapper.writeValueAsString(updatedUser);

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser));

        mockMvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(handler().methodName("updateUser"))
                .andExpect(content().json(jsonUser1))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("awesome_bilbo"));

        final int usersSize = userController.getAllUsers().size();
        assertEquals(1, usersSize, String.format("Ожидался размер списка 1, а получен %s", usersSize));
    }
}