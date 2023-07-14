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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        notExistingUser = sampleUser.toBuilder().id(69L).login("sam").build();
        alreadyExistingUser = sampleUser.toBuilder().id(1L).build();
        updatedUser = sampleUser.toBuilder().id(1L).login("awesome_bilbo").build();
    }

    @AfterEach
    void cleanStorage() {
        userController.getUserService().getUserStorage().getStorage().clear();
        userController.getUserService().getUserStorage().getEmails().clear();
        userController.getUserService().getUserStorage().setIdentifier(0);
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

        final User savedUser = userController.getUserService().getUserStorage().getStorage().get(1L);
        assertEquals(1, savedUser.getId(), String.format("Ожидался id=1, а получен id=%s", savedUser.getId()));
    }

    @Test
    void shouldThrowException_EmptyContent_Endpoint_PostUsers() throws Exception {
        MvcResult result = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(""))
                .andExpect(status().isInternalServerError())
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
                .andExpect(handler().methodName("add"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Email не может быть пустым"));

        final int usersSize = userController.getAllUsers().size();
        assertEquals(0, usersSize, String.format("Ожидался размер списка 0, а получен %s", usersSize));
    }

    @Test
    void shouldNotAddUser_WrongEmail_Endpoint_PostUsers() throws Exception {
        final String jsonUser = objectMapper.writeValueAsString(wrongEmailFormatUser);

        this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser))
                .andExpect(status().isBadRequest())
                .andExpect(handler().methodName("add"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Некорректный формат email"));

        final int usersSize = userController.getAllUsers().size();
        assertEquals(0, usersSize, String.format("Ожидался размер списка 0, а получен %s", usersSize));
    }

    @Test
    void shouldNotAddUser_SpacesLogin_Endpoint_PostUsers() throws Exception {
        final String jsonUser = objectMapper.writeValueAsString(spacesLoginUser);

        this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser))
                .andExpect(status().isBadRequest())
                .andExpect(handler().methodName("add"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Логин не может содержать пробелы"));

        final int usersSize = userController.getAllUsers().size();
        assertEquals(0, usersSize, String.format("Ожидался размер списка 0, а получен %s", usersSize));
    }

    @Test
    void shouldNotAddUser_EmptyLogin_Endpoint_PostUsers() throws Exception {
        final String jsonUser = objectMapper.writeValueAsString(blankLoginUser);

        this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser))
                .andExpect(status().isBadRequest())
                .andExpect(handler().methodName("add"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Логин не может быть пустым"));

        final int usersSize = userController.getAllUsers().size();
        assertEquals(0, usersSize, String.format("Ожидался размер списка 0, а получен %s", usersSize));
    }

    @Test
    void shouldNotAddUser_WrongBirthday_Endpoint_PostUsers() throws Exception {
        final String jsonUser = objectMapper.writeValueAsString(unbornUser);

        this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser))
                .andExpect(status().isBadRequest())
                .andExpect(handler().methodName("add"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Дата рождения не может быть в будущем"));

        final int usersSize = userController.getAllUsers().size();
        assertEquals(0, usersSize, String.format("Ожидался размер списка 0, а получен %s", usersSize));
    }

    @Test
    void shouldNotAddUser_AlreadyExistingUser_Endpoint_PostUsers() throws Exception {
        final String jsonUser = objectMapper.writeValueAsString(user);
        final String jsonUser1 = objectMapper.writeValueAsString(alreadyExistingUser);

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser));

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser1))
                .andExpect(status().isBadRequest())
                .andExpect(handler().methodName("add"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Пользователь с email=1@yandex.ru уже существует"));

        final int usersSize = userController.getAllUsers().size();
        assertEquals(1, usersSize, String.format("Ожидался размер списка 1, а получен %s", usersSize));
    }

    @Test
    void shouldNotUpdateUser_NotExistingUser_Endpoint_PutUsers() throws Exception {
        final String jsonUser1 = objectMapper.writeValueAsString(notExistingUser);

        mockMvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser1))
                .andExpect(status().isNotFound())
                .andExpect(handler().methodName("update"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("В базе данных отсутствует пользователь с id=69"));

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
                .andExpect(handler().methodName("update"))
                .andExpect(content().json(jsonUser1))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.login").value("awesome_bilbo"));

        final int usersSize = userController.getAllUsers().size();
        assertEquals(1, usersSize, String.format("Ожидался размер списка 1, а получен %s", usersSize));
    }
}