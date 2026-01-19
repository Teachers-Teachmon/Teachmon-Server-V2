package solvit.teachmon.domain.branch.exception;

public class BranchNotFoundException extends RuntimeException {
    public BranchNotFoundException() {
        super("해당 분기를 찾을 수 없습니다. 분기 설정을 먼저 해주세요.");
    }
}
