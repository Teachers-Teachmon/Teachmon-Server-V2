package solvit.teachmon.domain.after_school.domain.enums;

import solvit.teachmon.domain.after_school.exception.InvalidAfterSchoolUpdateRequestException;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.util.Locale;

public enum AfterSchoolUpdatePeriod {
    EIGHT_AND_NINE_PERIOD(SchoolPeriod.EIGHT_AND_NINE_PERIOD),
    TEN_AND_ELEVEN_PERIOD(SchoolPeriod.TEN_AND_ELEVEN_PERIOD),
    EIGHT_TO_ELEVEN_PERIOD(null);

    private final SchoolPeriod schoolPeriod;

    AfterSchoolUpdatePeriod(SchoolPeriod schoolPeriod) {
        this.schoolPeriod = schoolPeriod;
    }

    public static AfterSchoolUpdatePeriod from(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return switch (value.trim().toUpperCase(Locale.ROOT)) {
            case "EIGHT_AND_NINE_PERIOD", "8~9교시" -> EIGHT_AND_NINE_PERIOD;
            case "TEN_AND_ELEVEN_PERIOD", "10~11교시" -> TEN_AND_ELEVEN_PERIOD;
            case "EIGHT_TO_ELEVEN_PERIOD", "EIGHT_TO_ELEVEN", "8~11교시" -> EIGHT_TO_ELEVEN_PERIOD;
            default -> throw new InvalidAfterSchoolUpdateRequestException("유효하지 않은 방과후 교시 요청입니다: " + value);
        };
    }

    public boolean isCombined() {
        return this == EIGHT_TO_ELEVEN_PERIOD;
    }

    public SchoolPeriod toSchoolPeriod() {
        if (schoolPeriod == null) {
            throw new InvalidAfterSchoolUpdateRequestException("8~11교시는 단일 방과후 교시로 저장할 수 없습니다.");
        }
        return schoolPeriod;
    }
}
