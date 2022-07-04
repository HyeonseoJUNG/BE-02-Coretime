package com.prgrms.coretime.post.dto.response;

import com.prgrms.coretime.comment.domain.Comment;
import com.prgrms.coretime.post.domain.Post;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Page;

public record PostResponse(Long postId, BoardSimpleResponse board,
                           Long userId, String nickname, String title,
                           String content, Boolean isAnonymous,
                           List<PhotoResponse> photos,
                           List<CommentResponse> comments,
                           Integer likeCount) {

  @Builder
  public PostResponse {
  }

  public PostResponse(Post entity, Page<Comment> comments) {
    this(
        entity.getId(),
        new BoardSimpleResponse(entity.getBoard()),
        entity.getUser().getId(),
        entity.getIsAnonymous() ? "익명" : entity.getUser().getNickname(),
        entity.getTitle(),
        entity.getContent(),
        entity.getIsAnonymous(),
        entity.getPhotos().stream().map(PhotoResponse::new).toList(),
        comments.getContent().stream().map(CommentResponse::new).toList(),
        entity.getLikeCount()
    );
  }
}
