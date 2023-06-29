package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.util.validator.NoSpaces;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
public class User {
    private int id;
    @NotBlank
    @Email
    private final String email;
    @NotBlank
    @NoSpaces
    private final String login;
    private String name;
    @PastOrPresent
    private final LocalDate birthday;
}
