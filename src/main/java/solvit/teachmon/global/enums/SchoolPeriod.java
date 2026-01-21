package solvit.teachmon.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SchoolPeriod {
    ONE_PERIOD("1교시"),
    TWO_PERIOD("2교시"),
    THREE_PERIOD("3교시"),
    FOUR_PERIOD("4교시"),
    FIVE_PERIOD("5교시"),
    SIX_PERIOD("6교시"),
    SEVEN_PERIOD("7교시"),
    EIGHT_AND_NINE_PERIOD("8~9교시"),
    TEN_AND_ELEVEN_PERIOD("10~11교시");

    private final String period;
}
