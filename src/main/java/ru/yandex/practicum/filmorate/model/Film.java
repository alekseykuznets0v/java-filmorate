package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.util.validator.MaxLength;
import ru.yandex.practicum.filmorate.util.validator.MinDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;


@EqualsAndHashCode(callSuper = true)
@ToString
@Getter
@Setter
public class Film extends Entity{
    @NotBlank
    @MaxLength
    private final String description;
    @NotNull
    @MinDate
    private final LocalDate releaseDate;
    @NotNull
    @Positive
    private final int duration;
    @NotBlank
    private final String name;

    @Builder(toBuilder = true)
    public Film(int id, String name, String description, LocalDate releaseDate, int duration) {
        super(id);
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
