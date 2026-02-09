package solvit.teachmon.domain.place.presentation.dto.response;

import com.querydsl.core.annotations.QueryProjection;

@QueryProjection
public record PlaceSearchResponseDto(
        Long id,
        String name,
        Integer floor
) {
}