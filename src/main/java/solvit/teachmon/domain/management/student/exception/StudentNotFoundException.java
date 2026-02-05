package solvit.teachmon.domain.management.student.exception;

import solvit.teachmon.global.entity.exception.ResourceNotFoundException;

public class StudentNotFoundException extends ResourceNotFoundException {
    public StudentNotFoundException() {
        super("요청한 학생을 찾을 수 없습니다.");
    }
}
