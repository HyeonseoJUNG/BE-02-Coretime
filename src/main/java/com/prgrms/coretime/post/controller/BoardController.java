package com.prgrms.coretime.post.controller;

import com.prgrms.coretime.post.dto.request.PostCreateRequest;
import com.prgrms.coretime.post.dto.response.PostIdResponse;
import com.prgrms.coretime.post.dto.response.PostSimpleResponse;
import com.prgrms.coretime.post.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/boards")
@RestController
public class BoardController {
    private final PostService postService;

    public BoardController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{boardId}/posts")
    public Page<PostSimpleResponse> showPostsByBoard(
            @PathVariable(name = "boardId") Long boardId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @PageableDefault(
                    sort = {"created_at"},
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return postService.getPostsByBoard(boardId, keyword, pageable);
    }

    @PostMapping("/{boardId}/posts")
    public PostIdResponse createPost(
            @PathVariable(name = "boardId") Long boardId,
            Long userId,
            @RequestBody @Validated PostCreateRequest request
    ) {
        return postService.createPost(boardId, userId, request);
    }
}
