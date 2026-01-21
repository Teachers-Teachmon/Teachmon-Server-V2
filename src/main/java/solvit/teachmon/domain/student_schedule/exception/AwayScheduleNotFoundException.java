package solvit.teachmon.domain.student_schedule.exception;

import solvit.teachmon.global.entity.exception.ResourceNotFoundException;

public class AwayScheduleNotFoundException extends ResourceNotFoundException {
    public AwayScheduleNotFoundException() {
        super("조퇴 스케줄을 찾을 수 없습니다");
    }
}
