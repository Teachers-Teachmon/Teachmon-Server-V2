package solvit.teachmon.domain.self_study.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.self_study.domain.entity.AdditionalSelfStudyEntity;
import solvit.teachmon.domain.self_study.domain.repository.AdditionalSelfStudyRepository;
import solvit.teachmon.domain.self_study.presentation.dto.request.AdditionalSelfStudySetRequest;
import solvit.teachmon.global.annotation.Trace;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdditionalSelfStudyService {
    private final AdditionalSelfStudyRepository additionalSelfStudyRepository;

    @Trace
    @Transactional
    public void setAdditionalSelfStudy(AdditionalSelfStudySetRequest request) {
        List<AdditionalSelfStudyEntity> additionalSelfStudyEntities = new ArrayList<>();

        for(SchoolPeriod period : request.periods()) {
            additionalSelfStudyEntities.add(
                    AdditionalSelfStudyEntity.builder()
                        .day(request.day())
                        .period(period)
                        .grade(request.grade())
                        .build()
            );
        }

        additionalSelfStudyRepository.saveAll(additionalSelfStudyEntities);
    }
}
