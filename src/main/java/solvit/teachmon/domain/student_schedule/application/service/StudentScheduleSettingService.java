package solvit.teachmon.domain.student_schedule.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.management.student.domain.repository.StudentRepository;
import solvit.teachmon.domain.student_schedule.application.strategy.setting.StudentScheduleSettingStrategy;
import solvit.teachmon.domain.student_schedule.application.strategy.setting.StudentScheduleSettingStrategyComposite;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentScheduleSettingService {
    private final StudentScheduleSettingStrategyComposite studentScheduleSettingStrategyComposite;
    private final StudentRepository studentRepository;
    private final StudentScheduleGenerator studentScheduleGenerator;

    @Transactional
    public void createNewStudentSchedule(LocalDate baseDate) {
        List<StudentEntity> students = getNowStudents(baseDate);

        // baseDate 이후(포함) 스케줄 삭제 (과거 데이터는 보존)
        studentScheduleGenerator.deleteFutureStudentSchedules(baseDate);

        // 새로운 학생 스케줄 생성
        studentScheduleGenerator.createStudentScheduleByStudents(students, baseDate);
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
