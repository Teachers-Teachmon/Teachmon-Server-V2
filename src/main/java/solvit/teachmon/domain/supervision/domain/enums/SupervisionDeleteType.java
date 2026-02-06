package solvit.teachmon.domain.supervision.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SupervisionDeleteType {
    SELF_STUDY("self_study", SupervisionType.SELF_STUDY_SUPERVISION),
    LEAVE_SEAT("leave_seat", SupervisionType.LEAVE_SEAT_SUPERVISION),
    ALL("all", null);

    private final String value;
    private final SupervisionType supervisionType;

    @JsonValue
    public String getValue() {
        return value;
    }

    public SupervisionType toSupervisionType() {
        return supervisionType;
    }

    public boolean isAll() {
        return this == ALL;
    }
}