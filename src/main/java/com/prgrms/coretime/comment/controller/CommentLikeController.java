package com.prgrms.coretime.comment.controller;

import com.prgrms.coretime.comment.service.CommentLikeService;
import com.prgrms.coretime.common.ApiResponse;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentLikeController {

  private final CommentLikeService commentLikeService;

  @PostMapping("/{commentId}/like")
  public ResponseEntity<ApiResponse<Void>> createLike(Long userId,
      @PathVariable Long commentId
  )
      throws URISyntaxException {
    commentLikeService.createLike(userId, commentId);
    URI location = new URI("/api/v1/comments");
    return ResponseEntity.created(location).body(new ApiResponse("댓글 좋아요 생성"));
  }

  @DeleteMapping("/{commentId}/like")
  public ResponseEntity<ApiResponse<Void>> deleteLike(Long userId, @PathVariable Long commentId)
      throws URISyntaxException {
    commentLikeService.deleteLike(userId, commentId);
    URI location = new URI("/api/v1/comments");
    return ResponseEntity.created(location).body(new ApiResponse<>("댓글 삭제 생성"));
  }

}
