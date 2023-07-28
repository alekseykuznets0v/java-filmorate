# java-filmorate

### Модель базы данных представлена на ER-диаграмме

---
![Модель базы данных](QuickDBD-FilmorateERDiagram.PNG)

### Примеры запросов

---

<details>
<summary>Для фильмов:</summary>

* Получение всех фильмов:
```SQL
SELECT f.id,
       f.name,
       f.description,
       f.release_date,
       f.duration,
       mp.name AS mpa_rating,
FROM films AS f
JOIN mpa AS mp ON f.mpa_id = mp.id;
```

* Получение фильма по идентификатору:
```SQL
SELECT f.id,
       f.name,
       f.description,
       f.release_date,
       f.duration,
       mp.name AS mpa_rating,
FROM films AS f
JOIN mpa AS mp ON f.mpa_id = mp.id
WHERE f.id = ?;
```

* Получение топ-чарта фильмов по количеству лайков:
```SQL
SELECT f.id,
       f.name,
       f.description,
       f.release_date,
       f.duration,
       mp.name AS mpa_rating,
       COUNT(l.user_id) AS likes
FROM films AS f
JOIN mpa AS mp ON f.mpa_id = mp.id
LEFT JOIN likes AS l ON f.id = l.film_id
GROUP BY f.id
ORDER BY likes DESC 
LIMIT ?;
```
</details>

<details>
<summary>Для пользователей:</summary>

* Получение всех пользователей:

```SQL
SELECT *
FROM users
```

* Получение пользователя по идентификатору:
```SQL
SELECT *
FROM users
WHERE id = ?
```   

</details>
