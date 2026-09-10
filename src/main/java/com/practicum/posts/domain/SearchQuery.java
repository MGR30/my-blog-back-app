package com.practicum.posts.domain;

import java.util.List;

public record SearchQuery(String titleSubstring, List<String> tags) {
    public static SearchQuery empty() {
        return new SearchQuery("", List.of());
    }
}
