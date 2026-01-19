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
}
