package solvit.teachmon.domain.after_school.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

public class EmptySpreadSheetHeaderCellException extends TeachmonBusinessException {
  public EmptySpreadSheetHeaderCellException(Integer x, Integer y) {
    super(x + ", " + y + " 좌표가 비어있습니다.", HttpStatus.BAD_REQUEST);
  }
}
