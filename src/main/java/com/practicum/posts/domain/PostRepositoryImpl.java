package com.practicum.posts.domain;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class PostRepositoryImpl implements PostRepository {
    private JdbcTemplate jdbcTemplate;
    private PostRowMapper postRowMapper;

    private static final String SELECT_BASE = """
            SELECT p.id,
                   p.title,
                   p.text,
                   p.likes_count,
                   (SELECT COUNT(*) FROM comment c WHERE c.post_id = p.id) AS comment_count
            FROM post p
            WHERE 1 = 1
            """;

    @Override
    public Optional<Post> getPostById(Long id) {
        String sql = """
                SELECT p.id,
                       p.title,
                       p.text,
                       p.likes_count,
                       (SELECT COUNT(*) FROM comment c WHERE c.post_id = p.id) AS comment_count
                FROM post p
                WHERE p.id = ?
                """;
        try {
            Post post = jdbcTemplate.queryForObject(sql, postRowMapper, id);
            post.setTags(getTagsByPostId(id));
            return Optional.of(post);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Post> findPage(String titleSubstring, List<String> tags,
                               int pageNumber, int pageSize) {
        StringBuilder sql = new StringBuilder(SELECT_BASE);
        List<Object> args = new ArrayList<>();
        appendFilters(sql, args, titleSubstring, tags);
        sql.append(" ORDER BY p.id DESC LIMIT ? OFFSET ?");
        args.add(pageSize);
        args.add((long) (pageNumber - 1) * pageSize);

        List<Post> posts = jdbcTemplate.query(sql.toString(), postRowMapper, args.toArray());
        for (Post post : posts) {
            post.setTags(getTagsByPostId(post.getId()));
        }
        return posts;
    }

    @Override
    @Transactional
    public Post save(Post post) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO post (title, text, likes_count) VALUES (?, ?, 0)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getText());
            return ps;
        }, keyHolder);
        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        syncTags(id, post.getTags());
        return getPostById(id).orElseThrow();
    }

    @Override
    @Transactional
    public Post update(Post post) {
        jdbcTemplate.update("UPDATE post SET title = ?, text = ? WHERE id = ?",
                post.getTitle(), post.getText(), post.getId());
        jdbcTemplate.update("DELETE FROM post_tag WHERE post_id = ?", post.getId());
        syncTags(post.getId(), post.getTags());
        return getPostById(post.getId()).orElseThrow();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM post WHERE id = ?", id);
    }

    @Override
    public boolean existsById(Long id) {
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM post WHERE id = ?", Integer.class, id);
        return cnt != null && cnt > 0;
    }

    @Override
    public long incrementLikes(Long id) {
        jdbcTemplate.update("UPDATE post SET likes_count = likes_count + 1 WHERE id = ?", id);
        Long likes = jdbcTemplate.queryForObject(
                "SELECT likes_count FROM post WHERE id = ?", Long.class, id);
        return likes == null ? 0L : likes;
    }

    @Override
    public void updateImage(Long id, byte[] image) {
        jdbcTemplate.update("UPDATE post SET image = ? WHERE id = ?", image, id);
    }

    @Override
    public Optional<byte[]> getImage(Long id) {
        try {
            byte[] image = jdbcTemplate.queryForObject(
                    "SELECT image FROM post WHERE id = ?", byte[].class, id);
            return Optional.ofNullable(image);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public long count(String titleSubstring, List<String> tags) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM post p WHERE 1 = 1");
        List<Object> args = new ArrayList<>();
        appendFilters(sql, args, titleSubstring, tags);
        Long total = jdbcTemplate.queryForObject(sql.toString(), Long.class, args.toArray());
        return total == null ? 0L : total;
    }

    private void syncTags(Long postId, List<String> tags) {
        if (tags == null || tags.isEmpty()) return;

        for (String tag : tags) {
            if (tag == null || tag.isBlank()) continue;

            String normalized = tag.trim().toLowerCase();

            jdbcTemplate.update("""
                    INSERT INTO tag (name)
                    SELECT ? WHERE NOT EXISTS (
                        SELECT 1 FROM tag WHERE name = ?
                    )
                    """, normalized, normalized);

            Long tagId = jdbcTemplate.queryForObject(
                    "SELECT id FROM tag WHERE name = ?", Long.class, normalized);

            jdbcTemplate.update("""
                    INSERT INTO post_tag (post_id, tag_id)
                    SELECT ?, ? WHERE NOT EXISTS (
                        SELECT 1 FROM post_tag
                        WHERE post_id = ? AND tag_id = ?
                    )
                    """, postId, tagId, postId, tagId);
        }
    }

    private void appendFilters(StringBuilder sql, List<Object> args,
                               String titleSubstring, List<String> tags) {
        if (titleSubstring != null && !titleSubstring.isBlank()) {
            sql.append(" AND LOWER(p.title) LIKE ?");
            args.add("%" + titleSubstring.toLowerCase() + "%");
        }
        if (tags != null) {
            for (String tag : tags) {
                sql.append("""
                         AND EXISTS (
                             SELECT 1 FROM post_tag pt
                             JOIN tag t ON t.id = pt.tag_id
                             WHERE pt.post_id = p.id AND LOWER(t.name) = LOWER(?)
                         )
                        """);
                args.add(tag);
            }
        }
    }

    private List<String> getTagsByPostId(Long postId) {
        String getTagsSql = """
                SELECT t.name
                FROM tag t
                JOIN post_tag pt ON t.id = pt.tag_id
                WHERE pt.post_id = ?
                """;
        return jdbcTemplate.queryForList(getTagsSql, String.class, postId);
    }
}
