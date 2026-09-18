# my-blog-back-app

Бэкенд приложения-блога на Spring Framework 7 без Spring Boot. Приложение работает в сервлет-контейнере Tomcat/Jetty, предоставляет REST API для фронтенда и хранит посты, комментарии, теги и картинки постов в реляционной БД (PostgreSQL или H2).

## Стек

- **Java 21**
- **Spring Framework 7** — Core, Context, Web, WebMVC, JDBC, TX
- **Spring Test** — интеграционные и юнит-тесты
- **JUnit 5** + **Mockito** — тестирование
- **Jackson** — JSON-сериализация
- **Lombok** — уменьшение шаблонного кода
- **PostgreSQL** — основная БД
- **H2** — встроенная БД для тестов и локального запуска
- **Maven** — сборка (`war`)
- **Tomcat (embedded)** — контейнер для локального запуска

## Архитектура

```
Controller → Service → Repository → Database
```

Слои:

- `com.practicum.posts.api` — контроллер и DTO для постов
- `com.practicum.comments.api` — контроллер и DTO для комментариев
- `com.practicum.posts.domain` — модель, репозиторий, сервис, маппер постов
- `com.practicum.comments.domain` — модель, репозиторий, сервис, маппер комментариев
- `com.practicum.config` — Spring-конфигурация, DataSource, Jackson
- `com.practicum.shared` — общие DTO и обработка исключений

Схема БД:

- `post` — посты (id, title, text, image, likes_count, comment_count)
- `comment` — комментарии (id, post_id, text)
- `tag` — теги (id, name)
- `post_tag` — связи постов и тегов (post_id, tag_id)

Скрипты инициализации: `src/main/resources/schema.sql`, `src/main/resources/data.sql`.

## REST API

### Посты

| Метод | URL | Описание |
|-------|-----|----------|
| GET | `/api/posts?search=&pageNumber=&pageSize=` | Список постов с фильтрацией и пагинацией |
| GET | `/api/posts/{id}` | Получить пост |
| POST | `/api/posts` | Создать пост |
| PUT | `/api/posts/{id}` | Обновить пост |
| DELETE | `/api/posts/{id}` | Удалить пост (вместе с комментариями) |
| POST | `/api/posts/{id}/likes` | Инкремент лайков, возвращает новое значение |
| PUT | `/api/posts/{id}/image` | Загрузить картинку поста (`multipart/form-data`, поле `image`) |
| GET | `/api/posts/{id}/image` | Получить картинку поста |

### Комментарии

| Метод | URL | Описание |
|-------|-----|----------|
| GET | `/api/posts/{postId}/comments` | Список комментариев поста |
| GET | `/api/posts/{postId}/comments/{commentId}` | Один комментарий |
| POST | `/api/posts/{postId}/comments` | Создать комментарий |
| PUT | `/api/posts/{postId}/comments/{commentId}` | Обновить комментарий |
| DELETE | `/api/posts/{postId}/comments/{commentId}` | Удалить комментарий |

## Конфигурация

### Профили Spring

- `h2` — встроенная БД (используется `db-h2.properties`)
- `postgres` — PostgreSQL (используется `db-postgres.properties`)

### Файлы свойств

`src/main/resources/db-h2.properties`:

```properties
db.driver=org.h2.Driver
db.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
db.username=sa
db.password=
db.schema=schema.sql
db.data=data.sql
```

`src/main/resources/db-postgres.properties`:

```properties
db.driver=org.postgresql.Driver
db.url=jdbc:postgresql://localhost:5432/blog
db.username=blog
db.password=blog
db.schema=schema.sql
db.data=
```

## Сборка

```bash
mvn clean package
```

Результат: `target/my-blog-back-app.war`.

## Запуск

### Вариант 1. Локально через встроенный Tomcat (профиль `h2`)

Запусти класс `com.practicum.ApiApplication` в IDE. По умолчанию активируется профиль `h2`, приложение поднимается на `http://localhost:8080`.

### Вариант 2. WAR в свой Tomcat (профиль `postgres`)

1. Убедись, что PostgreSQL запущен, БД `blog` создана, а пользователь `blog/blog` имеет к ней доступ:

   ```sql
   CREATE DATABASE blog;
   CREATE USER blog WITH PASSWORD 'blog';
   GRANT ALL PRIVILEGES ON DATABASE blog TO blog;
   ```

2. Собери war:

   ```bash
   mvn clean package
   ```

3. Скопируй `target/my-blog-back-app.war` в `$CATALINA_HOME/webapps/`. В `web.xml` уже активирован профиль `postgres`.

4. Запусти Tomcat и открой `http://localhost:8080/my-blog-back-app/`.


## Тесты

```bash
mvn clean test
```

Покрытие:

- **Юнит-тесты сервисов** — `PostServiceTest`, `CommentServiceTest` (Mockito).
- **Интеграционные тесты контроллеров** — `PostControllerIntegrationTest`, `CommentControllerIntegrationTest` (MockMvc + H2).
- **Интеграционные тесты DAO** — `PostRepositoryTest`, `CommentRepositoryTest` (JdbcTemplate + H2).

Базовый класс `BaseIntegrationTest` поднимает Spring-контекст, активирует профиль `h2`, отключает загрузку `data.sql` и перед каждым тестом чистит таблицы. Контекст кешируется между классами тестов.

## Структура проекта

```
src
├── main
│   ├── java/com/practicum
│   │   ├── ApiApplication.java
│   │   ├── comments
│   │   │   ├── api       (CommentController, CommentRequest, CommentResponse)
│   │   │   └── domain    (Comment, CommentMapper, CommentRepository(+Impl), CommentRowMapper, CommentService)
│   │   ├── posts
│   │   │   ├── api       (PostController, PostRequest, PostResponse, PostsPageResponse)
│   │   │   └── domain    (Post, PostMapper, PostRepository(+Impl), PostRowMapper, PostService, SearchQuery)
│   │   ├── config        (ApplicationConfig, H2DataSourceConfig, PostgresDataSourceConfig, JacksonConfig)
│   │   └── shared        (ErrorResponse, GlobalExceptionHandler, *NotFoundException)
│   ├── resources
│   │   ├── db-h2.properties
│   │   ├── db-postgres.properties
│   │   ├── schema.sql
│   │   └── data.sql
│   └── webapp/WEB-INF/web.xml
└── test
    └── java/com/practicum
        ├── BaseIntegrationTest.java
        ├── comments
        │   ├── api       (CommentControllerIntegrationTest)
        │   └── domain    (CommentRepositoryTest, CommentServiceTest)
        └── posts
            ├── api       (PostControllerIntegrationTest)
            └── domain    (PostRepositoryTest, PostServiceTest)
```

## Лицензия

Учебный проект.