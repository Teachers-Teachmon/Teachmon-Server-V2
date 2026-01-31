package solvit.teachmon.domain.branch.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class BranchResponseDto {
    @JsonProperty("number")
    private Integer number;
    
    @JsonProperty("start_day")
    private LocalDate startDay;
    
    @JsonProperty("end_day")
    private LocalDate endDay;
}
