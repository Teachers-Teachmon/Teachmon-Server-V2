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

        // baseDate 이후(포함) 스케줄 삭제 (과거 데이터는 보존)
        deleteFutureStudentSchedules(baseDate);

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

            // baseDate 이후(포함) 날짜만 생성
            if (day.isBefore(baseDate)) {
                continue;
            }

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

    private void deleteFutureStudentSchedules(LocalDate baseDate) {
        LocalDate endDay = baseDate.with(DayOfWeek.SUNDAY);

        List<StudentScheduleEntity> oldSchedules = studentScheduleRepository.findAllByDayBetween(baseDate, endDay);
        studentScheduleRepository.deleteAll(oldSchedules);
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
