package solvit.teachmon.domain.management.student.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.management.student.domain.exception.StudentNotFoundException;
import solvit.teachmon.domain.management.student.domain.repository.StudentRepository;
import solvit.teachmon.domain.management.student.presentation.dto.request.StudentRequest;
import solvit.teachmon.global.annotation.Trace;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ManagementStudentService {
    private final StudentRepository studentRepository;

    @Trace
    @Transactional
    public void createStudent(StudentRequest request) {
        StudentEntity student = StudentEntity.builder()
                .year(LocalDate.now().getYear())
                .grade(request.grade())
                .classNumber(request.classNumber())
                .number(request.number())
                .name(request.name())
                .build();

        studentRepository.save(student);
    }

    @Trace
    @Transactional
    public void updateStudent(Long studentId, StudentRequest request) {
        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(StudentNotFoundException::new);

        student.changeInfo(request.grade(), request.classNumber(), request.number(), request.name());
    }

    @Trace
    @Transactional
    public void deleteStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new StudentNotFoundException();
        }
        studentRepository.deleteById(studentId);
    }
}
