package com.practicum.posts.domain;

import com.practicum.BaseIntegrationTest;
import com.practicum.comments.domain.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PostRepositoryTest extends BaseIntegrationTest {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        insertDefaultData();
    }

    @Test
    void getPostById_returnsPostWithTagsAndCounts() {
        Post post = postRepository.getPostById(1L).orElseThrow();

        assertEquals("Spring Boot", post.getTitle());
        assertEquals(5L, post.getLikesCount());
        assertEquals(2L, post.getCommentsCount());
        assertTrue(post.getTags().containsAll(List.of("java", "spring")));
    }

    @Test
    void findPage_filtersByTitleAndTags() {
        List<Post> posts = postRepository.findPage("spring", List.of("java"), 1, 10);
        assertEquals(1, posts.size());
        assertEquals(1L, posts.get(0).getId());

        List<Post> empty = postRepository.findPage("h2", List.of("java"), 1, 10);
        assertTrue(empty.isEmpty());
    }

    @Test
    void count_returnsFilteredCount() {
        assertEquals(1L, postRepository.count("spring", List.of("java")));
        assertEquals(2L, postRepository.count("", List.of()));
    }

    @Test
    void save_persistsPostAndTags() {
        Post post = new Post();
        post.setTitle("New post");
        post.setText("New text");
        post.setTags(List.of("new", "java"));

        Post saved = postRepository.save(post);

        assertNotNull(saved.getId());

        Post fromDb = postRepository.getPostById(saved.getId()).orElseThrow();
        assertEquals("New post", fromDb.getTitle());
        assertTrue(fromDb.getTags().containsAll(List.of("new", "java")));
    }

    @Test
    void update_replacesTags() {
        Post post = postRepository.getPostById(1L).orElseThrow();
        post.setTitle("Updated");
        post.setText("Updated text");
        post.setTags(List.of("sql"));

        Post updated = postRepository.update(post);

        assertEquals("Updated", updated.getTitle());
        assertEquals(List.of("sql"), updated.getTags());
    }

    @Test
    void delete_removesPostAndComments() {
        postRepository.delete(1L);

        assertTrue(postRepository.getPostById(1L).isEmpty());
        assertTrue(commentRepository.findByPostId(1L).isEmpty());
    }

    @Test
    void incrementLikes_incrementsAndReturnsNewValue() {
        assertEquals(6L, postRepository.incrementLikes(1L));
        assertEquals(7L, postRepository.incrementLikes(1L));
    }

    @Test
    void updateImage_and_getImage() {
        byte[] image = new byte[]{1, 2, 3};
        postRepository.updateImage(1L, image);

        assertArrayEquals(image, postRepository.getImage(1L).orElseThrow());
    }

    @Test
    void existsById_works() {
        assertTrue(postRepository.existsById(1L));
        assertFalse(postRepository.existsById(999L));
    }
}
