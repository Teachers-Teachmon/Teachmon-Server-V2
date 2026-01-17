package solvit.teachmon.domain.management.student.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.management.student.domain.repository.StudentRepository;
import solvit.teachmon.domain.management.student.presentation.dto.request.StudentRequest;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ManagementStudentService {
    private final StudentRepository studentRepository;

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

    @Transactional
    public void updateStudent(Long studentId, StudentRequest request) {
        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 학생을 찾을 수 없습니다."));

        student.changeInfo(request.grade(), request.classNumber(), request.number(), request.name());
    }

    @Transactional
    public void deleteStudent(Long studentId) {
        studentRepository.deleteById(studentId);
    }
}
