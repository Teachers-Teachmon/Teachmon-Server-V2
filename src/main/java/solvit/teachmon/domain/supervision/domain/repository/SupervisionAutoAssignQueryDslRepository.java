package solvit.teachmon.domain.supervision.domain.repository;

import solvit.teachmon.domain.supervision.domain.vo.SupervisionBanDayVo;
import solvit.teachmon.domain.supervision.domain.vo.TeacherSupervisionInfoVo;

import java.time.LocalDate;
import java.util.List;

/**
 * 감독 자동 배정을 위한 Repository (QueryDSL 구현)
 */
public interface SupervisionAutoAssignQueryDslRepository {

    /**
     * 감독 가능한 교사들의 감독 정보 조회
     * VIEWER 역할이 아니고 @bssm.hs.kr 메일을 가진 활성 교사들
     * 최근 감독일과 총 감독횟수 포함
     */
    List<TeacherSupervisionInfoVo> findEligibleTeacherSupervisionInfo();

    /**
     * 교사들의 금지요일 정보 조회
     */
    List<SupervisionBanDayVo> findBanDaysByTeacherIds(List<Long> teacherIds);

    /**
     * 특정 날짜에 이미 스케줄이 존재하는지 확인
     */
    boolean existsScheduleByDate(LocalDate date);
}