package com.practicum.posts.domain;

import com.practicum.posts.api.PostResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PostService {
    private PostRepository postRepository;
    private PostMapper postMapper;

    public PostResponse getPostById(Long id) {
        return postMapper.toResponse(postRepository.getPostById(id));
    }
}
