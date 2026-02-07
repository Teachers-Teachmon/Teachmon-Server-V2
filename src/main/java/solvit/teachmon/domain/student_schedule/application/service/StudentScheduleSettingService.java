package solvit.teachmon.domain.student_schedule.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.management.student.domain.repository.StudentRepository;
import solvit.teachmon.domain.student_schedule.application.strategy.setting.StudentScheduleSettingStrategy;
import solvit.teachmon.domain.student_schedule.application.strategy.setting.StudentScheduleSettingStrategyComposite;
import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.repository.StudentScheduleRepository;
import solvit.teachmon.global.enums.SchoolPeriod;
import solvit.teachmon.global.enums.WeekDay;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentScheduleSettingService {
    private final StudentScheduleSettingStrategyComposite studentScheduleSettingStrategyComposite;
    private final StudentRepository studentRepository;
    private final StudentScheduleRepository studentScheduleRepository;

    @Transactional
    public void createNewStudentSchedule(LocalDate baseDate) {
        List<StudentEntity> students = getNowStudents(baseDate);

        // 과거 스케줄 삭제
        deleteOldStudentSchedules(baseDate);

        // 새로운 학생 스케줄 생성
        List<StudentScheduleEntity> studentSchedules = new ArrayList<>();
        for(StudentEntity student : students) {
            List<StudentScheduleEntity> weekStudentSchedules = getWeekStudentSchedules(student, baseDate);
            studentSchedules.addAll(weekStudentSchedules);
        }

        studentScheduleRepository.saveAll(studentSchedules);
    }

    private List<StudentScheduleEntity> getWeekStudentSchedules(StudentEntity student, LocalDate baseDate) {
        List<StudentScheduleEntity> studentSchedules = new ArrayList<>();
        for(WeekDay weekDay : WeekDay.values()) {
            LocalDate day = baseDate.with(weekDay.toDayOfWeek());

            for(SchoolPeriod period : SchoolPeriod.getAfterActivityPeriod()) {
                studentSchedules.add(
                        StudentScheduleEntity.builder()
                            .student(student)
                            .day(day)
                            .period(period)
                            .build()
                );
            }
        }

        return studentSchedules;
    }

    private void deleteOldStudentSchedules(LocalDate baseDate) {
        LocalDate startDay = baseDate.with(DayOfWeek.MONDAY);
        LocalDate endDay = baseDate.with(DayOfWeek.SUNDAY);

        studentScheduleRepository.deleteAllByDayBetween(startDay, endDay);
    }

    private List<StudentEntity> getNowStudents(LocalDate baseDate) {
        Integer nowYear = baseDate.getYear();
        return studentRepository.findByYear(nowYear);
    }

    @Transactional
    public void settingAllTypeSchedule(LocalDate baseDate) {
        List<StudentScheduleSettingStrategy> settingStrategies = studentScheduleSettingStrategyComposite.getAllStrategies();

        for(StudentScheduleSettingStrategy settingStrategy : settingStrategies) {
            settingStrategy.settingSchedule(baseDate);
        }
    }
}
