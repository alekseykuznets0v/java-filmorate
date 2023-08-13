package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.util.validator.MaxLength;
import ru.yandex.practicum.filmorate.util.validator.MinDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
public class Film extends Entity {
    @NotBlank(message = "Название фильма не может быть пустым")
    private final String name;
    @NotBlank(message = "Описание фильма не может быть пустым")
    @MaxLength
    private final String description;
    @NotNull(message = "Дата выхода фильма не может быть null")
    @MinDate
    private final LocalDate releaseDate;
    @NotNull(message = "Продолжительность фильма не может быть null")
    @Positive(message = "Продолжительность фильма не может быть отрицательной")
    private final int duration;
    @EqualsAndHashCode.Exclude
    private int likesNumber;
    @EqualsAndHashCode.Exclude
    private Set<Genre> genres;
    @NotNull(message = "Рейтинг MPA не может быть null")
    private MpaRating mpa;

    private List<String> getGenresNames(Set<Genre> genres) {
        if (!genres.isEmpty()) {
            return genres.stream().map(Genre::getName).collect(Collectors.toList());
        } else return Collections.emptyList();
    }

    @Builder(toBuilder = true)
    public Film(Long id, String name, String description, LocalDate releaseDate, int duration, int likesNumber,
                Set<Genre> genres, MpaRating mpa) {
        setId(id == null ? 0L : id);
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likesNumber = likesNumber;
        setGenres(genres == null ? new HashSet<>() : genres);
        this.mpa = mpa;
    }

    @Override
    public String toString() {
        return "Film{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", releaseDate=" + releaseDate +
                ", duration=" + duration +
                ", id=" + id +
                ", likesNumber=" + likesNumber +
                ", mpa=" + mpa +
                ", genres=" + getGenresNames(this.genres) +
                '}';
    }
}
