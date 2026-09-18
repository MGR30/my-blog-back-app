package com.practicum.posts.domain;

import com.practicum.posts.api.PostRequest;
import com.practicum.posts.api.PostResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostMapper {
    private static final int PREVIEW_LENGTH = 128;

    public PostResponse toResponse(Post post, boolean truncateText) {
        String text = post.getText();
        if (truncateText && text != null && text.length() > PREVIEW_LENGTH) {
            text = text.substring(0, PREVIEW_LENGTH) + "…";
        }
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                text,
                post.getTags() == null ? List.of() : post.getTags(),
                post.getLikesCount() == null ? 0L : post.getLikesCount(),
                post.getCommentsCount() == null ? 0L : post.getCommentsCount()
        );
    }

    public Post toDomain(PostRequest request) {
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setText(request.getText());
        post.setTags(request.getTags() == null ? List.of() : request.getTags());
        return post;
    }
}
