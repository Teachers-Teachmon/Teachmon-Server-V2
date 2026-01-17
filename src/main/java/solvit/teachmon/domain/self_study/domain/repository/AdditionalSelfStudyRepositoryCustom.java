package solvit.teachmon.domain.self_study.domain.repository;

import solvit.teachmon.domain.self_study.presentation.dto.response.AdditionalSelfStudyGetResponse;

import java.util.List;

public interface AdditionalSelfStudyRepositoryCustom {
    List<AdditionalSelfStudyGetResponse> findGroupedByDayAndGrade(Integer year);
}
