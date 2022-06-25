package com.prgrms.coretime.timetable.controller;

import com.prgrms.coretime.common.ApiResponse;
import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.dto.request.TimetableCreateRequest;
import com.prgrms.coretime.timetable.dto.response.TimetablesResponse;
import com.prgrms.coretime.timetable.service.TimetableService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.net.URI;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"timetables"})
@RestController
@RequestMapping("/api/v1/timetables")
@RequiredArgsConstructor
public class TimetableController {
  private final TimetableService timetableService;

  @ApiOperation(value = "시간표 생성", notes = "시간표를 생성합니다.")
  @PostMapping
  public ResponseEntity<ApiResponse> createTimetable(@RequestBody @Valid TimetableCreateRequest timetableCreateRequest) {
    Long createTimetableId = timetableService.createTimetable(timetableCreateRequest);

    ApiResponse apiResponse = new ApiResponse("시간표 생성 완료");

    return ResponseEntity
        .created(URI.create("/timetables/" + createTimetableId))
        .body(apiResponse);
  }

  @ApiOperation(value = "시간표 목록 조회", notes = "연도와 학기에 따른 시간표 목록을 조회합니다.")
  @GetMapping
  public ResponseEntity<ApiResponse> getTimetables(@RequestParam Integer year, @RequestParam Semester semester) {
    TimetablesResponse timetablesResponse = timetableService.getTimetables(year, semester);

    ApiResponse apiResponse = new ApiResponse("시간표 목록 조회 완료", timetablesResponse);

    return ResponseEntity
        .ok()
        .body(apiResponse);
  }
}
