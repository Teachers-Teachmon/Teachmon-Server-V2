package solvit.teachmon.domain.self_study.domain.repository;

import solvit.teachmon.domain.self_study.presentation.dto.response.AdditionalSelfStudyGetResponse;

import java.util.List;

public interface AdditionalSelfStudyQueryDslRepository {
    List<AdditionalSelfStudyGetResponse> findGroupedByDayAndGrade(Integer year);
}
