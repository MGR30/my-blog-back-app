package com.practicum.comments.domain;

import lombok.Data;

@Data
public class Comment {
    private Long id;
    private String text;
    private Long postId;
}
