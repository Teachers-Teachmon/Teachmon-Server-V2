package solvit.teachmon.domain.supervision.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SupervisionExchangeRejectRequestDto(
        @JsonProperty("exchange_request_id")
        @NotNull(message = "교체 요청 ID는 필수입니다.")
        Long exchangeRequestId
) {
}