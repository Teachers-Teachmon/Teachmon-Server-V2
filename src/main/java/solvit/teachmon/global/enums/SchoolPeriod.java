package solvit.teachmon.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum SchoolPeriod {
    ONE_PERIOD("1교시", LocalTime.of(0, 0), LocalTime.of(9, 30)),
    TWO_PERIOD("2교시", LocalTime.of(9, 30), LocalTime.of(10, 30)),
    THREE_PERIOD("3교시", LocalTime.of(10, 30), LocalTime.of(11, 30)),
    FOUR_PERIOD("4교시", LocalTime.of(11, 30), LocalTime.of(12, 30)),
    FIVE_PERIOD("5교시", LocalTime.of(12, 30), LocalTime.of(14, 10)),
    SIX_PERIOD("6교시", LocalTime.of(14, 10), LocalTime.of(15, 10)),
    SEVEN_PERIOD("7교시", LocalTime.of(15, 10), LocalTime.of(16, 10)),
    EIGHT_AND_NINE_PERIOD("8~9교시", LocalTime.of(16, 10), LocalTime.of(18, 10)),
    TEN_AND_ELEVEN_PERIOD("10~11교시", LocalTime.of(18, 10), LocalTime.of(23, 59, 59));

    private final String period;

    public static String convertPeriodToKorean(SchoolPeriod period) {
        return switch (period) {
            case SEVEN_PERIOD -> "7교시";
            case EIGHT_AND_NINE_PERIOD -> "8~9교시";
            case TEN_AND_ELEVEN_PERIOD -> "10~11교시";
            default -> throw new IllegalArgumentException("Unsupported SchoolPeriod: " + period);
        };
    }
  
    private final LocalTime startTime;
    private final LocalTime endTime;

    public static SchoolPeriod fromTime(LocalTime time) {
        if (time == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(p -> !time.isBefore(p.startTime) && !time.isAfter(p.endTime))
                .findFirst()
                .orElse(null);
    }

    public static SchoolPeriod fromCurrentTime() {
        return fromTime(LocalTime.now());
    }

    public static List<SchoolPeriod> getAfterActivityPeriod() {
        return List.of(SEVEN_PERIOD, EIGHT_AND_NINE_PERIOD, TEN_AND_ELEVEN_PERIOD);
    }
}
