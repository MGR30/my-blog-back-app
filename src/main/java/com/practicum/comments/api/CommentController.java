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
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable("postId") Long postId) {
        return ResponseEntity.ok(commentService.getComments(postId));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponse> getComment(@PathVariable("postId") Long postId,
                                                      @PathVariable("commentId") Long commentId) {
        return ResponseEntity.ok(commentService.getComment(postId, commentId));
    }

    @PostMapping
    public ResponseEntity<CommentResponse> create(@PathVariable("postId") Long postId,
                                                  @RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.create(postId, request));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> update(@PathVariable("postId") Long postId,
                                                  @PathVariable("commentId") Long commentId,
                                                  @RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.update(postId, commentId, request));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable("postId") Long postId,
                                       @PathVariable("commentId") Long commentId) {
        commentService.delete(postId, commentId);
        return ResponseEntity.ok().build();
    }
}
