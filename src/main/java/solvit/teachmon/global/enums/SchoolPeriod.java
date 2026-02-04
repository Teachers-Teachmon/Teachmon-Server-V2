package solvit.teachmon.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SchoolPeriod {
    SEVEN_PERIOD("7교시"),
    EIGHT_AND_NINE_PERIOD("8~9교시"),
    TEN_AND_ELEVEN_PERIOD("10~11교시");

    private final String period;

    public static String convertPeriodToKorean(SchoolPeriod period) {
        return switch (period) {
            case SEVEN_PERIOD -> "7교시";
            case EIGHT_AND_NINE_PERIOD -> "8~9교시";
            case TEN_AND_ELEVEN_PERIOD -> "10~11교시";
            default -> throw new IllegalArgumentException("Unsupported SchoolPeriod: " + period);
        };
    }
}
