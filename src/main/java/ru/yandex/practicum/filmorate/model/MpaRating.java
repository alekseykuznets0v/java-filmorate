package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class MpaRating {
    @NotNull(message = "Id не может быть null")
    private int id;
    @NotBlank(message = "Название рейтинга не может быть пустым")
    private String name;
}
