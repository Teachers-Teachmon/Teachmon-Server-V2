package solvit.teachmon.domain.management.student.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import solvit.teachmon.domain.management.student.domain.repository.StudentRepository;
import solvit.teachmon.domain.management.student.presentation.dto.response.StudentSearchResponseDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchStudentService {
    private final StudentRepository studentRepository;

    public List<StudentSearchResponseDto> searchStudentByQuery(String query) {
        return studentRepository.searchStudentsByKeyword(query);
    }
}
