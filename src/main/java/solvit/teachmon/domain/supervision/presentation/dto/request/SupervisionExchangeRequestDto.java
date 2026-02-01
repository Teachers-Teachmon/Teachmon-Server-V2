package solvit.teachmon.domain.supervision.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SupervisionExchangeRequestDto(
        @JsonProperty("requestor_supervision_id")
        @NotNull(message = "요청자 감독 일정 ID는 필수입니다.")
        Long requestorSupervisionId,

        @JsonProperty("change_supervision_id")
        @NotNull(message = "교체할 감독 일정 ID는 필수입니다.")
        Long changeSupervisionId,

        @JsonProperty("reason")
        @NotBlank(message = "교체 사유는 필수입니다.")
        String reason
) {
}