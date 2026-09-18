package com.practicum.posts.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private Long id;
    private String title;
    private String text;
    private List<String> tags;
    private Long likesCount;
    private Long commentsCount;
}
