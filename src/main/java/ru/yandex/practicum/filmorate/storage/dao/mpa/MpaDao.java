package ru.yandex.practicum.filmorate.storage.dao.mpa;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;

public interface MpaDao {
    MpaRating getMpaById (int id);
    Collection<MpaRating> getAllMpa();
}
