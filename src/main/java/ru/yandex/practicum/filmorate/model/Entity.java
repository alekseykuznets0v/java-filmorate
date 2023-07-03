package ru.yandex.practicum.filmorate.model;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public abstract class Entity {
    protected int id;
}
