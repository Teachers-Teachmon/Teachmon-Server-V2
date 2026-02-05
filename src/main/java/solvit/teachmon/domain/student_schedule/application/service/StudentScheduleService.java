package solvit.teachmon.domain.student_schedule.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.student_schedule.application.dto.PeriodScheduleDto;
import solvit.teachmon.domain.student_schedule.application.dto.StudentScheduleDto;
import solvit.teachmon.domain.student_schedule.application.mapper.StudentScheduleMapper;
import solvit.teachmon.domain.student_schedule.application.strategy.StudentScheduleChangeStrategy;
import solvit.teachmon.domain.student_schedule.application.strategy.StudentScheduleChangeStrategyComposite;
import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.repository.StudentScheduleRepository;
import solvit.teachmon.domain.student_schedule.exception.StudentScheduleNotFoundException;
import solvit.teachmon.domain.student_schedule.presentation.dto.request.StudentScheduleCancelRequest;
import solvit.teachmon.domain.student_schedule.presentation.dto.request.StudentScheduleUpdateRequest;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.ClassStudentScheduleResponse;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.HistoryStudentScheduleResponse;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudentScheduleService {
    private final StudentScheduleMapper studentScheduleMapper;
    private final StudentScheduleRepository studentScheduleRepository;
    private final StudentScheduleChangeStrategyComposite studentScheduleChangeStrategyComposite;

    @Transactional(readOnly = true)
    public List<ClassStudentScheduleResponse> getGradeStudentSchedules(Integer grade, LocalDate day, SchoolPeriod period) {
        Map<Integer, List<StudentScheduleDto>> classStudentSchedule = studentScheduleRepository.findByGradeAndPeriodGroupByClass(grade, day, period);

        return classStudentSchedule.entrySet().stream()
                .map(entry -> studentScheduleMapper.toResponse(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Transactional
    public void updateStudentSchedule(Long scheduleId, StudentScheduleUpdateRequest request, TeacherEntity teacher) {
        StudentScheduleEntity studentSchedule = studentScheduleRepository.findById(scheduleId)
                .orElseThrow(StudentScheduleNotFoundException::new);

        StudentScheduleChangeStrategy scheduleChanger = studentScheduleChangeStrategyComposite.getStrategy(request.state());
        scheduleChanger.change(studentSchedule, teacher);
    }

    @Transactional
    public void cancelStudentSchedule(Long scheduleId, StudentScheduleCancelRequest request) {
        StudentScheduleEntity studentSchedule = studentScheduleRepository.findById(scheduleId)
                .orElseThrow(StudentScheduleNotFoundException::new);

        StudentScheduleChangeStrategy scheduleChanger = studentScheduleChangeStrategyComposite.getStrategy(request.state());
        scheduleChanger.cancel(studentSchedule);
    }

    @Transactional(readOnly = true)
    public List<HistoryStudentScheduleResponse> getStudentScheduleHistory(String query, LocalDate day) {
        Map<StudentEntity, List<PeriodScheduleDto>> studentSchedules = studentScheduleRepository.findByQueryAndDayGroupByStudent(query, day);

        return studentSchedules.entrySet().stream()
                .map(entry -> studentScheduleMapper.toHistoryResponse(entry.getKey(), entry.getValue()))
                .toList();
    }
}
