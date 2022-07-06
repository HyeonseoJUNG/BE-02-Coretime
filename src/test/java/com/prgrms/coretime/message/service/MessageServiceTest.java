package com.prgrms.coretime.message.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.prgrms.coretime.message.domain.MessageRepository;
import com.prgrms.coretime.message.domain.MessageRoom;
import com.prgrms.coretime.message.domain.MessageRoomRepository;
import com.prgrms.coretime.message.dto.request.MessageSendRequest;
import com.prgrms.coretime.user.domain.TestUser;
import com.prgrms.coretime.user.domain.TestUserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

  @InjectMocks
  private MessageService messageService;

  @Mock
  private MessageRepository messageRepository;

  @Mock
  private MessageRoomRepository messageRoomRepository;

  @Mock
  private TestUserRepository testUserRepository;

  private TestUser user1 = mock(TestUser.class);
  private TestUser user2 = mock(TestUser.class);

  private MessageRoom messageRoom = mock(MessageRoom.class);


  @Test
  @DisplayName("쪽지 전송(생성): 성공")
  void sendMessageSuccessTest() {
    MessageSendRequest request = new MessageSendRequest("This is the message.");
    doReturn(Optional.of(user1)).when(testUserRepository).findById(any());
    doReturn(Optional.of(messageRoom)).when(messageRoomRepository).findById(any());
    when(messageRoom.getInitialReceiver()).thenReturn(user1);
    when(messageRoom.getInitialSender()).thenReturn(user2);

    messageService.sendMessage(user1.getId(), messageRoom.getId(), request);

    verify(messageRepository).save(any());
  }

}