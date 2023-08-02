DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS film_genres CASCADE;
DROP TABLE IF EXISTS likes CASCADE;
DROP TABLE IF EXISTS friends CASCADE;
DROP TABLE IF EXISTS mpa CASCADE;
DROP TABLE IF EXISTS genres CASCADE;

CREATE TABLE IF NOT EXISTS mpa (
    id            INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name          VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS genres (
    id            INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name          VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
    id            BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name          VARCHAR NOT NULL,
    description   VARCHAR(200),
    release_date  DATE NOT NULL,
    duration      INTEGER CHECK (duration > 0),
    mpa_id        INTEGER REFERENCES mpa (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS users (
    id            BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email         VARCHAR NOT NULL UNIQUE,
    login         VARCHAR NOT NULL UNIQUE,
    name          VARCHAR NOT NULL,
    birthday      DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genres (
    film_id       BIGINT  NOT NULL REFERENCES films (id) ON DELETE CASCADE,
    genre_id      INTEGER NOT NULL REFERENCES genres (id) ON DELETE RESTRICT,
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS likes (
    film_id       BIGINT NOT NULL REFERENCES films (id) ON DELETE CASCADE,
    user_id       BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS friends (
    user_id       BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    friend_id     BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    approved      BOOLEAN,
    PRIMARY KEY (user_id, friend_id)
);