package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Genre {
    @NotNull(message = "Id жанра не может быть null")
    private final int id;
    @NotBlank(message = "Название жанра не может быть пустым")
    private String name;
}
