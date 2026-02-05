package solvit.teachmon.domain.supervision.domain.dto;

import lombok.Builder;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionType;
import solvit.teachmon.global.enums.WeekDay;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 교사별 감독 정보를 담는 DTO
 */
@Builder(toBuilder = true)
public record TeacherSupervisionInfo(
        Long teacherId,
        String teacherName,
        Set<WeekDay> banDays,                           // 금지 요일들 (SupervisionBanDayRepository에서 조회)
        LocalDate lastSupervisionDate,                  // 최근 감독 날짜 (우선순위 계산용)
        int totalSupervisionCount,                      // 총 감독 횟수 (우선순위 계산용)
        Map<SupervisionType, Integer> supervisionCounts // 감독 타입별 횟수
) {

    /**
     * 해당 요일이 금지요일인지 확인
     * banday = 1 (금지) or 0 (허용)
     */
    public boolean isBanDay(DayOfWeek dayOfWeek) {
        WeekDay weekDay = convertToWeekDay(dayOfWeek);
        return banDays.contains(weekDay);
    }

    /**
     * 최근 감독일로부터 경과일 계산
     * 감독한 적이 없으면 365일 (1년, 최고 우선순위)
     */
    public long getDaysSinceLastSupervision(LocalDate targetDate) {
        if (lastSupervisionDate == null) {
            return 365L; // 1년에 해당하는 일수로 충분히 높은 우선순위 보장
        }
        return ChronoUnit.DAYS.between(lastSupervisionDate, targetDate);
    }

    /**
     * 감독 배정 후 교사 정보 업데이트
     */
    public TeacherSupervisionInfo withUpdatedSupervision(LocalDate newDate, SupervisionType type) {
        Map<SupervisionType, Integer> updatedCounts = new HashMap<>(supervisionCounts);
        updatedCounts.put(type, updatedCounts.getOrDefault(type, 0) + 1);
        
        return this.toBuilder()
                .lastSupervisionDate(newDate)
                .totalSupervisionCount(totalSupervisionCount + 1)
                .supervisionCounts(updatedCounts)
                .build();
    }

    /**
     * DayOfWeek -> WeekDay 변환
     */
    private WeekDay convertToWeekDay(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> WeekDay.MON;
            case TUESDAY -> WeekDay.TUE;
            case WEDNESDAY -> WeekDay.WED;
            case THURSDAY -> WeekDay.THU;
            default -> throw new IllegalArgumentException("지원하지 않는 요일입니다: " + dayOfWeek);
        };
    }
}