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
import java.util.Map;
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
        
        Optional<BranchEntity> branch = branchRepository.findByYearAndDate(today.getYear(), today);
        if (branch.isEmpty() || !branch.get().isAfterSchoolEndDay(today)) {
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

        // 가장 이른 출장 날짜부터 오늘까지 모든 보강 조회
        LocalDate earliestTripDate = pastBusinessTrips.stream()
                .map(AfterSchoolBusinessTripEntity::getDay)
                .min(LocalDate::compareTo)
                .get();

        List<AfterSchoolReinforcementEntity> allReinforcements = afterSchoolReinforcementRepository
                .findAllByChangeDayBetween(earliestTripDate, today.plusDays(1));
        
        Set<AfterSchoolEntity> unreinforcedAfterSchools = new HashSet<>();

        // 방과후별 출장 개수 계산
        Map<AfterSchoolEntity, Integer> tripCountByAfterSchool = pastBusinessTrips.stream()
                .collect(Collectors.groupingBy(
                    AfterSchoolBusinessTripEntity::getAfterSchool,
                    Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
                ));
        
        // 방과후별 보강 개수 계산 (한 번에)
        Map<AfterSchoolEntity, Long> reinforcementCountByAfterSchool = allReinforcements.stream()
                .collect(Collectors.groupingBy(
                    AfterSchoolReinforcementEntity::getAfterSchool,
                    Collectors.counting()
                ));

        // 출장 개수와 보강 개수 비교
        tripCountByAfterSchool.forEach((afterSchool, tripCount) -> {
            long reinforcementCount = reinforcementCountByAfterSchool.getOrDefault(afterSchool, 0L);
            if (reinforcementCount < tripCount) {
                unreinforcedAfterSchools.add(afterSchool);
            }
        });

        return unreinforcedAfterSchools;
    }
}