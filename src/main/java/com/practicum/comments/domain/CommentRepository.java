package com.practicum.comments.domain;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    List<Comment> findByPostId(Long postId);

    Optional<Comment> findById(Long id);

    Comment save(Comment comment);

    Comment update(Comment comment);

    void delete(Long id);

    boolean existsById(Long id);
}
