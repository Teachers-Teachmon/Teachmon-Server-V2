package solvit.teachmon.domain.leave_seat.application.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.leave_seat.domain.entity.FixedLeaveSeatEntity;
import solvit.teachmon.domain.leave_seat.domain.entity.FixedLeaveSeatStudentEntity;
import solvit.teachmon.domain.leave_seat.domain.repository.FixedLeaveSeatRepository;
import solvit.teachmon.domain.leave_seat.domain.repository.FixedLeaveSeatStudentRepository;
import solvit.teachmon.domain.leave_seat.presentation.dto.request.FixedLeaveSeatCreateRequest;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.management.student.domain.repository.StudentRepository;
import solvit.teachmon.domain.management.student.exception.StudentNotFoundException;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.place.domain.repository.PlaceRepository;
import solvit.teachmon.domain.place.exception.PlaceNotFoundException;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FixedLeaveSeatFacadeService {
    private final PlaceRepository placeRepository;
    private final FixedLeaveSeatRepository fixedLeaveSeatRepository;
    private final FixedLeaveSeatStudentRepository fixedLeaveSeatStudentRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public void createStaticLeaveSeat(FixedLeaveSeatCreateRequest request, TeacherEntity teacher) {
        PlaceEntity place = placeRepository.findById(request.placeId())
                .orElseThrow(PlaceNotFoundException::new);

        List<StudentEntity> students = getStudents(request.students());

        // 고정 이석 저장
        FixedLeaveSeatEntity fixedLeaveSeat = saveFixedLeaveSeat(request, teacher, place);

        // 고정 이석 학생들 저장
        saveFixedLeaveSeatStudent(fixedLeaveSeat, students);
    }

    private void saveFixedLeaveSeatStudent(FixedLeaveSeatEntity fixedLeaveSeat, List<StudentEntity> students) {
        List<FixedLeaveSeatStudentEntity> leaveSeatStudents = students.stream()
                .map(student -> FixedLeaveSeatStudentEntity.builder()
                        .fixedLeaveSeat(fixedLeaveSeat)
                        .student(student)
                        .build())
                .toList();

        fixedLeaveSeatStudentRepository.saveAll(leaveSeatStudents);
    }

    private FixedLeaveSeatEntity saveFixedLeaveSeat(FixedLeaveSeatCreateRequest request, TeacherEntity teacher, PlaceEntity place) {
        FixedLeaveSeatEntity fixedLeaveSeat = FixedLeaveSeatEntity.builder()
                .teacher(teacher)
                .place(place)
                .weekDay(request.weekDay())
                .period(request.period())
                .cause(request.cause())
                .build();

        return fixedLeaveSeatRepository.save(fixedLeaveSeat);
    }

    private List<StudentEntity> getStudents(List<Long> studentIds) {
        List<StudentEntity> students = studentRepository.findAllById(studentIds);
        if (students.size() != studentIds.size()) {
            throw new StudentNotFoundException();
        }
        return students;
    }
}
