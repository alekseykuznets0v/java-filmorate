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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
        final User sampleUser = User.builder()
                .email("1@yandex.ru")
                .login("bilbo")
                .name("")
                .birthday(testDate)
                .build();
        user = sampleUser;
        invalidEmailUser = sampleUser.toBuilder().email("").build();
        wrongEmailFormatUser = sampleUser.toBuilder().email(".@.@d").build();
        blankLoginUser = sampleUser.toBuilder().login("").build();
        spacesLoginUser = sampleUser.toBuilder().login("bilbo baggins").build();
        unbornUser = sampleUser.toBuilder().birthday(LocalDate.now().plusDays(1)).build();
        notExistingUser = sampleUser.toBuilder().id(69).login("sam").build();
        alreadyExistingUser = sampleUser.toBuilder().id(1).build();
        updatedUser = sampleUser.toBuilder().id(1).login("awesome_bilbo").build();
    }

    @AfterEach
    void cleanStorage() {
        userController.getStorage().clear();
        userController.setIdentifier(0);
        userController.getEmails().clear();
    }

    @Test
    void shouldAddUser_Endpoint_PostUsers() throws Exception {
        final String jsonUser = objectMapper.writeValueAsString(user);
        user.setId(1);
        user.setName(user.getLogin());
        final String expectedJsonUser = objectMapper.writeValueAsString(user);

        this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("add"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJsonUser))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists());

        final int usersSize = userController.getAllUsers().size();
        assertEquals(1, usersSize, String.format("Ожидался размер списка 1, а получен %s", usersSize));

        final User savedUser = userController.getStorage().get(1);
        assertEquals(1, savedUser.getId(), String.format("Ожидался id=1, а получен id=%s", savedUser.getId()));
    }

    @Test
    void shouldThrowException_EmptyContent_Endpoint_PostUsers() throws Exception {
        MvcResult result = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(""))
                .andExpect(status().isBadRequest())
                .andExpect(handler().methodName("add"))
                .andReturn();

        String exception = Objects.requireNonNull(result.getResolvedException()).getClass().toString();
        final int usersSize = userController.getAllUsers().size();

        assertEquals(0, usersSize, String.format("Ожидался размер списка 0, а получен %s", usersSize));
        assertEquals("class org.springframework.http.converter.HttpMessageNotReadableException", exception);
    }

    @Test
    void shouldNotAddUser_EmptyEmail_Endpoint_PostUsers() throws Exception {
        final String jsonUser = objectMapper.writeValueAsString(invalidEmailUser);

        this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser))
                .andExpect(status().isBadRequest())
                .andReturn();

        final int usersSize = userController.getAllUsers().size();
        assertEquals(0, usersSize, String.format("Ожидался размер списка 0, а получен %s", usersSize));
    }

    @Test
    void shouldNotAddUser_WrongEmail_Endpoint_PostUsers() throws Exception {
        final String jsonUser = objectMapper.writeValueAsString(wrongEmailFormatUser);

        this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser))
                .andExpect(status().isBadRequest())
                .andReturn();

        final int usersSize = userController.getAllUsers().size();
        assertEquals(0, usersSize, String.format("Ожидался размер списка 0, а получен %s", usersSize));
    }

    @Test
    void shouldNotAddUser_SpacesLogin_Endpoint_PostUsers() throws Exception {
        final String jsonUser = objectMapper.writeValueAsString(spacesLoginUser);

        this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser))
                .andExpect(status().isBadRequest())
                .andReturn();

        final int usersSize = userController.getAllUsers().size();
        assertEquals(0, usersSize, String.format("Ожидался размер списка 0, а получен %s", usersSize));
    }

    @Test
    void shouldNotAddUser_EmptyLogin_Endpoint_PostUsers() throws Exception {
        final String jsonUser = objectMapper.writeValueAsString(blankLoginUser);

        this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser))
                .andExpect(status().isBadRequest())
                .andReturn();

        final int usersSize = userController.getAllUsers().size();
        assertEquals(0, usersSize, String.format("Ожидался размер списка 0, а получен %s", usersSize));
    }

    @Test
    void shouldNotAddUser_WrongBirthday_Endpoint_PostUsers() throws Exception {
        final String jsonUser = objectMapper.writeValueAsString(unbornUser);

        this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser))
                .andExpect(status().isBadRequest())
                .andReturn();

        final int usersSize = userController.getAllUsers().size();
        assertEquals(0, usersSize, String.format("Ожидался размер списка 0, а получен %s", usersSize));
    }

    @Test
    void shouldNotAddUserAndThrowException_AlreadyExistingUser_Endpoint_PostUsers() throws Exception {
        final String jsonUser = objectMapper.writeValueAsString(user);
        final String jsonUser1 = objectMapper.writeValueAsString(alreadyExistingUser);

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser));

        NestedServletException exception = assertThrows(NestedServletException.class,
                () -> mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser1)));

        String exceptionMessage = exception.getCause().getMessage();
        final int usersSize = userController.getAllUsers().size();

        assertEquals(1, usersSize, String.format("Ожидался размер списка 1, а получен %s", usersSize));
        assertEquals("Пользователь с email=1@yandex.ru уже существует", exceptionMessage);
    }

    @Test
    void shouldNotUpdateUserAndThrowException_NotExistingUser_Endpoint_PutUsers() throws Exception {
        final String jsonUser1 = objectMapper.writeValueAsString(notExistingUser);

        NestedServletException exception = assertThrows(NestedServletException.class,
                () -> mockMvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser1)));

        String exceptionMessage = exception.getCause().getMessage();
        final int usersSize = userController.getAllUsers().size();

        assertEquals(0, usersSize, String.format("Ожидался размер списка 0, а получен %s", usersSize));
        assertEquals("В базе данных нет пользователя с id=69", exceptionMessage);
    }

    @Test
    void shouldUpdateUser_Endpoint_PutUsers() throws Exception {
        final String jsonUser = objectMapper.writeValueAsString(user);
        final String jsonUser1 = objectMapper.writeValueAsString(updatedUser);

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser));

        mockMvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(handler().methodName("update"))
                .andExpect(content().json(jsonUser1))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.login").value("awesome_bilbo"));

        final int usersSize = userController.getAllUsers().size();
        assertEquals(1, usersSize, String.format("Ожидался размер списка 1, а получен %s", usersSize));
    }
}