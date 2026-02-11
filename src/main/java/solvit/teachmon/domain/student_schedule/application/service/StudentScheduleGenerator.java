package solvit.teachmon.domain.student_schedule.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.management.student.domain.repository.StudentRepository;
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
public class StudentScheduleGenerator {
    private final StudentScheduleRepository studentScheduleRepository;
    private final StudentRepository studentRepository;

    /**
     * 해당 학년, 날짜, 교시의 StudentSchedule을 찾거나 없으면 생성합니다.
     * 추가 자습은 1~11교시 모두 가능하므로, StudentSchedule이 없을 수 있습니다.
     *
     * @param grade 학년
     * @param day 날짜
     * @param period 교시
     * @return 조회되거나 생성된 StudentSchedule 리스트
     */
    public List<StudentScheduleEntity> findOrCreateStudentSchedules(Integer grade, LocalDate day, SchoolPeriod period) {
        // 기존 StudentSchedule 조회
        List<StudentScheduleEntity> existingSchedules = studentScheduleRepository.findAllByGradeAndDayAndPeriod(
                grade, day, period
        );

        // 이미 존재하면 그대로 반환
        if (!existingSchedules.isEmpty()) {
            return existingSchedules;
        }

        // 없으면 해당 학년 학생들의 StudentSchedule 생성
        Integer year = day.getYear();
        List<StudentEntity> students = studentRepository.findByYear(year)
                .stream()
                .filter(student -> student.getGrade().equals(grade))
                .toList();

        List<StudentScheduleEntity> newSchedules = students.stream()
                .map(student -> StudentScheduleEntity.builder()
                        .student(student)
                        .day(day)
                        .period(period)
                        .build())
                .toList();

        return studentScheduleRepository.saveAll(newSchedules);
    }

    public void createStudentScheduleByStudents(List<StudentEntity> students, LocalDate baseDate) {
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

    public void deleteFutureStudentSchedules(LocalDate baseDate) {
        LocalDate endDay = baseDate.with(DayOfWeek.SUNDAY);

        List<StudentScheduleEntity> oldSchedules = studentScheduleRepository.findAllByDayBetween(baseDate, endDay);
        studentScheduleRepository.deleteAll(oldSchedules);
    }
}
