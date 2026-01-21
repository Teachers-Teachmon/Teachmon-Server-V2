package solvit.teachmon.domain.student_schedule.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;

@Builder
public record HistoryStudentScheduleResponse(
        @JsonProperty("student_number")
        Integer studentNumber,
        String name,
        @JsonProperty("ONE_PERIOD")
        ScheduleType onePeriod,
        @JsonProperty("TWO_PERIOD")
        ScheduleType twoPeriod,
        @JsonProperty("THREE_PERIOD")
        ScheduleType threePeriod,
        @JsonProperty("FOUR_PERIOD")
        ScheduleType fourPeriod,
        @JsonProperty("FIVE_PERIOD")
        ScheduleType fivePeriod,
        @JsonProperty("SIX_PERIOD")
        ScheduleType sixPeriod,
        @JsonProperty("SEVEN_PERIOD")
        ScheduleType sevenPeriod,
        @JsonProperty("EIGHT_AND_NINE_PERIOD")
        ScheduleType eightAndNinePeriod,
        @JsonProperty("TEN_AND_ELEVEN_PERIOD")
        ScheduleType tenAndElevenPeriod
) {}