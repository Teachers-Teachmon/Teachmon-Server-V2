package solvit.teachmon.domain.management.student.domain.repository.querydsl;

import solvit.teachmon.domain.management.student.presentation.dto.response.StudentSearchResponseDto;

import java.util.List;

public interface StudentQueryDslRepository {
    List<StudentSearchResponseDto> searchStudentsByKeyword(String keyword);
}