package solvit.teachmon.domain.management.student.domain.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

public class InvalidStudentInfoException extends TeachmonBusinessException {
  public InvalidStudentInfoException(String message) {
    super(message, HttpStatus.BAD_REQUEST);
  }
}