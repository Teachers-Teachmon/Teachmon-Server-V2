package solvit.teachmon.domain.after_school.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

public class AfterSchoolNotFoundException extends TeachmonBusinessException {
    public AfterSchoolNotFoundException() {
        super("방과후 프로그램을 찾을 수 없습니다", HttpStatus.NOT_FOUND);
    }
    
    public AfterSchoolNotFoundException(Long id) {
        super("ID " + id + "인 방과후 프로그램을 찾을 수 없습니다", HttpStatus.NOT_FOUND);
    }
}
