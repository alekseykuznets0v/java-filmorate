package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Entity;

import java.util.Collection;
import java.util.Map;

public interface Storage<T extends Entity> {
    Collection<T> getAllValues();

    Map<Integer, T> getStorage();

    T getById(int id);

    T add(T t);

    T update(T t);

}
