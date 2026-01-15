package solvit.teachmon.domain.self_study.application.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;
import solvit.teachmon.domain.branch.domain.repository.BranchRepository;
import solvit.teachmon.domain.self_study.domain.entity.SelfStudyEntity;
import solvit.teachmon.domain.self_study.domain.repository.SelfStudyRepository;
import solvit.teachmon.domain.self_study.presentation.dto.request.SelfStudySetRequest;
import solvit.teachmon.global.annotation.Trace;
import solvit.teachmon.global.enums.WeekDay;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SelfStudyFacadeService {
    private final SelfStudyRepository selfStudyRepository;
    private final BranchRepository branchRepository;

    @Trace
    @Transactional
    public void setSelfStudy(Integer year, Integer branch, Integer grade, List<SelfStudySetRequest> request) {
        // 분기 가져오기
        BranchEntity branchEntity = branchRepository.findByYearAndBranch(year, branch)
                .orElseThrow(() -> new IllegalArgumentException("해당 분기를 찾을 수 없습니다. 분기 설정을 먼저 해주세요"));

        // 기존 자습 설정 제거
        selfStudyRepository.deleteAllByYearAndBranchAndGrade(year, branchEntity, grade);

        // 새로운 자습 설정 추가 리스트
        List<SelfStudyEntity> selfStudyEntities = new ArrayList<>();

        for(SelfStudySetRequest selfStudySetRequest : request) {    // 각 요일별 자습 설정 가져오기
            WeekDay weekDay = selfStudySetRequest.weekDay();

            // 각 교시별 자습 설정 가져온 후 entity 생성
            selfStudySetRequest.periods().stream()
                    .distinct() // 중복 제거
                    .map(p -> SelfStudyEntity.builder()
                            .year(year)
                            .branch(branchEntity)
                            .grade(grade)
                            .weekDay(weekDay)
                            .period(p)
                            .build())
                    .forEach(selfStudyEntities::add);
        }

        // 일괄 저장하기
        selfStudyRepository.saveAll(selfStudyEntities);
    }
}
