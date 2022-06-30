package com.prgrms.coretime.timetable.domain.repository.lectureDetail;

import static com.prgrms.coretime.timetable.domain.lectureDetail.QLectureDetail.lectureDetail;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;

@RequiredArgsConstructor
public class LectureDetailRepositoryImpl implements LectureDetailCustomRepository{
  private final JPAQueryFactory queryFactory;

  @Override
  @Modifying(clearAutomatically = true)
  public void deleteCustomLectureDetailsByLectureId(Long lectureId) {
    queryFactory
        .delete(lectureDetail)
        .where(lectureDetail.lecture.id.eq(lectureId))
        .execute();
  }

  @Override
  @Modifying(clearAutomatically = true)
  public void deleteLectureDetailsByLectureIds(List<Long> lectureIds) {
    queryFactory
        .delete(lectureDetail)
        .where(lectureDetail.lecture.id.in(lectureIds))
        .execute();
  }
}
