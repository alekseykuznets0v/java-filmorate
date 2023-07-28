-- Exported from QuickDBD: https://www.quickdatabasediagrams.com/
-- Link to schema: https://app.quickdatabasediagrams.com/#/d/rG3LDV
-- NOTE! If you have used non-SQL datatypes in your design, you will have to change these here.

-- Modify this code to update the DB schema diagram.
-- To reset the sample schema, replace everything with
-- two dots ('..' - without quotes).

CREATE TABLE "films" (
    "id" bigint   NOT NULL,
    "name" varchar(255)   NOT NULL,
    "description" varchar(200)   NOT NULL,
    "release_date" date   NOT NULL,
    "duration" int   NOT NULL,
    "mpa_id" int   NOT NULL,
    CONSTRAINT "pk_films" PRIMARY KEY (
        "id"
     )
);

CREATE TABLE "users" (
    "id" bigint   NOT NULL,
    "email" varchar(255)   NOT NULL,
    "login" varchar(255)   NOT NULL,
    "name" varchar(255)   NOT NULL,
    CONSTRAINT "pk_users" PRIMARY KEY (
        "id"
     )
);

CREATE TABLE "genres" (
    "id" int   NOT NULL,
    "name" varchar(255)   NOT NULL,
    CONSTRAINT "pk_genres" PRIMARY KEY (
        "id"
     )
);

CREATE TABLE "film_genres" (
    "film_id" bigint   NOT NULL,
    "genre_id" int   NOT NULL,
    CONSTRAINT "pk_film_genres" PRIMARY KEY (
        "film_id","genre_id"
     )
);

CREATE TABLE "mpa" (
    "id" int   NOT NULL,
    "name" varchar(255)   NOT NULL,
    CONSTRAINT "pk_mpa" PRIMARY KEY (
        "id"
     )
);

CREATE TABLE "friends" (
    "user_id" bigint   NOT NULL,
    "friend_id" bigint   NOT NULL,
    "approved" boolean   NOT NULL,
    CONSTRAINT "pk_friends" PRIMARY KEY (
        "user_id","friend_id"
     )
);

CREATE TABLE "likes" (
    "film_id" bigint   NOT NULL,
    "user_id" bigint   NOT NULL,
    CONSTRAINT "pk_likes" PRIMARY KEY (
        "film_id","user_id"
     )
);

ALTER TABLE "films" ADD CONSTRAINT "fk_films_mpa_id" FOREIGN KEY("mpa_id")
REFERENCES "mpa" ("id");

ALTER TABLE "film_genres" ADD CONSTRAINT "fk_film_genres_film_id" FOREIGN KEY("film_id")
REFERENCES "films" ("id");

ALTER TABLE "film_genres" ADD CONSTRAINT "fk_film_genres_genre_id" FOREIGN KEY("genre_id")
REFERENCES "genres" ("id");

ALTER TABLE "friends" ADD CONSTRAINT "fk_friends_user_id" FOREIGN KEY("user_id")
REFERENCES "users" ("id");

ALTER TABLE "friends" ADD CONSTRAINT "fk_friends_friend_id" FOREIGN KEY("friend_id")
REFERENCES "users" ("id");

ALTER TABLE "likes" ADD CONSTRAINT "fk_likes_film_id" FOREIGN KEY("film_id")
REFERENCES "films" ("id");

ALTER TABLE "likes" ADD CONSTRAINT "fk_likes_user_id" FOREIGN KEY("user_id")
REFERENCES "users" ("id");

