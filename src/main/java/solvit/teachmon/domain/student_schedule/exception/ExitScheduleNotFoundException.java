package solvit.teachmon.domain.student_schedule.exception;

import solvit.teachmon.global.entity.exception.ResourceNotFoundException;

public class ExitScheduleNotFoundException extends ResourceNotFoundException {
  public ExitScheduleNotFoundException() {
    super("이탈 스케줄을 찾을 수 없습니다.");
  }
}
