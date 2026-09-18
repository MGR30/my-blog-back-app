package com.practicum;

import com.practicum.config.ApplicationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringJUnitConfig(ApplicationConfig.class)
@WebAppConfiguration
@ActiveProfiles("h2")
@TestPropertySource(properties = {"db.data="})
public abstract class BaseIntegrationTest {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @BeforeEach
    void resetDatabase() {
        jdbcTemplate.execute("DELETE FROM comment");
        jdbcTemplate.execute("DELETE FROM post_tag");
        jdbcTemplate.execute("DELETE FROM post");
        jdbcTemplate.execute("DELETE FROM tag");

        jdbcTemplate.execute("ALTER TABLE comment ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE post ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE tag ALTER COLUMN id RESTART WITH 1");
    }

    protected void insertDefaultData() {
        jdbcTemplate.update("INSERT INTO post (id, title, text, likes_count) VALUES (?,?,?,?)",
                1L, "Spring Boot", "Spring Boot ".repeat(20), 5);
        jdbcTemplate.update("INSERT INTO post (id, title, text, likes_count) VALUES (?,?,?,?)",
                2L, "H2 Database", "H2 is a lightweight in-memory database.", 1);

        jdbcTemplate.update("INSERT INTO tag (id, name) VALUES (?,?)", 1L, "java");
        jdbcTemplate.update("INSERT INTO tag (id, name) VALUES (?,?)", 2L, "spring");
        jdbcTemplate.update("INSERT INTO tag (id, name) VALUES (?,?)", 3L, "sql");

        jdbcTemplate.update("INSERT INTO post_tag (post_id, tag_id) VALUES (?,?)", 1L, 1L);
        jdbcTemplate.update("INSERT INTO post_tag (post_id, tag_id) VALUES (?,?)", 1L, 2L);
        jdbcTemplate.update("INSERT INTO post_tag (post_id, tag_id) VALUES (?,?)", 2L, 3L);

        jdbcTemplate.update("INSERT INTO comment (id, post_id, text) VALUES (?,?,?)",
                1L, 1L, "First comment");
        jdbcTemplate.update("INSERT INTO comment (id, post_id, text) VALUES (?,?,?)",
                2L, 1L, "Second comment");
        jdbcTemplate.update("INSERT INTO comment (id, post_id, text) VALUES (?,?,?)",
                3L, 2L, "Comment for second post");

        jdbcTemplate.execute("ALTER TABLE post ALTER COLUMN id RESTART WITH 100");
        jdbcTemplate.execute("ALTER TABLE tag ALTER COLUMN id RESTART WITH 100");
        jdbcTemplate.execute("ALTER TABLE comment ALTER COLUMN id RESTART WITH 100");
    }
}
