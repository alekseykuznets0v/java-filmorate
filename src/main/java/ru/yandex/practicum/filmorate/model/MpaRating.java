package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class MpaRating {
    private int id;
    @NotBlank(message = "Название рейтинга не может быть пустым")
    private String name;
}
