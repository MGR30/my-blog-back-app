package com.practicum.posts.api;

import com.practicum.posts.domain.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
public class PostController {
    private PostService postService;

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }
}
