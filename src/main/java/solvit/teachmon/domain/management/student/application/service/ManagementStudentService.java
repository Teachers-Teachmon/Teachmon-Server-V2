package solvit.teachmon.domain.management.student.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.management.student.application.mapper.StudentMapper;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.management.student.exception.StudentNotFoundException;
import solvit.teachmon.domain.management.student.domain.repository.StudentRepository;
import solvit.teachmon.domain.management.student.presentation.dto.request.StudentRequest;

@Service
@RequiredArgsConstructor
public class ManagementStudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    @Transactional
    public void createStudent(StudentRequest request) {
        StudentEntity student = studentMapper.toEntity(request);
        student.setNowYear();

        studentRepository.save(student);
    }

    @Transactional
    public void updateStudent(Long studentId, StudentRequest request) {
        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(StudentNotFoundException::new);

        student.changeInfo(request.grade(), request.classNumber(), request.number(), request.name());
    }

    @Transactional
    public void deleteStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new StudentNotFoundException();
        }
        studentRepository.deleteById(studentId);
    }
}
