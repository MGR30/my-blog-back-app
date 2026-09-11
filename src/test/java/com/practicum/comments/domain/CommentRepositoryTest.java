package com.practicum.comments.domain;


import com.practicum.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class CommentRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        insertDefaultData();
    }

    @Test
    void findByPostId_returnsComments() {
        var comments = commentRepository.findByPostId(1L);

        assertEquals(2, comments.size());
        assertEquals("First comment", comments.get(0).getText());
    }

    @Test
    void findById_returnsComment() {
        Comment comment = commentRepository.findById(2L).orElseThrow();

        assertEquals("Second comment", comment.getText());
        assertEquals(1L, comment.getPostId());
    }

    @Test
    void save_persistsComment() {
        Comment comment = new Comment();
        comment.setText("New comment");
        comment.setPostId(1L);

        Comment saved = commentRepository.save(comment);

        assertNotNull(saved.getId());
        assertEquals("New comment",
                commentRepository.findById(saved.getId()).orElseThrow().getText());
    }

    @Test
    void update_updatesText() {
        Comment comment = commentRepository.findById(1L).orElseThrow();
        comment.setText("Updated");

        commentRepository.update(comment);

        assertEquals("Updated",
                commentRepository.findById(1L).orElseThrow().getText());
    }

    @Test
    void delete_removesComment() {
        commentRepository.delete(1L);
        assertFalse(commentRepository.existsById(1L));
    }

    @Test
    void existsById_works() {
        assertTrue(commentRepository.existsById(1L));
        assertFalse(commentRepository.existsById(999L));
    }
}
