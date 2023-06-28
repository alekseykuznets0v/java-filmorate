package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import ru.yandex.practicum.filmorate.util.validator.NoSpaces;

import java.time.LocalDate;

@Data
public class User {
    private int id;
    @NotBlank(message = "Email пользователя не должен быть пустым")
    @Email(message = "Некорректный формат email пользователя")
    private final String email;
    @NotBlank(message = "Логин пользователя не может быть пустым")
    @NoSpaces(message = "Логин пользователя не может содержать пробелы") //собственный валидатор
    private final String login;
    private String name;
    @PastOrPresent(message = "Дата рождения пользователя не может быть в будущем")
    private final LocalDate birthday;
}
