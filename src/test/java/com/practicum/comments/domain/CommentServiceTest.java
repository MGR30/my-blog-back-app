package com.practicum.comments.domain;
import com.practicum.comments.api.CommentRequest;
import com.practicum.comments.api.CommentResponse;
import com.practicum.posts.domain.PostRepository;
import com.practicum.shared.exception.CommentNotFoundException;
import com.practicum.shared.exception.PostNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    private CommentMapper commentMapper;
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        commentMapper = new CommentMapper();
        commentService = new CommentService(commentRepository, postRepository, commentMapper);
    }

    @Test
    void getComments_returnsList() {
        when(postRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.findByPostId(1L))
                .thenReturn(List.of(comment(1L, "Text", 1L)));

        List<CommentResponse> comments = commentService.getComments(1L);

        assertEquals(1, comments.size());
        assertEquals("Text", comments.get(0).getText());
    }

    @Test
    void getComments_postNotFound_throws() {
        when(postRepository.existsById(1L)).thenReturn(false);

        assertThrows(PostNotFoundException.class, () -> commentService.getComments(1L));
    }

    @Test
    void getComment_notFound_throws() {
        when(postRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class,
                () -> commentService.getComment(1L, 2L));
    }

    @Test
    void create_savesComment() {
        when(postRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> {
            Comment c = inv.getArgument(0);
            c.setId(100L);
            return c;
        });

        CommentResponse response = commentService.create(
                1L, new CommentRequest("Text", 1L));

        assertEquals(100L, response.getId());
        assertEquals("Text", response.getText());
        assertEquals(1L, response.getPostId());
    }

    @Test
    void update_notFound_throws() {
        when(postRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class,
                () -> commentService.update(1L, 2L, new CommentRequest("Text", 1L)));
    }

    @Test
    void delete_success() {
        when(postRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.findById(2L))
                .thenReturn(Optional.of(comment(2L, "Text", 1L)));

        commentService.delete(1L, 2L);

        verify(commentRepository).delete(2L);
    }

    @Test
    void delete_wrongPost_throws() {
        when(postRepository.existsById(2L)).thenReturn(true);
        when(commentRepository.findById(1L))
                .thenReturn(Optional.of(comment(1L, "Text", 1L)));

        assertThrows(CommentNotFoundException.class,
                () -> commentService.delete(2L, 1L));
    }

    private Comment comment(Long id, String text, Long postId) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setText(text);
        comment.setPostId(postId);
        return comment;
    }
}
