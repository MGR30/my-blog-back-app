package com.practicum.posts.domain;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Optional<Post> getPostById(Long id);

    List<Post> findPage(String titleSubstring, List<String> tags,
                        int pageNumber, int pageSize);

    long count(String titleSubstring, List<String> tags);
}
