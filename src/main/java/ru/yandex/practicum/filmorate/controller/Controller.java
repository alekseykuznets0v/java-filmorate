package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.model.Entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

abstract class Controller<T extends Entity> {
    protected long identifier = 0;
    protected final Map<Long, T> storage = new HashMap<>();

    protected Collection<T> getAllValues() {
        return storage.values();
    }

    protected long getIdentifier() {
        return ++identifier;
    }

    public void setIdentifier(long identifier) {
        this.identifier = identifier;
    }

    public Map<Long, T> getStorage() {
        return storage;
    }
}
