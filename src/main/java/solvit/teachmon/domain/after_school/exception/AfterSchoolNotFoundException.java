package solvit.teachmon.domain.after_school.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

public class AfterSchoolNotFoundException extends TeachmonBusinessException {
    public AfterSchoolNotFoundException() {
        super("해당 방과후를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
    }
}