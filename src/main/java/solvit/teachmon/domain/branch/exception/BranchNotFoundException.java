package solvit.teachmon.domain.branch.exception;

import solvit.teachmon.global.entity.exception.ResourceNotFoundException;

public class BranchNotFoundException extends ResourceNotFoundException {
    public BranchNotFoundException() {
        super("해당 분기를 찾을 수 없습니다. 분기 설정을 먼저 해주세요.");
    }
}
