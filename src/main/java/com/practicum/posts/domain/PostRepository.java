package com.practicum.posts.domain;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Optional<Post> getPostById(Long id);

    List<Post> findPage(String titleSubstring, List<String> tags,
                        int pageNumber, int pageSize);

    long count(String titleSubstring, List<String> tags);

    Post save(Post post);

    Post update(Post post);

    void delete(Long id);

    boolean existsById(Long id);

    long incrementLikes(Long id);

    void updateImage(Long id, byte[] image);

    Optional<byte[]> getImage(Long id);
}
