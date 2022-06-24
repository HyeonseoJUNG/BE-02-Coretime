
package com.prgrms.coretime.timetable.service;

import com.prgrms.coretime.common.error.NotFoundException;
import com.prgrms.coretime.timetable.domain.repository.TemporaryUserRepository;
import com.prgrms.coretime.timetable.domain.repository.TimetableRepository;
import com.prgrms.coretime.timetable.domain.timetable.Timetable;
import com.prgrms.coretime.timetable.dto.request.TimetableCreateRequest;
import com.prgrms.coretime.user.domain.User;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class TimetableService {
  private final TimetableRepository timetableRepository;

  // TODO : 머지 후 TemporaryUserRepository -> UserRepository로 교체
  private final TemporaryUserRepository userRepository;

  @Transactional
  public void createTimetable(@RequestBody @Valid TimetableCreateRequest timetableCreateRequest) {
    // TODO : 사용자 ID 가져오는 로직 추가
    Long userId = 1L;
    User user  = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다!"));

    Timetable newTimetable = Timetable.builder()
        .name(timetableCreateRequest.getName())
        .year(timetableCreateRequest.getYear())
        .semester(timetableCreateRequest.getSemester())
        .build();
    newTimetable.setUser(user);

    timetableRepository.save(newTimetable);
  }

  // 조회

  // 이름 수정

  // 삭제
}
