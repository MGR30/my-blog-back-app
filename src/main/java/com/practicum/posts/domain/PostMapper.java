package com.practicum.posts.domain;

import com.practicum.posts.api.PostResponse;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {
    public PostResponse toResponse(Post post){
        return new PostResponse();
    }
}
