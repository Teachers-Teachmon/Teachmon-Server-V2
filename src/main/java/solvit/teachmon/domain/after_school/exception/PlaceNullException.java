package solvit.teachmon.domain.after_school.exception;

public class PlaceNullException extends TeachmonSpreadSheetException {
    
    public PlaceNullException(long rowNum) {
        super(rowNum + "행의 장소이름 값이 비어있습니다.");
    }
}
