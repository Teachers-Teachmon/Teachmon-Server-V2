package solvit.teachmon.domain.supervision.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SupervisionTodayType {
    NONE("NONE"),
    SELF_STUDY("SELF_STUDY"),
    LEAVE_SEAT("LEAVE_SEAT"),
    ALL("ALL");

    private final String value;

    SupervisionTodayType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static SupervisionTodayType from(boolean hasSelfStudy, boolean hasLeaveSeat) {
        if (hasSelfStudy && hasLeaveSeat) {
            return ALL;
        } else if (hasSelfStudy) {
            return SELF_STUDY;
        } else if (hasLeaveSeat) {
            return LEAVE_SEAT;
        } else {
            return NONE;
        }
    }
}