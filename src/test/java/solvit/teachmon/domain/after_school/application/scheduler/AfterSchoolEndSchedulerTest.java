package solvit.teachmon.domain.after_school.application.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolBusinessTripEntity;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolReinforcementEntity;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolBusinessTripRepository;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolReinforcementRepository;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolRepository;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;
import solvit.teachmon.domain.branch.domain.repository.BranchRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("방과후 자동 종료 스케줄러 테스트")
class AfterSchoolEndSchedulerTest {

    @InjectMocks
    private AfterSchoolEndScheduler afterSchoolEndScheduler;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private AfterSchoolRepository afterSchoolRepository;

    @Mock
    private AfterSchoolReinforcementRepository afterSchoolReinforcementRepository;

    @Mock
    private AfterSchoolBusinessTripRepository afterSchoolBusinessTripRepository;

    private LocalDate testDate;
    private BranchEntity branch;
    private AfterSchoolEntity afterSchool1;
    private AfterSchoolEntity afterSchool2;
    private AfterSchoolEntity afterSchool3;

    @BeforeEach
    void setUp() {
        testDate = LocalDate.now(); // 현재 날짜 사용
        
        branch = mock(BranchEntity.class);
        afterSchool1 = mock(AfterSchoolEntity.class);
        afterSchool2 = mock(AfterSchoolEntity.class);
        afterSchool3 = mock(AfterSchoolEntity.class);
    }

    @Test
    @DisplayName("브랜치가 없으면 종료하지 않는다")
    void shouldNotEndWhenBranchNotFound() {
        // Given: 브랜치가 존재하지 않음
        when(branchRepository.findByAfterSchoolDate(testDate))
            .thenReturn(Optional.empty());

        // When: 스케줄러 실행
        afterSchoolEndScheduler.checkAndEndAfterSchool();

        // Then: 다른 repository 호출되지 않음
        verify(afterSchoolRepository, never()).findByBranchAndIsEndFalse(any());
        verify(afterSchool1, never()).endAfterSchool();
        verify(afterSchool2, never()).endAfterSchool();
        verify(afterSchool3, never()).endAfterSchool();
    }

    @Test
    @DisplayName("활성 방과후가 없으면 종료하지 않는다")
    void shouldNotEndWhenNoActiveAfterSchools() {
        // Given: 브랜치 조건은 만족하지만 활성 방과후가 없음
        when(branchRepository.findByAfterSchoolDate(testDate))
            .thenReturn(Optional.of(branch));
        when(afterSchoolRepository.findByBranchAndIsEndFalse(branch))
            .thenReturn(List.of());

        // When: 스케줄러 실행
        afterSchoolEndScheduler.checkAndEndAfterSchool();

        // Then: 종료 처리가 호출되지 않음
        verify(afterSchoolReinforcementRepository, never()).findFutureReinforcementsByAfterSchools(anyList(), any());
        verify(afterSchool1, never()).endAfterSchool();
        verify(afterSchool2, never()).endAfterSchool();
        verify(afterSchool3, never()).endAfterSchool();
    }

    @Test
    @DisplayName("미래 보강이 있는 방과후는 종료하지 않는다")
    void shouldNotEndAfterSchoolsWithFutureReinforcement() {
        // Given: 활성 방과후와 미래 보강이 있음
        List<AfterSchoolEntity> activeAfterSchools = List.of(afterSchool1, afterSchool2);
        
        AfterSchoolReinforcementEntity futureReinforcement = mock(AfterSchoolReinforcementEntity.class);
        when(futureReinforcement.getAfterSchool()).thenReturn(afterSchool1);
        
        setupBasicMocks(activeAfterSchools);
        when(afterSchoolReinforcementRepository.findFutureReinforcementsByAfterSchools(activeAfterSchools, testDate))
            .thenReturn(List.of(futureReinforcement));
        when(afterSchoolBusinessTripRepository.findPastBusinessTripsByAfterSchools(activeAfterSchools, testDate))
            .thenReturn(List.of());

        // When: 스케줄러 실행
        afterSchoolEndScheduler.checkAndEndAfterSchool();

        // Then: 미래 보강이 있는 afterSchool1은 종료되지 않고, afterSchool2만 종료됨
        verify(afterSchool1, never()).endAfterSchool();
        verify(afterSchool2, times(1)).endAfterSchool();
    }

    @Test
    @DisplayName("미보강 출장이 있는 방과후는 종료하지 않는다")
    void shouldNotEndAfterSchoolsWithUnreinforcedBusinessTrips() {
        // Given: 활성 방과후와 미보강 출장이 있음
        List<AfterSchoolEntity> activeAfterSchools = List.of(afterSchool1, afterSchool2);
        
        AfterSchoolBusinessTripEntity pastTrip = mock(AfterSchoolBusinessTripEntity.class);
        when(pastTrip.getAfterSchool()).thenReturn(afterSchool1);
        
        setupBasicMocks(activeAfterSchools);
        when(afterSchoolReinforcementRepository.findFutureReinforcementsByAfterSchools(activeAfterSchools, testDate))
            .thenReturn(List.of());
        when(afterSchoolBusinessTripRepository.findPastBusinessTripsByAfterSchools(activeAfterSchools, testDate))
            .thenReturn(List.of(pastTrip));
        when(afterSchoolBusinessTripRepository.findAfterSchoolsWithUnreinforcedTrips(List.of(afterSchool1), testDate))
            .thenReturn(List.of(afterSchool1)); // afterSchool1에 미보강 출장 있음

        // When: 스케줄러 실행
        afterSchoolEndScheduler.checkAndEndAfterSchool();

        // Then: 미보강 출장이 있는 afterSchool1은 종료되지 않고, afterSchool2만 종료됨
        verify(afterSchool1, never()).endAfterSchool();
        verify(afterSchool2, times(1)).endAfterSchool();
    }

    @Test
    @DisplayName("출장과 보강 개수가 같으면 방과후를 종료한다")
    void shouldEndAfterSchoolsWhenBusinessTripsAreFullyReinforced() {
        // Given: 출장 개수와 보강 개수가 같음 (미보강 출장 없음)
        List<AfterSchoolEntity> activeAfterSchools = List.of(afterSchool1, afterSchool2);
        
        AfterSchoolBusinessTripEntity pastTrip = mock(AfterSchoolBusinessTripEntity.class);
        when(pastTrip.getAfterSchool()).thenReturn(afterSchool1);
        
        setupBasicMocks(activeAfterSchools);
        when(afterSchoolReinforcementRepository.findFutureReinforcementsByAfterSchools(activeAfterSchools, testDate))
            .thenReturn(List.of());
        when(afterSchoolBusinessTripRepository.findPastBusinessTripsByAfterSchools(activeAfterSchools, testDate))
            .thenReturn(List.of(pastTrip));
        when(afterSchoolBusinessTripRepository.findAfterSchoolsWithUnreinforcedTrips(List.of(afterSchool1), testDate))
            .thenReturn(List.of()); // 미보강 출장 없음 (모두 보강됨)

        // When: 스케줄러 실행
        afterSchoolEndScheduler.checkAndEndAfterSchool();

        // Then: 모든 방과후가 종료됨
        verify(afterSchool1, times(1)).endAfterSchool();
        verify(afterSchool2, times(1)).endAfterSchool();
    }

    @Test
    @DisplayName("모든 조건을 만족하면 방과후를 종료한다")
    void shouldEndAfterSchoolsWhenAllConditionsMet() {
        // Given: 모든 조건이 방과후 종료에 부합함
        List<AfterSchoolEntity> activeAfterSchools = List.of(afterSchool1, afterSchool2, afterSchool3);
        
        setupBasicMocks(activeAfterSchools);
        when(afterSchoolReinforcementRepository.findFutureReinforcementsByAfterSchools(activeAfterSchools, testDate))
            .thenReturn(List.of()); // 미래 보강 없음
        when(afterSchoolBusinessTripRepository.findPastBusinessTripsByAfterSchools(activeAfterSchools, testDate))
            .thenReturn(List.of()); // 과거 출장 없음

        // When: 스케줄러 실행
        afterSchoolEndScheduler.checkAndEndAfterSchool();

        // Then: 모든 방과후가 종료됨
        verify(afterSchool1, times(1)).endAfterSchool();
        verify(afterSchool2, times(1)).endAfterSchool();
        verify(afterSchool3, times(1)).endAfterSchool();
    }

    @Test
    @DisplayName("복잡한 시나리오 - 일부는 종료, 일부는 보류")
    void shouldHandleComplexScenarioCorrectly() {
        // Given: 복잡한 시나리오
        List<AfterSchoolEntity> activeAfterSchools = List.of(afterSchool1, afterSchool2, afterSchool3);

        // afterSchool1: 미래 보강 있음 (종료 안됨)
        AfterSchoolReinforcementEntity futureReinforcement = mock(AfterSchoolReinforcementEntity.class);
        when(futureReinforcement.getAfterSchool()).thenReturn(afterSchool1);
        
        // afterSchool2: 미보강 출장 있음 (종료 안됨)
        AfterSchoolBusinessTripEntity unreinforcedTrip = mock(AfterSchoolBusinessTripEntity.class);
        when(unreinforcedTrip.getAfterSchool()).thenReturn(afterSchool2);
        
        // afterSchool3: 정상 종료 조건 (종료됨)
        
        setupBasicMocks(activeAfterSchools);
        when(afterSchoolReinforcementRepository.findFutureReinforcementsByAfterSchools(activeAfterSchools, testDate))
            .thenReturn(List.of(futureReinforcement));
        when(afterSchoolBusinessTripRepository.findPastBusinessTripsByAfterSchools(activeAfterSchools, testDate))
            .thenReturn(List.of(unreinforcedTrip));
        when(afterSchoolBusinessTripRepository.findAfterSchoolsWithUnreinforcedTrips(List.of(afterSchool2), testDate))
            .thenReturn(List.of(afterSchool2)); // afterSchool2에 미보강 출장 있음

        // When: 스케줄러 실행
        afterSchoolEndScheduler.checkAndEndAfterSchool();

        // Then: afterSchool3만 종료되고 나머지는 보류됨
        verify(afterSchool1, never()).endAfterSchool(); // 미래 보강으로 보류
        verify(afterSchool2, never()).endAfterSchool(); // 미보강 출장으로 보류
        verify(afterSchool3, times(1)).endAfterSchool(); // 정상 종료
    }

    @Test
    @DisplayName("출장이 여러 개 있고 일부만 보강된 경우 종료하지 않는다")
    void shouldNotEndWhenMultipleTripsPartiallyReinforced() {
        // Given: 출장 2개, 보강 1개인 경우
        List<AfterSchoolEntity> activeAfterSchools = List.of(afterSchool1);
        
        AfterSchoolBusinessTripEntity trip1 = mock(AfterSchoolBusinessTripEntity.class);
        when(trip1.getAfterSchool()).thenReturn(afterSchool1);
        
        AfterSchoolBusinessTripEntity trip2 = mock(AfterSchoolBusinessTripEntity.class);
        when(trip2.getAfterSchool()).thenReturn(afterSchool1);
        
        setupBasicMocks(activeAfterSchools);
        when(afterSchoolReinforcementRepository.findFutureReinforcementsByAfterSchools(activeAfterSchools, testDate))
            .thenReturn(List.of());
        when(afterSchoolBusinessTripRepository.findPastBusinessTripsByAfterSchools(activeAfterSchools, testDate))
            .thenReturn(List.of(trip1, trip2)); // 출장 2개
        when(afterSchoolBusinessTripRepository.findAfterSchoolsWithUnreinforcedTrips(List.of(afterSchool1), testDate))
            .thenReturn(List.of(afterSchool1)); // afterSchool1에 미보강 출장 있음 (출장 2개 > 보강 1개)

        // When: 스케줄러 실행
        afterSchoolEndScheduler.checkAndEndAfterSchool();

        // Then: 출장 개수 > 보강 개수이므로 종료되지 않음
        verify(afterSchool1, never()).endAfterSchool();
    }

    @Test
    @DisplayName("출장보다 보강이 더 많은 경우 방과후를 종료한다")
    void shouldEndWhenMoreReinforcementsThanTrips() {
        // Given: 출장보다 보강이 더 많은 경우 (미보강 출장 없음)
        List<AfterSchoolEntity> activeAfterSchools = List.of(afterSchool1);
        
        AfterSchoolBusinessTripEntity trip = mock(AfterSchoolBusinessTripEntity.class);
        when(trip.getAfterSchool()).thenReturn(afterSchool1);
        
        setupBasicMocks(activeAfterSchools);
        when(afterSchoolReinforcementRepository.findFutureReinforcementsByAfterSchools(activeAfterSchools, testDate))
            .thenReturn(List.of());
        when(afterSchoolBusinessTripRepository.findPastBusinessTripsByAfterSchools(activeAfterSchools, testDate))
            .thenReturn(List.of(trip)); // 출장 1개
        when(afterSchoolBusinessTripRepository.findAfterSchoolsWithUnreinforcedTrips(List.of(afterSchool1), testDate))
            .thenReturn(List.of()); // 미보강 출장 없음 (보강이 충분함)

        // When: 스케줄러 실행
        afterSchoolEndScheduler.checkAndEndAfterSchool();

        // Then: 보강 개수 >= 출장 개수이므로 종료됨
        verify(afterSchool1, times(1)).endAfterSchool();
    }

    private void setupBasicMocks(List<AfterSchoolEntity> activeAfterSchools) {
        when(branchRepository.findByAfterSchoolDate(testDate))
            .thenReturn(Optional.of(branch));
        when(afterSchoolRepository.findByBranchAndIsEndFalse(branch))
            .thenReturn(activeAfterSchools);
    }
}