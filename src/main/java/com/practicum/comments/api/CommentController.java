package com.practicum.comments.api;

import com.practicum.comments.domain.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getComments(postId));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponse> getComment(@PathVariable Long postId,
                                                      @PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.getComment(postId, commentId));
    }

    @PostMapping
    public ResponseEntity<CommentResponse> create(@PathVariable Long postId,
                                                  @RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.create(postId, request));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> update(@PathVariable Long postId,
                                                  @PathVariable Long commentId,
                                                  @RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.update(postId, commentId, request));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long postId,
                                       @PathVariable Long commentId) {
        commentService.delete(postId, commentId);
        return ResponseEntity.ok().build();
    }
}
