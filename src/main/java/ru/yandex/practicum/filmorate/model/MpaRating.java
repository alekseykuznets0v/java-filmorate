package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class MpaRating {
    @NotNull(message = "Id mpa не может быть null")
    private final int id;
    @NotBlank(message = "Название рейтинга не может быть пустым")
    private String name;
}
