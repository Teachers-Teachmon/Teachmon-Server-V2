package solvit.teachmon.domain.student_schedule.application.batch.reader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.self_study.domain.entity.SelfStudyEntity;
import solvit.teachmon.domain.self_study.domain.repository.SelfStudyRepository;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;
import solvit.teachmon.domain.branch.domain.repository.BranchRepository;
import solvit.teachmon.domain.branch.exception.BranchNotFoundException;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class SelfStudyScheduleReader implements ItemReader<SelfStudyEntity> {
    private final SelfStudyRepository selfStudyRepository;
    private final BranchRepository branchRepository;
    
    @Value("#{jobParameters['baseDate']}")
    private LocalDate baseDate;
    
    private Iterator<SelfStudyEntity> iterator;

    @Override
    public SelfStudyEntity read() {
        if (iterator == null) {
            initialize();
        }
        
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    private void initialize() {
        BranchEntity branch = branchRepository.findByDay(baseDate)
                .orElseThrow(BranchNotFoundException::new);
        
        List<SelfStudyEntity> selfStudies = selfStudyRepository.findAllByBranchWithPessimisticRead(branch);
        this.iterator = selfStudies.stream()
                .filter(s -> !isBeforeSelfStudy(s, baseDate))
                .toList()
                .iterator();
        
        log.info("SelfStudyScheduleReader initialized with {} items for baseDate={}", 
                selfStudies.size(), baseDate);
    }

    private Boolean isBeforeSelfStudy(SelfStudyEntity selfStudy, LocalDate baseDate) {
        LocalDate selfStudyDay = baseDate.with(selfStudy.getWeekDay().toDayOfWeek());
        return selfStudyDay.isBefore(baseDate);
    }
}
