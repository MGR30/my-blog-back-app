package com.practicum.posts.domain;

import com.practicum.posts.api.PostRequest;
import com.practicum.posts.api.PostResponse;
import com.practicum.posts.api.PostsPageResponse;
import com.practicum.shared.exception.PostNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PostService {
    private static final String POST_NOT_FOUND_MESSAGE = "Пост не найден";
    private PostRepository postRepository;
    private PostMapper postMapper;

    public PostResponse getPostById(Long id) {
        Post post = postRepository.getPostById(id)
                .orElseThrow(() -> new PostNotFoundException(POST_NOT_FOUND_MESSAGE));
        return postMapper.toResponse(post, false);
    }

    public PostsPageResponse getPosts(String search, int pageNumber, int pageSize) {
        if (pageNumber < 1) pageNumber = 1;
        if (pageSize < 1) pageSize = 5;

        SearchQuery query = parseSearch(search);

        long total = postRepository.count(query.titleSubstring(), query.tags());
        List<PostResponse> posts = postRepository
                .findPage(query.titleSubstring(), query.tags(), pageNumber, pageSize)
                .stream()
                .map(p -> postMapper.toResponse(p, true))
                .toList();

        int lastPage = total == 0
                ? 1
                : (int) Math.ceil((double) total / pageSize);

        boolean hasPrev = pageNumber > 1;
        boolean hasNext = pageNumber < lastPage;

        return new PostsPageResponse(posts, hasPrev, hasNext, lastPage);
    }

    @Transactional
    public PostResponse createPost(PostRequest request) {
        validate(request);
        Post post = postMapper.toDomain(request);
        Post saved = postRepository.save(post);
        return postMapper.toResponse(saved, false);
    }

    @Transactional
    public PostResponse updatePost(Long id, PostRequest request) {
        validate(request);
        if (!postRepository.existsById(id)) {
            throw new PostNotFoundException(POST_NOT_FOUND_MESSAGE);
        }
        Post post = postMapper.toDomain(request);
        post.setId(id);
        return postMapper.toResponse(postRepository.update(post), false);
    }

    @Transactional
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new PostNotFoundException(POST_NOT_FOUND_MESSAGE);
        }
        postRepository.delete(id);
    }

    @Transactional
    public long likePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new PostNotFoundException(POST_NOT_FOUND_MESSAGE);
        }
        return postRepository.incrementLikes(id);
    }

    @Transactional
    public void updateImage(Long id, byte[] image) {
        if (!postRepository.existsById(id)) {
            throw new PostNotFoundException(POST_NOT_FOUND_MESSAGE);
        }
        postRepository.updateImage(id, image);
    }

    public byte[] getImage(Long id) {
        return postRepository.getImage(id)
                .orElseThrow(() -> new PostNotFoundException(POST_NOT_FOUND_MESSAGE));
    }

    private void validate(PostRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("Поле 'title' обязательно");
        }
        if (request.getText() == null || request.getText().isBlank()) {
            throw new IllegalArgumentException("Поле 'text' обязательно");
        }
    }

    private SearchQuery parseSearch(String search) {
        if (search == null || search.isBlank()) {
            return SearchQuery.empty();
        }
        List<String> tags = new ArrayList<>();
        List<String> words = new ArrayList<>();
        for (String token : search.trim().split("\\s+")) {
            if (token.isBlank()) continue;
            if (token.startsWith("#") && token.length() > 1) {
                tags.add(token.substring(1));
            } else {
                words.add(token);
            }
        }
        return new SearchQuery(String.join(" ", words), tags);
    }
}
