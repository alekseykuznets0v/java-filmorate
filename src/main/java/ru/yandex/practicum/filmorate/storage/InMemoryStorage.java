package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class InMemoryStorage<T extends Entity> implements Storage<T> {
    protected int identifier = 0;
    protected final Map<Integer, T> storage = new HashMap<>();

    @Override
    public Collection<T> getAllValues() {
        return storage.values();
    }

    @Override
    public Map<Integer, T> getStorage() {
        return storage;
    }

    @Override
    public T getById(int id) {
        if (storage.containsKey(id)) {
            return storage.get(id);
        } else {
            String warning = String.format("В базе данных нет значения для ключа id=%s", id);
            log.warn(ValidationException.class + ": " + warning);
            throw new ValidationException(warning);
        }
    }

    @Override
    public T add(T t) {
        int id = getIdentifier();
        t.setId(id);
        return storage.put(id, t);
    }

    @Override
    public T update(T t) {
        if (storage.containsKey(t.getId())) {
            return storage.put(t.getId(), t);
        } else {
            String warning = String.format("В базе данных нет значения для ключа id=%s", t.getId());
            log.warn(ValidationException.class + ": " + warning);
            throw new ValidationException(warning);
        }
    }

    protected int getIdentifier() {
        return ++identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }
}
