package solvit.teachmon.domain.student_schedule.exception;

import solvit.teachmon.global.entity.exception.ResourceNotFoundException;

public class ScheduleNotFoundException extends ResourceNotFoundException {
  public ScheduleNotFoundException() {
    super("스케줄을 찾을 수 없습니다.");
  }
}
