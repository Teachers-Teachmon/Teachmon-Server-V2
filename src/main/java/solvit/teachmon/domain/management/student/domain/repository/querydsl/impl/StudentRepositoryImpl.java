package solvit.teachmon.domain.management.student.domain.repository.querydsl.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.management.student.domain.repository.querydsl.StudentQueryDslRepository;
import solvit.teachmon.domain.management.student.presentation.dto.response.QStudentSearchResponseDto;
import solvit.teachmon.domain.management.student.presentation.dto.response.StudentSearchResponseDto;

import java.util.List;

import static solvit.teachmon.domain.management.student.domain.entity.QStudentEntity.studentEntity;

@Repository
@RequiredArgsConstructor
public class StudentRepositoryImpl implements StudentQueryDslRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<StudentSearchResponseDto> searchStudentsByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return queryFactory.select(
                    new QStudentSearchResponseDto(
                            studentEntity.id,
                            studentEntity.grade,
                            studentEntity.classNumber,
                            studentEntity.number,
                            studentEntity.name
                    )
            )
            .from(studentEntity)
            .fetch();
        }
        
        String trimmedKeyword = keyword.trim();
        
        // 모든 학생을 조회하고 Java에서 필터링
        List<StudentSearchResponseDto> allStudents = queryFactory.select(
                new QStudentSearchResponseDto(
                        studentEntity.id,
                        studentEntity.grade,
                        studentEntity.classNumber,
                        studentEntity.number,
                        studentEntity.name
                )
        )
        .from(studentEntity)
        .fetch();
        
        // Java에서 검색 로직 처리
        return allStudents.stream()
                .filter(student -> matchesKeyword(student, trimmedKeyword))
                .toList();
    }
    
    private boolean matchesKeyword(StudentSearchResponseDto student, String keyword) {
        // 1. 이름 검색
        if (student.name().toLowerCase().contains(keyword.toLowerCase())) {
            return true;
        }
        
        // 2. "1-1-3" 형태 검색
        String dashFormat = student.grade() + "-" + student.classNumber() + "-" + student.number();
        if (dashFormat.toLowerCase().contains(keyword.toLowerCase())) {
            return true;
        }
        
        // 3. "113" 형태 검색 (기본)
        String basicFormat = "" + student.grade() + student.classNumber() + student.number();
        if (basicFormat.toLowerCase().contains(keyword.toLowerCase())) {
            return true;
        }
        
        // 4. "1103" 형태 검색 (zero-padding)
        String paddedFormat = "" + student.grade() + student.classNumber() + String.format("%02d", student.number());
        return paddedFormat.toLowerCase().contains(keyword.toLowerCase());
    }
}