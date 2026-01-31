package solvit.teachmon.domain.leave_seat.presentation.dto.response;

import lombok.Builder;

@Builder
public record PlaceInfoResponse(
        Long id,
        String name
) {}
