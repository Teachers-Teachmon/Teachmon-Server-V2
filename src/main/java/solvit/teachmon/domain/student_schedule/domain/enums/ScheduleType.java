package solvit.teachmon.domain.student_schedule.domain.enums;

import java.util.List;

public enum ScheduleType {
    SELF_STUDY,
    AFTER_SCHOOL,
    FIXED_LEAVE_SEAT,
    LEAVE_SEAT,
    ADDITIONAL_SELF_STUDY,
    EXIT,
    AWAY,
    AFTER_SCHOOL_REINFORCEMENT;

    public boolean isContains(List<ScheduleType> types) {
        return types != null && types.contains(this);
    }
}
