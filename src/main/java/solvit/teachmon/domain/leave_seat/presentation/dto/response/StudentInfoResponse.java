package solvit.teachmon.domain.leave_seat.presentation.dto.response;

import lombok.Builder;

@Builder
public record StudentInfoResponse(
        Long id,

        Integer number,

        String name,

        String state
) {}
