package solvit.teachmon.domain.self_study.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.self_study.domain.repository.AdditionalSelfStudyRepository;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("추가 자습 설정 서비스 - 삭제 테스트")
class AdditionalSelfStudyServiceDeleteTest {

    @Mock
    private AdditionalSelfStudyRepository additionalSelfStudyRepository;

    @InjectMocks
    private AdditionalSelfStudyService additionalSelfStudyService;

    @Test
    @DisplayName("추가 자습을 삭제할 수 있다")
    void shouldDeleteAdditionalSelfStudy() {
        // Given: 추가 자습 ID가 있을 때
        Long additionalId = 1L;

        // When: 추가 자습을 삭제하면
        additionalSelfStudyService.deleteAdditionalSelfStudy(additionalId);

        // Then: Repository의 deleteById가 호출된다
        verify(additionalSelfStudyRepository, times(1)).deleteById(additionalId);
    }

    @Test
    @DisplayName("다양한 ID의 추가 자습을 삭제할 수 있다")
    void shouldDeleteDifferentIds() {
        // Given: 다른 ID의 추가 자습이 있을 때
        Long additionalId = 999L;

        // When: 추가 자습을 삭제하면
        additionalSelfStudyService.deleteAdditionalSelfStudy(additionalId);

        // Then: 해당 ID로 삭제가 호출된다
        verify(additionalSelfStudyRepository, times(1)).deleteById(999L);
    }
}
