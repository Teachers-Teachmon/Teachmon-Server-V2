package solvit.teachmon.domain.supervision.domain.repository;

import solvit.teachmon.domain.management.teacher.presentation.dto.response.TeacherListResponse;
import solvit.teachmon.domain.supervision.domain.entity.SupervisionScheduleEntity;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionType;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionSortOrder;
import solvit.teachmon.domain.supervision.presentation.dto.response.SupervisionRankResponseDto;
import solvit.teachmon.domain.supervision.presentation.dto.response.SupervisionScheduleResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface SupervisionScheduleQueryDslRepository {
    List<TeacherListResponse> countTeacherSupervision(String query);
    List<SupervisionScheduleEntity> findByMonthAndQuery(Integer month, String query);
    List<SupervisionScheduleResponseDto> findSchedulesGroupedByDayAndQuery(Integer month, String query);
    List<LocalDate> findSupervisionDaysByTeacherAndMonth(Long teacherId, Integer month);
    List<SupervisionType> findTodaySupervisionTypesByTeacher(Long teacherId, LocalDate today);
    List<SupervisionRankResponseDto> findSupervisionRankings(String query, SupervisionSortOrder sortOrder);
}