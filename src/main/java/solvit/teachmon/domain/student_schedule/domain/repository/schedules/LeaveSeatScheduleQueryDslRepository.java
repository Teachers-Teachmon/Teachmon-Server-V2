package solvit.teachmon.domain.student_schedule.domain.repository.schedules;

import solvit.teachmon.domain.student_schedule.application.dto.PlaceScheduleDto;
import solvit.teachmon.domain.student_schedule.application.dto.StudentScheduleDto;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface LeaveSeatScheduleQueryDslRepository {
    Map<Integer, Long> getLeaveSeatPlaceCount(List<ScheduleEntity> schedules);
    List<PlaceScheduleDto> getPlaceScheduleByFloor(List<ScheduleEntity> schedules, Integer floor);
    List<StudentScheduleDto> getStudentScheduleByPlaceAndDayAndPeriod(Long placeId, LocalDate day, SchoolPeriod period);
}
