package solvit.teachmon.domain.self_study.presentation.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import solvit.teachmon.global.enums.SchoolPeriod;

@QueryProjection
public record AdditionalSelfStudyPeriodResponse(
        Long id,
        SchoolPeriod period
) {}
