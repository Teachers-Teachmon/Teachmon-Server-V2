package solvit.teachmon.domain.student_schedule.domain.enums;

import java.util.List;

public enum ScheduleType {
    SELF_STUDY,
    AFTER_SCHOOL,
    LEAVE_SEAT,
    ADDITIONAL_SELF_STUDY,
    EXIT,
    AWAY;

    public boolean isContains(List<ScheduleType> types) {
        return types != null && types.contains(this);
    }
}