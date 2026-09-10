package com.practicum.posts.domain;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
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
    public long count(String titleSubstring, List<String> tags) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM post p WHERE 1 = 1");
        List<Object> args = new ArrayList<>();
        appendFilters(sql, args, titleSubstring, tags);
        Long total = jdbcTemplate.queryForObject(sql.toString(), Long.class, args.toArray());
        return total == null ? 0L : total;
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
