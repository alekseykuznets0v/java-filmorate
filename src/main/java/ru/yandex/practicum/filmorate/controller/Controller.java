package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.model.Entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

abstract class Controller<T extends Entity> {
    protected int identifier = 0;
    protected final Map<Integer, T> storage = new HashMap<>();

    protected Collection<T> getAllValues() {
        return storage.values();
    }

    protected int getIdentifier() {
        return ++identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public Map<Integer, T> getStorage() {
        return storage;
    }
}
