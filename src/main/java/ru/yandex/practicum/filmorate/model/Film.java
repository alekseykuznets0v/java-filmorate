package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.util.validator.MaxLength;
import ru.yandex.practicum.filmorate.util.validator.MinDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Data
public class Film {
    private int id;
    @NotBlank
    private final String name;
    @NotBlank
    @MaxLength
    private final String description;
    @NotNull
    @MinDate
    private final LocalDate releaseDate;
    @NotNull
    @Positive
    private final int duration;
}
