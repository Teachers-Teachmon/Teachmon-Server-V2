package solvit.teachmon.domain.team.exception;

import solvit.teachmon.global.entity.exception.ResourceNotFoundException;

public class TeamNotFoundException extends ResourceNotFoundException {
    public TeamNotFoundException() {
        super("요청한 팀을 찾을 수 없습니다.");
    }
}