package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.util.validator.NoSpaces;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class User extends Entity {
    @NotBlank
    @Email
    private final String email;
    @NotBlank
    @NoSpaces
    private final String login;
    private String name;
    @PastOrPresent
    private final LocalDate birthday;

    @Builder(toBuilder = true)
    public User(int id, String name, String email, String login, LocalDate birthday) {
        super(id);
        setName(name == null || name.isBlank() ? login : name);
        this.email = email;
        this.login = login;
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", login='" + login + '\'' +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                ", id=" + id +
                '}';
    }
}