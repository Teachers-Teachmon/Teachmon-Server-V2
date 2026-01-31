package solvit.teachmon.domain.branch.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BranchRequestDto {
    @JsonProperty("number")
    @NotNull(message = "분기 생성 요청에서 number(분기 번호)는 필수입니다.")
    @Min(value = 1, message = "분기 번호는 1부터 4까지만 가능합니다.")
    @Max(value = 4, message = "분기 번호는 1부터 4까지만 가능합니다.")
    private Integer number;
    
    @JsonProperty("start_day")
    @NotNull(message = "분기 생성 요청에서 start_day(시작일)는 필수입니다.")
    private LocalDate startDay;
    
    @JsonProperty("end_day")
    @NotNull(message = "분기 생성 요청에서 end_day(종료일)는 필수입니다.")
    private LocalDate endDay;
}
