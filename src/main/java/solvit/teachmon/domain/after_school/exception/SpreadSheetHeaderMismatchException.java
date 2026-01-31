package solvit.teachmon.domain.after_school.exception;

public class SpreadSheetHeaderMismatchException extends SpreadSheetHeaderException {
    public SpreadSheetHeaderMismatchException(Integer index, String actual, String expected) {
        super(
                "헤더의 " + (index + 1) + "번째 컬럼명이 올바르지 않습니다. "
                        + "기대값은 '" + expected + "' 이지만, 실제 값은 '" + actual + "' 입니다."
        );
    }
}
