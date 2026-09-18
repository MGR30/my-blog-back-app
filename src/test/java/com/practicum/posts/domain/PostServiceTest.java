package com.practicum.posts.domain;

import com.practicum.posts.api.PostRequest;
import com.practicum.posts.api.PostResponse;
import com.practicum.posts.api.PostsPageResponse;
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
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    private PostMapper postMapper;
    private PostService postService;

    @BeforeEach
    void setUp() {
        postMapper = new PostMapper();
        postService = new PostService(postRepository, postMapper);
    }

    @Test
    void getPostById_returnsResponse() {
        Post post = post(1L, "Title", "Text", 5L, 2L);
        when(postRepository.getPostById(1L)).thenReturn(Optional.of(post));

        PostResponse response = postService.getPostById(1L);

        assertEquals(1L, response.getId());
        assertEquals("Title", response.getTitle());
        assertEquals(5L, response.getLikesCount());
        assertEquals(2L, response.getCommentsCount());
    }

    @Test
    void getPostById_notFound_throws() {
        when(postRepository.getPostById(1L)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.getPostById(1L));
    }

    @Test
    void getPosts_parsesSearchAndReturnsPage() {
        when(postRepository.count("spring", List.of("java", "sql"))).thenReturn(10L);
        when(postRepository.findPage("spring", List.of("java", "sql"), 2, 5))
                .thenReturn(List.of(post(1L, "Spring", "Text", 0L, 0L)));

        PostsPageResponse response = postService.getPosts("spring #java #sql", 2, 5);

        assertEquals(1, response.getPosts().size());
        assertTrue(response.isHasPrev());
        assertFalse(response.isHasNext());
        assertEquals(2, response.getLastPage());

        verify(postRepository).count("spring", List.of("java", "sql"));
        verify(postRepository).findPage("spring", List.of("java", "sql"), 2, 5);
    }

    @Test
    void createPost_validatesAndSaves() {
        PostRequest request = new PostRequest("Title", "Text", List.of("java"));
        Post saved = post(100L, "Title", "Text", 0L, 0L);
        when(postRepository.save(any(Post.class))).thenReturn(saved);

        PostResponse response = postService.createPost(request);

        assertEquals(100L, response.getId());
        assertEquals("Title", response.getTitle());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void createPost_blankTitle_throws() {
        PostRequest request = new PostRequest(" ", "Text", List.of());

        assertThrows(IllegalArgumentException.class, () -> postService.createPost(request));
        verifyNoInteractions(postRepository);
    }

    @Test
    void updatePost_notFound_throws() {
        when(postRepository.existsById(1L)).thenReturn(false);

        assertThrows(PostNotFoundException.class,
                () -> postService.updatePost(1L, new PostRequest("Title", "Text", List.of())));
    }

    @Test
    void deletePost_notFound_throws() {
        when(postRepository.existsById(1L)).thenReturn(false);

        assertThrows(PostNotFoundException.class, () -> postService.deletePost(1L));
    }

    @Test
    void likePost_increments() {
        when(postRepository.existsById(1L)).thenReturn(true);
        when(postRepository.incrementLikes(1L)).thenReturn(6L);

        assertEquals(6L, postService.likePost(1L));
    }

    @Test
    void getImage_returnsBytes() {
        byte[] bytes = {1, 2, 3};
        when(postRepository.getImage(1L)).thenReturn(Optional.of(bytes));

        assertArrayEquals(bytes, postService.getImage(1L));
    }

    private Post post(Long id, String title, String text, Long likes, Long comments) {
        Post post = new Post();
        post.setId(id);
        post.setTitle(title);
        post.setText(text);
        post.setTags(List.of());
        post.setLikesCount(likes);
        post.setCommentsCount(comments);
        return post;
    }
}
