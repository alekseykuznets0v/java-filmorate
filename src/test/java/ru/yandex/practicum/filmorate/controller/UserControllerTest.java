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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
    private User user;
    private User secondUser;
    private User invalidEmailUser;
    private User wrongEmailFormatUser;
    private User blankLoginUser;
    private User spacesLoginUser;
    private User unbornUser;
    private User notExistingUser;
    private User alreadyExistingUser;
    private User updatedUser;
    private final UserController userController;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;

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
        secondUser = sampleUser.toBuilder().name("gendalf").login("white_mage").email("2@yandex.ru").birthday(testDate).build();
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
        userService.deleteAllUsers();
    }

    @Test
    void shouldAddUser_Endpoint_PostUsers() throws Exception {
        final String jsonUser = objectMapper.writeValueAsString(user);

        MvcResult result = this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("add"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists()).andReturn();

        final int usersSize = userService.getAllUsers().size();
        final User returnedUser = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
        Long returnedUserId = returnedUser.getId();
        final User savedUser = userController.getUserById(returnedUserId);
        Long savedUserId = savedUser.getId();

        assertEquals(1, usersSize, String.format("Ожидался размер списка 1, а получен %s", usersSize));
        assertEquals(returnedUserId, savedUserId, String.format("Ожидался id=%s, а получен id=%s", returnedUser, savedUserId));
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
                .andExpect(jsonPath("$.error").value("Пользователь с id=69 не найден"));

        final int usersSize = userController.getAllUsers().size();
        assertEquals(0, usersSize, String.format("Ожидался размер списка 0, а получен %s", usersSize));
    }

    @Test
    void shouldUpdateUser_Endpoint_PutUsers() throws Exception {
        final String jsonUser = objectMapper.writeValueAsString(user);
        MvcResult result = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser))
                .andReturn();

        final User returnedUser = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
        final String jsonUser1 = objectMapper.writeValueAsString(updatedUser.toBuilder().id(returnedUser.getId()).build());

        mockMvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(handler().methodName("update"))
                .andExpect(content().json(jsonUser1))
                .andExpect(jsonPath("$.id").value(returnedUser.getId()))
                .andExpect(jsonPath("$.login").value("awesome_bilbo"));

        final int usersSize = userService.getAllUsers().size();
        assertEquals(1, usersSize, String.format("Ожидался размер списка 1, а получен %s", usersSize));
    }

    @Test
    void shouldReturnEmptyList_Endpoint_GetUsers() throws Exception {
        MvcResult result = mockMvc.perform(get("/users").accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getAllUsers"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<User> users = objectMapper.readValue(result.getResponse().getContentAsString(),
                objectMapper.readerForListOf(User.class).getValueType());

        assertEquals(Collections.emptyList(), users, String.format("Ожидался пустой список, а получен %s", users));
    }

    @Test
    void shouldReturnListOfAllUsers_Endpoint_GetUsers() throws Exception {
        userService.addUser(user);
        userService.addUser(secondUser);

        MvcResult result = mockMvc.perform(get("/users").accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getAllUsers"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<Film> films = objectMapper.readValue(result.getResponse().getContentAsString(),
                objectMapper.readerForListOf(User.class).getValueType());

        assertEquals(2, films.size(), String.format("Ожидался размер списка 2, а получен %s", films.size()));
    }

    @Test
    void shouldReturnUser_Endpoint_GetUser() throws Exception {
        final String jsonUser = objectMapper.writeValueAsString(user);

        MvcResult result = this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser)).andReturn();

        final User returnedUser = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
        Long returnedUserId = returnedUser.getId();

        mockMvc.perform(get("/users/{id}", returnedUserId).accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getUserById"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(returnedUserId));
    }
}