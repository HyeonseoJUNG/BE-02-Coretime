package com.prgrms.coretime.message.controller;

import com.prgrms.coretime.common.ApiResponse;
import com.prgrms.coretime.common.jwt.JwtPrincipal;
import com.prgrms.coretime.message.dto.request.MessageRoomCreateRequest;
import com.prgrms.coretime.message.dto.request.MessageRoomGetRequest;
import com.prgrms.coretime.message.dto.response.MessageRoomIdResponse;
import com.prgrms.coretime.message.dto.response.MessageRoomListResponse;
import com.prgrms.coretime.message.dto.response.MessageRoomResponse;
import com.prgrms.coretime.message.service.MessageRoomService;
import io.swagger.annotations.ApiOperation;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("/api/v1/message-rooms")
@RequiredArgsConstructor
public class MessageRoomController {

  private final MessageRoomService messageRoomService;

  @ApiOperation(value = "쪽지방 생성하기", notes = "쪽지방을 생성하는 요청입니다.")
  @PostMapping
  public ResponseEntity<ApiResponse> createMessageRoom(
      @AuthenticationPrincipal JwtPrincipal principal,
      @Valid @RequestBody final MessageRoomCreateRequest request,
      RedirectAttributes redirectAttributes) throws URISyntaxException {

    Optional<Long> maybeMessageRoomId = messageRoomService.getMessageRoomId(principal.userId,
        request.getCreatedFrom(), request.getReceiverId(), request.getIsAnonymous());
    if (maybeMessageRoomId.isPresent()) {
      URI redirectUri = new URI(
          new StringBuilder().append("/api/v1/message-rooms/").append(maybeMessageRoomId.get())
              .append("/redirect-message?userId=").append(principal.userId).toString()
      );

      redirectAttributes.addFlashAttribute("message", request.getFirstMessage());
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setLocation(redirectUri);

      return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
          .headers(httpHeaders)
          .body(new ApiResponse<>("이미 존재하는 쪽지방입니다. 쪽지방 생성 대신 쪽지 전송 요청으로 리디렉트되었습니다."));
    }

    MessageRoomIdResponse response = messageRoomService.saveMessageRoom(principal.userId, request);

    URI redirectUri = new URI(
        new StringBuilder().append("/api/v1/message-rooms/").append(response.getMessageRoomId())
            .append("?userId=").append(principal.userId).toString());
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(redirectUri);
    return ResponseEntity.status(HttpStatus.FOUND)
        .headers(httpHeaders)
        .body(new ApiResponse<>("쪽지방 생성이 완료되었습니다."));
  }

  @ApiOperation(value = "쪽지방 조회하기", notes = "쪽지방 정보와 최근 쪽지를 조회하는 요청입니다.")
  @GetMapping("/{messageRoomId}")
  public ResponseEntity<ApiResponse> getMessageRoom(@AuthenticationPrincipal JwtPrincipal principal,
      @PathVariable("messageRoomId") Long messageRoomId) {

    MessageRoomGetRequest request = new MessageRoomGetRequest(messageRoomId);
    MessageRoomResponse response = messageRoomService.getMessageRoom(principal.userId, request);
    return ResponseEntity.ok().body(new ApiResponse<>("쪽지방 조회가 완료되었습니다.", response));
  }

  @ApiOperation(value = "쪽지방 리스트 조회하기", notes = "쪽지방 리스트를 조회하는 요청입니다.")
  @GetMapping
  public ResponseEntity<ApiResponse> getMessageRooms(
      @AuthenticationPrincipal JwtPrincipal principal,
      @PageableDefault(size = 20, sort = "updated_at", direction = Sort.Direction.DESC) final Pageable pageable) {

    Page<MessageRoomListResponse> response = messageRoomService.getMessageRooms(principal.userId,
        pageable);
    return ResponseEntity.ok().body(new ApiResponse<>("쪽지방 리스트 조회가 완료되었습니다.", response));
  }

  @ApiOperation(value = "쪽지방 차단하기", notes = "쪽지방을 차단하는 요청입니다.")
  @PatchMapping("/{messageRoomId}/block")
  public ResponseEntity<ApiResponse> blockMessageRoom(
      @AuthenticationPrincipal JwtPrincipal principal,
      @PathVariable("messageRoomId") Long messageRoomId) {

    messageRoomService.blockMessageRoom(principal.userId, messageRoomId);
    return ResponseEntity.ok().body(new ApiResponse<>("쪽지방 차단이 완료되었습니다."));
  }

  @ApiOperation(value = "쪽지방 삭제하기", notes = "쪽지방을 삭제하는 요청입니다.")
  @PatchMapping("/{messageRoomId}/delete")
  public ResponseEntity<ApiResponse> deleteMessageRoom(
      @AuthenticationPrincipal JwtPrincipal principal,
      @PathVariable("messageRoomId") Long messageRoomId) {

    messageRoomService.deleteMessageRoom(principal.userId, messageRoomId);
    return ResponseEntity.ok().body(new ApiResponse<>("쪽지방 삭제가 완료되었습니다."));
  }
}
