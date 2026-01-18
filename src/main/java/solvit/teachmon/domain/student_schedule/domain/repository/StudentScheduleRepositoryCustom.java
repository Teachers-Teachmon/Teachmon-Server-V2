package solvit.teachmon.domain.student_schedule.domain.repository;

import solvit.teachmon.domain.student_schedule.application.dto.StudentScheduleDto;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StudentScheduleRepositoryCustom {
    Map<Integer, List<StudentScheduleDto>> findByGradeAndPeriodGroupByClass(Integer grade, LocalDate day, SchoolPeriod period);
}
