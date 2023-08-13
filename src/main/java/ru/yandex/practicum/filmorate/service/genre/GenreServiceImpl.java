package ru.yandex.practicum.filmorate.service.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.genre.GenreDao;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreDao genreDao;

    @Override
    public Collection<Genre> getAllGenres() {
        return genreDao.getAllGenres();
    }

    @Override
    public Genre getGenreById(int id) {
        Optional<Genre> optionalGenre = genreDao.getGenreById(id);

        if (optionalGenre.isPresent()) {
            return optionalGenre.get();
        } else {
            throw new NotFoundException(String.format("Жанр с id=%s не найден", id));
        }
    }
}
