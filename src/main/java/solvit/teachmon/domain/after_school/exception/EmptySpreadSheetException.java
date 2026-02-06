package solvit.teachmon.domain.after_school.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

public class EmptySpreadSheetException extends TeachmonBusinessException {
    public EmptySpreadSheetException() {
        super("스프레드 시트의 값이 비어 있습니다.", HttpStatus.BAD_REQUEST);
    }
}
