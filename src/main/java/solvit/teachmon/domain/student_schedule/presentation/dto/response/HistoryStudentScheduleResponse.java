package solvit.teachmon.domain.student_schedule.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record HistoryStudentScheduleResponse(
        @JsonProperty("student_number")
        Integer studentNumber,
        String name,
        @JsonProperty("ONE_PERIOD")
        PeriodScheduleResponse onePeriod,
        @JsonProperty("TWO_PERIOD")
        PeriodScheduleResponse twoPeriod,
        @JsonProperty("THREE_PERIOD")
        PeriodScheduleResponse threePeriod,
        @JsonProperty("FOUR_PERIOD")
        PeriodScheduleResponse fourPeriod,
        @JsonProperty("FIVE_PERIOD")
        PeriodScheduleResponse fivePeriod,
        @JsonProperty("SIX_PERIOD")
        PeriodScheduleResponse sixPeriod,
        @JsonProperty("SEVEN_PERIOD")
        PeriodScheduleResponse sevenPeriod,
        @JsonProperty("EIGHT_AND_NINE_PERIOD")
        PeriodScheduleResponse eightAndNinePeriod,
        @JsonProperty("TEN_AND_ELEVEN_PERIOD")
        PeriodScheduleResponse tenAndElevenPeriod
) {}