package solvit.teachmon.domain.supervision.domain.repository;

import solvit.teachmon.domain.supervision.application.dto.TeacherSupervisionCountDto;

import java.util.List;

public interface SupervisionScheduleRepositoryCustom {
    List<TeacherSupervisionCountDto> countTeacherSupervision(String query);
}