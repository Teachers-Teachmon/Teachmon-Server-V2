package solvit.teachmon.domain.self_study.exception;

import solvit.teachmon.global.entity.exception.ResourceNotFoundException;

public class AdditionalSelfStudyNotFoundException extends ResourceNotFoundException {
    public AdditionalSelfStudyNotFoundException() {
        super("추가 자습을 찾을 수 없습니다.");
    }
}
