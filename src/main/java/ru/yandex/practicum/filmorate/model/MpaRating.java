package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class MpaRating extends Entity {
    @NotBlank(message = "Название рейтинга не может быть пустым")
    private String name;

    public MpaRating(Long id, String name) {
        setId(id == null ? 0L : id);
        this.name = name;
    }

    public MpaRating(Long id) {
        setId(id == null ? 0L : id);
        this.name = null;
    }
}
