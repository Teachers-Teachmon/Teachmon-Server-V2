package solvit.teachmon.domain.student_schedule.domain.enums;

import lombok.Getter;

import java.util.List;

@Getter
public enum ScheduleType {
    SELF_STUDY(1),
    AFTER_SCHOOL(3),
    FIXED_LEAVE_SEAT(3),
    LEAVE_SEAT(4),
    ADDITIONAL_SELF_STUDY(2),
    EXIT(5),
    AWAY(5),
    AFTER_SCHOOL_REINFORCEMENT(3);

    private final Integer level;

    ScheduleType(Integer level) {
        this.level = level;
    }

    public boolean isContains(List<ScheduleType> types) {
        return types != null && types.contains(this);
    }
}
