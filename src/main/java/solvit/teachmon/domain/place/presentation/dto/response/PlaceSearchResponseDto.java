package solvit.teachmon.domain.place.presentation.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record PlaceSearchResponseDto(
        Long id,
        String name,
        Integer floor
) {
    @QueryProjection
    public PlaceSearchResponseDto(Long id, String name, Integer floor) {
        this.id = id;
        this.name = name;
        this.floor = floor;
    }
}