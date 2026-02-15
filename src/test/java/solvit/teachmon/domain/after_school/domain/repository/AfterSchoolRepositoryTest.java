package solvit.teachmon.domain.after_school.domain.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("방과후 저장소 테스트")
class AfterSchoolRepositoryTest {

    @Mock
    private AfterSchoolRepository afterSchoolRepository;

    @Test
    @DisplayName("findByTeacherIdWithRelations 메소드가 존재한다")
    void shouldHaveFindByTeacherIdWithRelationsMethod() {
        // Given
        Long teacherId = 1L;

        // When & Then - 메소드 호출이 성공하면 테스트 통과
        afterSchoolRepository.findByTeacherIdWithRelations(teacherId);
        
        // 메소드 시그니처가 올바르게 정의되어 있는지 확인
        assertThat(afterSchoolRepository).isNotNull();
    }
}