package solvit.teachmon.domain.user.exception;

import solvit.teachmon.global.entity.exception.ResourceNotFoundException;

public class TeacherNotFoundException extends ResourceNotFoundException {
    public TeacherNotFoundException() {
        super("요청한 교사를 찾을 수 없습니다.");
    }
}
