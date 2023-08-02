package ru.yandex.practicum.filmorate.service.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.dao.mpa.MpaDao;

import java.util.Collection;
@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {
    private final MpaDao mpaDao;

    @Override
    public Collection<MpaRating> getAllMpa() {
        return mpaDao.getAllMpa();
    }

    @Override
    public MpaRating getMpaById(int id) {
        return mpaDao.getMpaById(id);
    }
}
