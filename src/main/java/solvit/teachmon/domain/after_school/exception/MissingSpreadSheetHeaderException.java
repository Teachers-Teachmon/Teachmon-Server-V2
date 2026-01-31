package solvit.teachmon.domain.after_school.exception;

public class MissingSpreadSheetHeaderException extends SpreadSheetHeaderException {
    public MissingSpreadSheetHeaderException() {
        super("스프레드 시트의 헤더가 존재하지 않습니다.");
    }
}
