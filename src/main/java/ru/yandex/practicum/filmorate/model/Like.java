package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class Like {
    @NotNull(message = "Id не может быть null")
    private long filmId;
    @NotNull(message = "Id не может быть null")
    private long userId;
}
