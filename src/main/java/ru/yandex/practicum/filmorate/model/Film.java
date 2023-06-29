package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import ru.yandex.practicum.filmorate.util.validator.MaxLength;
import ru.yandex.practicum.filmorate.util.validator.MinDate;

import java.time.LocalDate;

@Data
public class Film {
    private int id;
    @NotBlank(message = "Название фильма не должно быть пустым")
    private final String name;
    @NotBlank(message = "Описание фильма не должно быть пустым")
    @MaxLength(message = "Описание должно быть менее 200 символов") //собственный валидатор
    private final String description;
    @NotNull(message = "Дата выхода фильма не должна быть null")
    @MinDate(message = "Дата на может быть ранее 28.12.1895") //собственный валидатор
    private final LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма не должна быть отрицательной")
    private final int duration;
}
