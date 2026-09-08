package com.practicum.posts.domain;

import lombok.Data;

import java.util.List;

@Data
public class Post {
    private Long id;
    private String title;
    private String text;
    private List<String> tags;
    private Long likesCount;
    private Long commentsCount;
}
