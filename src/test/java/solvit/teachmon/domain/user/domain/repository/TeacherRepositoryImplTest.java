package solvit.teachmon.domain.user.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.user.domain.repository.querydsl.impl.TeacherViewQueryDslRepositoryImpl;
import solvit.teachmon.domain.user.presentation.dto.response.TeacherSearchResponseDto;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("선생님 저장소 구현체 테스트")
class TeacherRepositoryImplTest {

    @Mock
    private JPAQueryFactory queryFactory;
    
    private TeacherViewQueryDslRepositoryImpl teacherRepositoryImpl;

    @BeforeEach
    void setUp() {
        teacherRepositoryImpl = new TeacherViewQueryDslRepositoryImpl(queryFactory);
    }

    @Test
    @DisplayName("null 검색어를 처리할 수 있다")
    void shouldHandleNullQuery() {
        // When: null 검색어로 검색하면
        List<TeacherSearchResponseDto> results = teacherRepositoryImpl.performTeacherSearch(null);

        // Then: 빈 결과가 반환된다 (예외가 발생하지 않음)
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("빈 검색어를 처리할 수 있다")
    void shouldHandleEmptyQuery() {
        // When: 빈 검색어로 검색하면
        List<TeacherSearchResponseDto> results = teacherRepositoryImpl.performTeacherSearch("");

        // Then: 빈 결과가 반환된다
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("공백만 있는 검색어를 처리할 수 있다")
    void shouldHandleWhitespaceQuery() {
        // When: 공백만 있는 검색어로 검색하면
        List<TeacherSearchResponseDto> results = teacherRepositoryImpl.performTeacherSearch("   ");

        // Then: 빈 결과가 반환된다
        assertThat(results).isEmpty();
    }
}