package solvit.teachmon.domain.leave_seat.presentation.dto.response;

import lombok.Builder;

@Builder
public record StudentInfoResponse(
        Integer number,

        String name,

        String state
) {}
