package solvit.teachmon.domain.after_school.exception;

public class PlaceNotExistException extends TeachmonSpreadSheetException {
    
    public PlaceNotExistException(long rowNum, String placeName) {
        super(rowNum + "행의 장소이름 '" + placeName + "'가 시스템에 등록되지 않았습니다.");
    }
}
