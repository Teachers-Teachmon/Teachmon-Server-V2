package solvit.teachmon.domain.management.teacher.exception;

import solvit.teachmon.global.entity.exception.DuplicateException;

public class TeacherAlreadyExistsException extends DuplicateException {
    public TeacherAlreadyExistsException() {
        super("이미 등록된 이메일입니다.");
    }
}
