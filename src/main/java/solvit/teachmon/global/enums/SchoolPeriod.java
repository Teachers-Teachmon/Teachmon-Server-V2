package solvit.teachmon.global.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SchoolPeriod {
    SEVEN_PERIOD("7교시"),
    EIGHT_AND_NINE_PERIOD("8~9교시"),
    TEN_AND_ELEVEN_PERIOD("10~11교시");

    private final String period;
}
