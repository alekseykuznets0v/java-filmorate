package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import ru.yandex.practicum.filmorate.util.validator.NoSpaces;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
public class User extends Entity {
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private final String email;
    @NotBlank(message = "Логин не может быть пустым")
    @NoSpaces
    private final String login;
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private final LocalDate birthday;
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Set<Long> approvedFriends;
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Set<Long> incomingFriendRequests;
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Set<Long> sentFriendRequests;

    @Builder(toBuilder = true)
    public User(Long id, String name, String email, String login, LocalDate birthday, Set<Long> approvedFriends,
                Set<Long> incomingFriendRequests, Set<Long> sentFriendRequests) {
        setId(id == null ? 0L : id);
        setName(name == null || name.isBlank() ? login : name);
        this.email = email;
        this.login = login;
        this.birthday = birthday;
        setApprovedFriends(approvedFriends == null ? new HashSet<>() : approvedFriends);
        setIncomingFriendRequests(incomingFriendRequests == null ? new HashSet<>() : incomingFriendRequests);
        setSentFriendRequests(sentFriendRequests == null ? new HashSet<>() : sentFriendRequests);
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", login='" + login + '\'' +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                ", id=" + id +
                ", friends=" + approvedFriends.size() +
                '}';
    }
}