package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class InMemoryStorage<T extends Entity> {
    protected long identifier;
    protected final Map<Long, T> storage;

    protected InMemoryStorage() {
        this.identifier = 0;
        this.storage = new HashMap<>();
    }

    protected long getIdentifier() {
        return ++identifier;
    }

    public void setIdentifier(long identifier) {
        this.identifier = identifier;
    }
}
