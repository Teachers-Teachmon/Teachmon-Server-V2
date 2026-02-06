package solvit.teachmon.domain.supervision.domain.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SupervisionExchangeType {
    PENDING("대기"),
    ACCEPTED("수락됨"),
    REJECTED("거절됨");

    private final String value;
}