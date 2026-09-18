-- AI. Данный для запуска локально
-- Очистка таблиц (если нужно пересоздать данные)
-- TRUNCATE TABLE post_teg RESTART IDENTITY CASCADE;
-- TRUNCATE TABLE post RESTART IDENTITY CASCADE;
-- TRUNCATE TABLE tag RESTART IDENTITY CASCADE;

-- Вставка тегов
INSERT INTO tag (id, name) VALUES
(1, 'Java'),
(2, 'Spring'),
(3, 'SQL'),
(4, 'Hibernate'),
(5, 'JPA'),
(6, 'Maven'),
(7, 'Gradle'),
(8, 'Kotlin'),
(9, 'Docker'),
(10, 'CI/CD');

-- Вставка постов (id не указываем, генерируется автоматически)
INSERT INTO post (title, text, likes_count, comment_count) VALUES
('Введение в Spring Framework', 'Spring — это мощный фреймворк для Java-приложений...', 15, 3),
('Настройка H2 in-memory базы', 'H2 — легковесная БД, отлично подходит для тестирования...', 8, 1),
('JPA и Hibernate: основные аннотации', 'Рассмотрим @Entity, @Id, @OneToMany и другие...', 22, 5),
('Миграции с Flyway', 'Управление версиями схемы БД с помощью Flyway...', 12, 2),
('Docker для разработчиков Java', 'Как упаковать Spring Boot приложение в контейнер...', 30, 7),
('Сборка проекта с Gradle', 'Отличия от Maven и основные задачи...', 5, 0),
('Паттерны проектирования в Java', 'Singleton, Factory, Observer — примеры и применение...', 18, 4),
('Тестирование с JUnit 5', 'Написание юнит-тестов и интеграционных тестов...', 25, 6),
('REST API с Spring MVC', 'Создание контроллеров, обработка запросов, ResponseEntity...', 20, 8),
('Kotlin vs Java: сравнение', 'Синтаксис, производительность, популярность...', 10, 2);

-- Вставка связей пост-тег (используем полученные id)
INSERT INTO post_tag (post_id, tag_id) VALUES
((SELECT id FROM post WHERE title = 'Введение в Spring Framework'), (SELECT id FROM tag WHERE name = 'Spring')),
((SELECT id FROM post WHERE title = 'Введение в Spring Framework'), (SELECT id FROM tag WHERE name = 'Java')),
((SELECT id FROM post WHERE title = 'Настройка H2 in-memory базы'), (SELECT id FROM tag WHERE name = 'SQL')),
((SELECT id FROM post WHERE title = 'Настройка H2 in-memory базы'), (SELECT id FROM tag WHERE name = 'Hibernate')), -- добавили для примера
((SELECT id FROM post WHERE title = 'JPA и Hibernate: основные аннотации'), (SELECT id FROM tag WHERE name = 'JPA')),
((SELECT id FROM post WHERE title = 'JPA и Hibernate: основные аннотации'), (SELECT id FROM tag WHERE name = 'Hibernate')),
((SELECT id FROM post WHERE title = 'JPA и Hibernate: основные аннотации'), (SELECT id FROM tag WHERE name = 'SQL')),
((SELECT id FROM post WHERE title = 'Миграции с Flyway'), (SELECT id FROM tag WHERE name = 'SQL')),
((SELECT id FROM post WHERE title = 'Миграции с Flyway'), (SELECT id FROM tag WHERE name = 'CI/CD')),
((SELECT id FROM post WHERE title = 'Docker для разработчиков Java'), (SELECT id FROM tag WHERE name = 'Docker')),
((SELECT id FROM post WHERE title = 'Docker для разработчиков Java'), (SELECT id FROM tag WHERE name = 'Java')),
((SELECT id FROM post WHERE title = 'Сборка проекта с Gradle'), (SELECT id FROM tag WHERE name = 'Gradle')),
((SELECT id FROM post WHERE title = 'Сборка проекта с Gradle'), (SELECT id FROM tag WHERE name = 'Maven')),
((SELECT id FROM post WHERE title = 'Паттерны проектирования в Java'), (SELECT id FROM tag WHERE name = 'Java')),
((SELECT id FROM post WHERE title = 'Тестирование с JUnit 5'), (SELECT id FROM tag WHERE name = 'Java')),
((SELECT id FROM post WHERE title = 'REST API с Spring MVC'), (SELECT id FROM tag WHERE name = 'Spring')),
((SELECT id FROM post WHERE title = 'REST API с Spring MVC'), (SELECT id FROM tag WHERE name = 'Java')),
((SELECT id FROM post WHERE title = 'Kotlin vs Java: сравнение'), (SELECT id FROM tag WHERE name = 'Kotlin')),
((SELECT id FROM post WHERE title = 'Kotlin vs Java: сравнение'), (SELECT id FROM tag WHERE name = 'Java'));