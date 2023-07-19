package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Entity;

import java.util.HashMap;
import java.util.Map;

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

}
