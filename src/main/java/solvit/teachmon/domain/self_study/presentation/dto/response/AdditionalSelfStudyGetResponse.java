package solvit.teachmon.domain.self_study.presentation.dto.response;

import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDate;
import java.util.List;

@QueryProjection
public record AdditionalSelfStudyGetResponse(
        LocalDate day,
        Integer grade,
        List<AdditionalSelfStudyPeriodResponse> periods
) {}
