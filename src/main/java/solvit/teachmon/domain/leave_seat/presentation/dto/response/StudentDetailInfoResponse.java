package solvit.teachmon.domain.leave_seat.presentation.dto.response;

import lombok.Builder;

@Builder
public record StudentDetailInfoResponse(
        Long id,
        Integer number,
        String name
) {}
