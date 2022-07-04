package com.prgrms.coretime.timetable.service;

import static com.prgrms.coretime.common.ErrorCode.ALREADY_ADDED_LECTURE;
import static com.prgrms.coretime.common.ErrorCode.INVALID_LECTURE_ADD_REQUEST;
import static com.prgrms.coretime.common.ErrorCode.LECTURE_TIME_OVERLAP;

import com.prgrms.coretime.common.error.exception.AlreadyExistsException;
import com.prgrms.coretime.common.error.exception.InvalidRequestException;
import com.prgrms.coretime.timetable.domain.EnrollmentId;
import com.prgrms.coretime.timetable.domain.LectureDetail;
import com.prgrms.coretime.timetable.domain.OfficialLecture;
import com.prgrms.coretime.timetable.domain.Timetable;
import com.prgrms.coretime.timetable.domain.repository.enrollment.EnrollmentRepository;
import com.prgrms.coretime.timetable.domain.repository.lecture.LectureRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnrollmentValidator {
  private final EnrollmentRepository enrollmentRepository;
  private final LectureRepository lectureRepository;

  public void validateOfficialLectureEnrollment(Long schoolId, OfficialLecture officialLecture, Timetable timetable) {
    if(!officialLecture.canEnrol(schoolId)) {
      throw new InvalidRequestException(INVALID_LECTURE_ADD_REQUEST);
    }

    if(!timetable.canEnrol(officialLecture.getOpenYear(), officialLecture.getSemester())) {
      throw new InvalidRequestException(INVALID_LECTURE_ADD_REQUEST);
    }

    if(enrollmentRepository.findById(new EnrollmentId(officialLecture.getId(), timetable.getId())).isPresent()) {
      throw new InvalidRequestException(ALREADY_ADDED_LECTURE);
    }
  }

  public void validateLectureTimeOverlap(Long timetableId, List<LectureDetail> lectureDetails) {
    if(lectureRepository.getNumberOfTimeOverlapLectures(timetableId, lectureDetails) > 0) {
      throw new InvalidRequestException(LECTURE_TIME_OVERLAP);
    }
  }
}
