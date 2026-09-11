package com.practicum.comments.domain;

import com.practicum.comments.api.CommentRequest;
import com.practicum.comments.api.CommentResponse;
import com.practicum.posts.domain.PostRepository;
import com.practicum.shared.exception.CommentNotFoundException;
import com.practicum.shared.exception.PostNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;

    public List<CommentResponse> getComments(Long postId) {
        ensurePostExists(postId);
        return commentRepository.findByPostId(postId).stream()
                .map(commentMapper::toResponse).toList();
    }

    public CommentResponse getComment(Long postId, Long commentId) {
        ensurePostExists(postId);
        Comment comment = commentRepository.findById(commentId)
                .filter(x -> x.getPostId().equals(postId))
                .orElseThrow(() -> new CommentNotFoundException("Комментарий не найден"));
        return commentMapper.toResponse(comment);
    }

    @Transactional
    public CommentResponse create(Long postId, CommentRequest request) {
        ensurePostExists(postId);
        Comment comment = commentMapper.toDomain(request);
        comment.setPostId(postId);
        return commentMapper.toResponse(commentRepository.save(comment));
    }

    @Transactional
    public CommentResponse update(Long postId, Long commentId, CommentRequest request) {
        ensurePostExists(postId);
        Comment comment = commentRepository.findById(commentId)
                .filter(x -> x.getPostId().equals(postId))
                .orElseThrow(() -> new CommentNotFoundException("Комментарий не найден"));
        comment.setText(request.getText());
        return commentMapper.toResponse(commentRepository.update(comment));
    }

    @Transactional
    public void delete(Long postId, Long commentId) {
        ensurePostExists(postId);

        Comment comment = commentRepository.findById(commentId)
                .filter(x -> x.getPostId().equals(postId))
                .orElseThrow(() -> new CommentNotFoundException("Комментарий не найден"));

        commentRepository.delete(comment.getId());;
    }

    private void ensurePostExists(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException("Пост не найден");
        }
    }
}
