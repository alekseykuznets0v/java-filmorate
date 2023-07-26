package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
public class Genre extends Entity{
    @NotBlank(message = "Название жанра не может быть пустым")
    private final String name;

    @Builder(toBuilder = true)
    public Genre(Long id, String name) {
        setId(id == null ? 0L : id);
        this.name = name;
    }
}
