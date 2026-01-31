package solvit.teachmon.domain.after_school.exception;

public class SpreadSheetHeaderSizeMismatchException extends SpreadSheetHeaderException {
    public SpreadSheetHeaderSizeMismatchException() {
        super("스프레드 시트 헤더 컬럼의 수가 일치하지 않습니다.");
    }
}
