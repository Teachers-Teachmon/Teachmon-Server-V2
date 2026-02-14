package solvit.teachmon.domain.student_schedule.domain.repository;

import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.student_schedule.application.dto.PeriodScheduleDto;
import solvit.teachmon.domain.student_schedule.application.dto.StudentScheduleDto;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StudentScheduleQueryDslRepository {
    Map<Integer, List<StudentScheduleDto>> findByGradeAndPeriodGroupByClass(Integer grade, LocalDate day, SchoolPeriod period);
    Map<StudentEntity, List<PeriodScheduleDto>> findByQueryAndDayGroupByStudent(String query, LocalDate day);
    Map<ScheduleType, List<ScheduleEntity>> findAllByDayAndPeriodAndTypeIn(LocalDate day, SchoolPeriod period, List<ScheduleType> types);
    Map<Long, ScheduleType> findLastScheduleTypeByStudentsAndDayAndPeriod(List<StudentEntity> students, LocalDate day, SchoolPeriod period);

    /**
     * 장소 기반 스케줄 조회 (EXIT/AWAY 특별 처리)
     * EXIT/AWAY인 경우 stackOrder-1의 장소 기반 스케줄도 함께 반환
     */
    Map<ScheduleType, List<ScheduleEntity>> findPlaceBasedSchedulesByDayAndPeriodAndTypeIn(LocalDate day, SchoolPeriod period, List<ScheduleType> types);
}
