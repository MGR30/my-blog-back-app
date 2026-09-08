package com.practicum.posts.domain;

import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository {
    Post getPostById(Long id);
}
