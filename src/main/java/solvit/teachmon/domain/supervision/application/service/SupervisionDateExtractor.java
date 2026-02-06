package solvit.teachmon.domain.supervision.application.service;

import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Component
public class SupervisionDateExtractor {

    /**
     * 평일 날짜만 추출 (월~목)
     */
    public List<LocalDate> extractWeekdays(LocalDate startDate, LocalDate endDate) {
        return startDate.datesUntil(endDate.plusDays(1))
                .filter(date -> {
                    DayOfWeek dayOfWeek = date.getDayOfWeek();
                    return dayOfWeek == DayOfWeek.MONDAY ||
                            dayOfWeek == DayOfWeek.TUESDAY ||
                            dayOfWeek == DayOfWeek.WEDNESDAY ||
                            dayOfWeek == DayOfWeek.THURSDAY;
                })
                .toList();
    }
}