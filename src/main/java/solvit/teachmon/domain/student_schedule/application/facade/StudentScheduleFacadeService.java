package solvit.teachmon.domain.student_schedule.application.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.student_schedule.application.dto.StudentScheduleDto;
import solvit.teachmon.domain.student_schedule.application.mapper.StudentScheduleMapper;
import solvit.teachmon.domain.student_schedule.domain.repository.StudentScheduleRepository;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.ClassStudentScheduleResponse;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudentScheduleFacadeService {
    private final StudentScheduleMapper studentScheduleMapper;
    private final StudentScheduleRepository studentScheduleRepository;

    @Transactional(readOnly = true)
    public List<ClassStudentScheduleResponse> getGradeStudentSchedules(Integer grade, LocalDate day, SchoolPeriod period) {
        Map<Integer, List<StudentScheduleDto>> classStudentSchedule = studentScheduleRepository.findByGradeAndPeriodGroupByClass(grade, day, period);

        return studentScheduleMapper.toResponse(classStudentSchedule);
    }
}
