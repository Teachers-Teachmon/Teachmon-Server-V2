package solvit.teachmon.domain.after_school.application.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolBusinessTripEntity;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolReinforcementEntity;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolBusinessTripRepository;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolReinforcementRepository;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolRepository;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;
import solvit.teachmon.domain.branch.domain.repository.BranchRepository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AfterSchoolEndScheduler {
    
    private final BranchRepository branchRepository;
    private final AfterSchoolRepository afterSchoolRepository;
    private final AfterSchoolReinforcementRepository afterSchoolReinforcementRepository;
    private final AfterSchoolBusinessTripRepository afterSchoolBusinessTripRepository;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @Transactional
    public void checkAndEndAfterSchool() {
        LocalDate today = LocalDate.now();
        
        Optional<BranchEntity> branch = branchRepository.findByAfterSchoolDate(today);
        if (branch.isEmpty()) {
            return;
        }

        List<AfterSchoolEntity> activeAfterSchools = afterSchoolRepository.findByBranchAndIsEndFalse(branch.get());
        if (activeAfterSchools.isEmpty()) {
            return;
        }

        // 미래 보강 예정인 방과후들 찾기
        List<AfterSchoolReinforcementEntity> futureReinforcements = afterSchoolReinforcementRepository
                .findFutureReinforcementsByAfterSchools(activeAfterSchools, today);
        Set<AfterSchoolEntity> afterSchoolsWithFutureReinforcement = futureReinforcements.stream()
                .map(AfterSchoolReinforcementEntity::getAfterSchool)
                .collect(Collectors.toSet());

        // 과거 출장이 있는 방과후들 찾기  
        List<AfterSchoolBusinessTripEntity> pastBusinessTrips = afterSchoolBusinessTripRepository
                .findPastBusinessTripsByAfterSchools(activeAfterSchools, today);
        
        // 미보강 출장이 있는 방과후들 찾기 (한 번에 처리)
        Set<AfterSchoolEntity> afterSchoolsWithUnreinforcedTrips = findAfterSchoolsWithUnreinforcedTrips(
                pastBusinessTrips, today);

        int endedCount = 0;
        int skippedCount = 0;

        for (AfterSchoolEntity afterSchool : activeAfterSchools) {
            // 미래 보강이 있으면 종료하지 않음
            if (afterSchoolsWithFutureReinforcement.contains(afterSchool)) {
                skippedCount++;
                continue;
            }

            // 과거 출장 중 보강하지 않은 것이 있으면 종료하지 않음
            if (afterSchoolsWithUnreinforcedTrips.contains(afterSchool)) {
                skippedCount++;
                continue;
            }

            // 모든 조건 통과하면 종료
            afterSchool.endAfterSchool();
            endedCount++;
        }

        log.info("방과후 자동 종료 완료 - 종료: {}개, 보류: {}개", endedCount, skippedCount);
    }

    private Set<AfterSchoolEntity> findAfterSchoolsWithUnreinforcedTrips(List<AfterSchoolBusinessTripEntity> pastBusinessTrips,
                                                                        LocalDate today) {
        if (pastBusinessTrips.isEmpty()) {
            return Set.of();
        }

        // 출장이 있는 방과후들 추출 (중복 제거)
        List<AfterSchoolEntity> afterSchoolsWithTrips = pastBusinessTrips.stream()
                .map(AfterSchoolBusinessTripEntity::getAfterSchool)
                .distinct()
                .toList();

        // DB에서 직접 미보강 방과후들 조회 (출장 개수 > 보강 개수인 방과후들)
        List<AfterSchoolEntity> unreinforcedAfterSchools = afterSchoolBusinessTripRepository
                .findAfterSchoolsWithUnreinforcedTrips(afterSchoolsWithTrips, today);
        
        return new HashSet<>(unreinforcedAfterSchools);
    }
}