package solvit.teachmon.domain.self_study.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.self_study.application.mapper.AdditionalSelfStudyMapper;
import solvit.teachmon.domain.self_study.domain.entity.AdditionalSelfStudyEntity;
import solvit.teachmon.domain.self_study.domain.repository.AdditionalSelfStudyRepository;
import solvit.teachmon.domain.self_study.presentation.dto.request.AdditionalSelfStudySetRequest;
import solvit.teachmon.domain.self_study.presentation.dto.response.AdditionalSelfStudyGetResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdditionalSelfStudyService {
    private final AdditionalSelfStudyRepository additionalSelfStudyRepository;
    private final AdditionalSelfStudyMapper additionalSelfStudyMapper;

    @Transactional
    public void setAdditionalSelfStudy(AdditionalSelfStudySetRequest request) {
        // 추가 학습 entity 로 변환
        List<AdditionalSelfStudyEntity> additionalSelfStudyEntities = additionalSelfStudyMapper.toEntities(request);

        additionalSelfStudyRepository.saveAll(additionalSelfStudyEntities);
    }

    @Transactional(readOnly = true)
    public List<AdditionalSelfStudyGetResponse> getAdditionalSelfStudy(Integer year) {
        return additionalSelfStudyRepository.findGroupedByDayAndGrade(year);
    }

    @Transactional
    public void deleteAdditionalSelfStudy(Long additionalId) {
        additionalSelfStudyRepository.deleteById(additionalId);
    }
}
