package solvit.teachmon.domain.student_schedule.exception;

import solvit.teachmon.global.entity.exception.ResourceNotFoundException;

public class StudentScheduleNotFoundException extends ResourceNotFoundException {
    public StudentScheduleNotFoundException() {
        super("학생 스케줄을 찾을 수 없습니다.");
    }
}
