package solvit.teachmon.domain.supervision.domain.repository;

import solvit.teachmon.domain.management.teacher.presentation.dto.response.TeacherListResponse;

import java.util.List;

public interface SupervisionScheduleQueryDslRepository {
    List<TeacherListResponse> countTeacherSupervision(String query);
}