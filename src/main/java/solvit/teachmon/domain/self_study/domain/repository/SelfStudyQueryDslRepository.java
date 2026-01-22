package solvit.teachmon.domain.self_study.domain.repository;

import solvit.teachmon.domain.branch.domain.entity.BranchEntity;
import solvit.teachmon.global.enums.SchoolPeriod;
import solvit.teachmon.global.enums.WeekDay;

import java.util.List;
import java.util.Map;

public interface SelfStudyQueryDslRepository {
    Map<WeekDay, List<SchoolPeriod>> findGroupedByWeekDay(BranchEntity branch, Integer grade);
}
