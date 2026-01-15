package solvit.teachmon.domain.self_study.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.self_study.domain.entity.AdditionalSelfStudyEntity;
import solvit.teachmon.domain.self_study.domain.repository.AdditionalSelfStudyRepository;
import solvit.teachmon.domain.self_study.presentation.dto.request.AdditionalSelfStudySetRequest;
import solvit.teachmon.domain.self_study.presentation.dto.response.AdditionalSelfStudyGetResponse;
import solvit.teachmon.domain.self_study.presentation.dto.response.AdditionalSelfStudyPeriodResponse;
import solvit.teachmon.global.annotation.Trace;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Trace
    public List<AdditionalSelfStudyGetResponse> getAdditionalSelfStudy(Integer year) {
        // 년도로 전체 추가 자습 조회
        List<AdditionalSelfStudyEntity> additionalSelfStudyEntities = additionalSelfStudyRepository.findByYear(year);

        return additionalSelfStudyEntities.stream()
                // 날짜와 학년으로 그룹화
                .collect(Collectors.groupingBy(row -> new DayAndGradeGroupKey(row.getDay(), row.getGrade())))
                // entrySet()으로 Map을 (key, value) 쌍으로 변환 후 스트림 처리
                .entrySet().stream()
                .map(entry -> new AdditionalSelfStudyGetResponse(
                        entry.getKey().day(),
                        entry.getKey().grade(),
                        entry.getValue().stream()
                                .map(r -> new AdditionalSelfStudyPeriodResponse(
                                        r.getId(),
                                        r.getPeriod()
                                ))
                                .toList()
                ))
                .toList();
    }

    private record DayAndGradeGroupKey(LocalDate day, Integer grade) {}

    @Trace
    @Transactional
    public void deleteAdditionalSelfStudy(Long additionalId) {
        additionalSelfStudyRepository.deleteById(additionalId);
    }
}
