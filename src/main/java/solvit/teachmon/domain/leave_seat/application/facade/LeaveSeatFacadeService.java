package solvit.teachmon.domain.leave_seat.application.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.leave_seat.domain.entity.LeaveSeatEntity;
import solvit.teachmon.domain.leave_seat.domain.entity.LeaveSeatStudentEntity;
import solvit.teachmon.domain.leave_seat.domain.repository.LeaveSeatRepository;
import solvit.teachmon.domain.leave_seat.domain.repository.LeaveSeatStudentRepository;
import solvit.teachmon.domain.leave_seat.presentation.dto.request.LeaveSeatCreateRequest;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.management.student.domain.repository.StudentRepository;
import solvit.teachmon.domain.management.student.exception.StudentNotFoundException;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.place.domain.repository.PlaceRepository;
import solvit.teachmon.domain.place.exception.PlaceNotFoundException;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.LeaveSeatScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.domain.repository.ScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.StudentScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.schedules.LeaveSeatScheduleRepository;
import solvit.teachmon.domain.student_schedule.exception.StudentScheduleNotFoundException;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveSeatFacadeService {
    private final PlaceRepository placeRepository;
    private final LeaveSeatRepository leaveSeatRepository;
    private final ScheduleRepository scheduleRepository;
    private final StudentScheduleRepository studentScheduleRepository;
    private final LeaveSeatScheduleRepository leaveSeatScheduleRepository;
    private final LeaveSeatStudentRepository leaveSeatStudentRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public void createLeaveSeat(LeaveSeatCreateRequest request, TeacherEntity teacher) {
        PlaceEntity place = placeRepository.findById(request.placeId())
                .orElseThrow(PlaceNotFoundException::new);

        List<StudentEntity> students = getStudents(request.students());

        // 기존 leaveSeat 이 있으면 가져오고, 없으면 새로 생성
        LeaveSeatEntity leaveSeat = leaveSeatRepository.findByPlaceAndDayAndPeriod(place, request.day(), request.period())
                .orElseGet(() -> saveLeaveSeat(request, teacher, place));

        saveLeaveSeatStudent(leaveSeat, students);
        List<StudentScheduleEntity> studentSchedules = getStudentSchedules(students, request.day(), request.period());
        saveLeaveSeatSchedules(studentSchedules, leaveSeat);
    }

    private List<StudentEntity> getStudents(List<Long> studentIds) {
        List<StudentEntity> students = studentRepository.findAllById(studentIds);
        if (students.size() != studentIds.size()) {
            throw new StudentNotFoundException();
        }
        return students;
    }

    private List<StudentScheduleEntity> getStudentSchedules(List<StudentEntity> students, LocalDate day, SchoolPeriod period) {
        List<StudentScheduleEntity> studentSchedules = studentScheduleRepository.findAllByStudentsAndDayAndPeriod(students, day, period);
        if (studentSchedules.size() != students.size()) {
            throw new StudentScheduleNotFoundException();
        }
        return studentSchedules;
    }

    private void saveLeaveSeatStudent(LeaveSeatEntity leaveSeat, List<StudentEntity> students) {
        List<LeaveSeatStudentEntity> leaveSeatStudents = students.stream()
                .map(student -> LeaveSeatStudentEntity.builder()
                        .leaveSeat(leaveSeat)
                        .student(student)
                        .build())
                .toList();

        leaveSeatStudentRepository.saveAll(leaveSeatStudents);
    }

    private LeaveSeatEntity saveLeaveSeat(LeaveSeatCreateRequest request, TeacherEntity teacher, PlaceEntity place) {
        LeaveSeatEntity leaveSeat = LeaveSeatEntity.builder()
                .place(place)
                .teacher(teacher)
                .day(request.day())
                .period(request.period())
                .cause(request.cause())
                .build();

        return leaveSeatRepository.save(leaveSeat);
    }

    private void saveLeaveSeatSchedules(List<StudentScheduleEntity> studentSchedules, LeaveSeatEntity leaveSeat) {
        List<ScheduleEntity> newSchedules = studentSchedules.stream()
                .map(studentSchedule -> {
                    Integer lastStackOrder = scheduleRepository.findLastStackOrderByStudentScheduleId(studentSchedule.getId());
                    return ScheduleEntity.createNewStudentSchedule(studentSchedule, lastStackOrder, ScheduleType.LEAVE_SEAT);
                })
                .toList();
        scheduleRepository.saveAll(newSchedules);

        List<LeaveSeatScheduleEntity> leaveSeatSchedules = newSchedules.stream()
                .map(schedule -> LeaveSeatScheduleEntity.builder()
                        .schedule(schedule)
                        .leaveSeat(leaveSeat)
                        .build())
                .toList();

        leaveSeatScheduleRepository.saveAll(leaveSeatSchedules);
    }
}
