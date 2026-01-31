package solvit.teachmon.domain.after_school.exception;

public class RowSizeMismatchException extends TeachmonSpreadSheetException {
    
    public RowSizeMismatchException(long rowNum, int expectedSize, int actualSize) {
        super(rowNum + "행의 컬럼 개수가 올바르지 않습니다. 기대값: " + expectedSize + ", 실제값: " + actualSize);
    }
}
