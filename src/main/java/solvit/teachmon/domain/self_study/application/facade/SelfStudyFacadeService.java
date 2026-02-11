package solvit.teachmon.domain.self_study.application.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;
import solvit.teachmon.domain.branch.domain.repository.BranchRepository;
import solvit.teachmon.domain.branch.exception.BranchNotFoundException;
import solvit.teachmon.domain.self_study.application.mapper.SelfStudyMapper;
import solvit.teachmon.domain.self_study.domain.entity.SelfStudyEntity;
import solvit.teachmon.domain.self_study.domain.repository.SelfStudyRepository;
import solvit.teachmon.domain.self_study.presentation.dto.common.WeekDaySelfStudyDto;
import solvit.teachmon.global.enums.SchoolPeriod;
import solvit.teachmon.global.enums.WeekDay;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SelfStudyFacadeService {
    private final SelfStudyRepository selfStudyRepository;
    private final BranchRepository branchRepository;
    private final SelfStudyMapper selfStudyMapper;

    @Transactional
    public void setSelfStudy(Integer year, Integer branch, Integer grade, List<WeekDaySelfStudyDto> request) {
        BranchEntity branchEntity = branchRepository.findByYearAndBranch(year, branch)
                .orElseThrow(BranchNotFoundException::new);

        List<SelfStudyEntity> oldSelfStudies = selfStudyRepository.findAllByBranchAndGrade(branchEntity, grade);
        selfStudyRepository.deleteAll(oldSelfStudies);

        List<SelfStudyEntity> selfStudyEntities = selfStudyMapper.toEntities(request, branchEntity, grade);

        selfStudyRepository.saveAll(selfStudyEntities);
    }

    public List<WeekDaySelfStudyDto> getSelfStudy(Integer year, Integer branch, Integer grade) {
        BranchEntity branchEntity = branchRepository.findByYearAndBranch(year, branch)
                .orElseThrow(BranchNotFoundException::new);

        Map<WeekDay, List<SchoolPeriod>> groupedByWeekDay = selfStudyRepository.findGroupedByWeekDay(branchEntity, grade);

        return selfStudyMapper.toWeekDaySelfStudyDtos(groupedByWeekDay);
    }
}
