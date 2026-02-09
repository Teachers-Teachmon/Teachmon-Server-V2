package solvit.teachmon.domain.management.student.domain.repository.querydsl.impl;

import com.querydsl.core.BooleanBuilder;
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
            return List.of();
        }
        
        BooleanBuilder builder = new BooleanBuilder();
        
        // 이름으로 검색
        builder.or(studentEntity.name.containsIgnoreCase(keyword));
        
        // 학번으로 검색 (학년+반+번호 조합)
        try {
            Integer studentNumber = Integer.valueOf(keyword.trim());
            
            // 4자리 학번으로 검색 (예: 2203)
            if (studentNumber >= 1000 && studentNumber <= 9999) {
                Integer grade = studentNumber / 1000;
                Integer classNumber = (studentNumber % 1000) / 100;
                Integer number = studentNumber % 100;
                
                builder.or(studentEntity.grade.eq(grade)
                        .and(studentEntity.classNumber.eq(classNumber))
                        .and(studentEntity.number.eq(number)));
            }
            
            // 부분 학번으로도 검색 가능하도록
            builder.or(studentEntity.number.eq(studentNumber));
        } catch (NumberFormatException e) {
            // 숫자가 아닌 경우 이름 검색만 수행
        }
        
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
        .where(builder)
        .fetch();
    }
}