package com.practicum.comments.domain;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {
    private final JdbcTemplate jdbcTemplate;
    private final CommentRowMapper commentRowMapper;

    @Override
    public List<Comment> findByPostId(Long postId) {
        return jdbcTemplate.query(
                "SELECT id, text, post_id FROM comment WHERE post_id = ? ORDER BY id",
                commentRowMapper, postId);
    }

    @Override
    public Optional<Comment> findById(Long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    "SELECT id, text, post_id FROM comment WHERE id = ?",
                    commentRowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Comment save(Comment comment) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO comment (text, post_id) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, comment.getText());
            ps.setLong(2, comment.getPostId());
            return ps;
        }, kh);
        comment.setId(Objects.requireNonNull(kh.getKey()).longValue());
        return comment;
    }

    @Override
    public Comment update(Comment comment) {
        jdbcTemplate.update("UPDATE comment SET text = ? WHERE id = ?",
                comment.getText(), comment.getId());
        return comment;
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM comment WHERE id = ?", id);
    }

    @Override
    public boolean existsById(Long id) {
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM comment WHERE id = ?", Integer.class, id);
        return cnt != null && cnt > 0;
    }
}
