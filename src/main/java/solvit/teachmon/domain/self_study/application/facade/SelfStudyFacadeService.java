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
        // 분기 가져오기
        BranchEntity branchEntity = branchRepository.findByYearAndBranch(year, branch)
                .orElseThrow(BranchNotFoundException::new);

        // 기존 자습 설정 제거
        selfStudyRepository.deleteAllByBranchAndGrade(branchEntity, grade);

        // 새로운 자습 설정 추가 리스트
        List<SelfStudyEntity> selfStudyEntities = selfStudyMapper.toEntities(request, branchEntity, grade);

        // 일괄 저장하기
        selfStudyRepository.saveAll(selfStudyEntities);
    }

    public List<WeekDaySelfStudyDto> getSelfStudy(Integer year, Integer branch, Integer grade) {
        // 분기 가져오기
        BranchEntity branchEntity = branchRepository.findByYearAndBranch(year, branch)
                .orElseThrow(BranchNotFoundException::new);

        // WeekDay 로 그룹화된 데이터 가져오기
        Map<WeekDay, List<SchoolPeriod>> groupedByWeekDay = selfStudyRepository.findGroupedByWeekDay(branchEntity, grade);

        // 모든 요일에 대해 Dto 변환
        return selfStudyMapper.toWeekDaySelfStudyDtos(groupedByWeekDay);
    }
}
