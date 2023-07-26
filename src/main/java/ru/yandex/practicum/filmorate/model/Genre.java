package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class Genre {
    @NotNull(message = "Id MPA не может быть null")
    private int id;
    @NotBlank(message = "Название жанра не может быть пустым")
    private String name;
}
