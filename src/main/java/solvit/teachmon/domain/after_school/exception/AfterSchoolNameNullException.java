package solvit.teachmon.domain.after_school.exception;

public class AfterSchoolNameNullException extends TeachmonSpreadSheetException {
    
    public AfterSchoolNameNullException(long rowNum) {
        super(rowNum + "행의 방과후 이름이 비어있습니다.");
    }
}
