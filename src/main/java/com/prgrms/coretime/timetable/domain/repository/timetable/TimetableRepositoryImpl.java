package com.prgrms.coretime.timetable.domain.repository.timetable;

import static com.prgrms.coretime.timetable.domain.timetable.QTimetable.timetable;
import static org.springframework.util.StringUtils.hasText;

import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.domain.timetable.Timetable;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TimetableRepositoryImpl implements TimetableCustomRepository {
  private final JPAQueryFactory queryFactory;

  @Override
  public boolean isDuplicateTimetableName(Long userId, String name, Integer year, Semester semester) {
    long numberOfDuplicateNameTable = queryFactory
        .select(timetable.count())
        .from(timetable)
        .where(
            getDuplicateNameTableCondition(userId, name, year, semester)
        )
        .fetchOne();

    return numberOfDuplicateNameTable > 0 ? true : false;
  }

  @Override
  public List<Timetable> getTimetables(Long userId, Integer year, Semester semester) {
    return queryFactory
        .selectFrom(timetable)
        .where(
            getTimetablesCondition(userId, year, semester)
        )
        .orderBy(timetable.name.asc())
        .fetch();
  }

  private BooleanBuilder getDuplicateNameTableCondition(Long userId, String name, Integer year, Semester semester) {
    BooleanBuilder duplicateNameTableCondition = new BooleanBuilder();

    duplicateNameTableCondition
        .and(userIdEq(userId))
        .and(yearEq(year))
        .and(semesterEq(semester))
        .and(nameEq(name));

    return duplicateNameTableCondition;
  }

  private BooleanBuilder getTimetablesCondition(Long userId, Integer year, Semester semester) {
    BooleanBuilder timetablesCondition = new BooleanBuilder();

    timetablesCondition
        .and(userIdEq(userId))
        .and(yearEq(year))
        .and(semesterEq(semester));

    return timetablesCondition;
  }

  private BooleanExpression userIdEq(Long userId) {
    return userId == null ? null : timetable.user.id.eq(userId);
  }

  private BooleanExpression yearEq(Integer year) {
    return year != null ? timetable.year.eq(year) : null;
  }

  private BooleanExpression semesterEq(Semester semester) {
    return semester != null ? timetable.semester.eq(semester) : null;
  }

  private BooleanExpression nameEq(String name) {
    return hasText(name) ? timetable.name.eq(name) : null;
  }
}
