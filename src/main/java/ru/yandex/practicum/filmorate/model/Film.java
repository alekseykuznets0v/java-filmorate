package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.util.validator.MaxLength;
import ru.yandex.practicum.filmorate.util.validator.MinDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.Set;


@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
public class Film extends Entity {
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
    private Set<Long> likes;

    @Builder(toBuilder = true)
    public Film(Long id, String name, String description, LocalDate releaseDate, int duration) {
        setId(id == null ? 0L : id);
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public int getLikesNumber(){
        return likes.size();
    }

    @Override
    public String toString() {
        return "Film{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", releaseDate=" + releaseDate +
                ", duration=" + duration +
                ", id=" + id +
                ", likes=" + likes.size() +
                '}';
    }
}
