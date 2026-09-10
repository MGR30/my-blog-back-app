package com.practicum.comments.domain;

import com.practicum.comments.api.CommentRequest;
import com.practicum.comments.api.CommentResponse;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public CommentResponse toResponse(Comment c) {
        return new CommentResponse(c.getId(), c.getText(), c.getPostId());
    }

    public Comment toDomain(CommentRequest r) {
        Comment comment = new Comment();
        comment.setText(r.getText());
        comment.setPostId(r.getPostId());
        return comment;
    }
}
