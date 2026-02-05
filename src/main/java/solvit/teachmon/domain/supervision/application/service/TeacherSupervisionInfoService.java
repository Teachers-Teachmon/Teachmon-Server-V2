package solvit.teachmon.domain.supervision.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.supervision.domain.dto.TeacherSupervisionInfo;
import solvit.teachmon.domain.supervision.domain.repository.SupervisionAutoAssignRepository;
import solvit.teachmon.domain.supervision.exception.InsufficientTeachersException;
import solvit.teachmon.domain.user.domain.enums.Role;
import solvit.teachmon.global.enums.WeekDay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TeacherSupervisionInfoService {

    private final SupervisionAutoAssignRepository autoAssignRepository;

    public List<TeacherSupervisionInfo> getTeacherSupervisionInfos() {
        List<SupervisionAutoAssignRepository.TeacherSupervisionInfoProjection> teacherProjections = 
                getTeacherProjections();
        
        List<Long> teacherIds = extractTeacherIds(teacherProjections);
        Map<Long, Set<WeekDay>> banDaysByTeacher = getBanDaysByTeacher(teacherIds);
        
        return buildTeacherSupervisionInfos(teacherProjections, banDaysByTeacher);
    }

    private List<SupervisionAutoAssignRepository.TeacherSupervisionInfoProjection> getTeacherProjections() {
        List<SupervisionAutoAssignRepository.TeacherSupervisionInfoProjection> teacherProjections =
                autoAssignRepository.findTeacherSupervisionInfoByRole(Role.TEACHER);

        if (teacherProjections.isEmpty()) {
            throw new InsufficientTeachersException("감독 배정 가능한 교사가 없습니다.");
        }
        
        return teacherProjections;
    }

    private List<Long> extractTeacherIds(List<SupervisionAutoAssignRepository.TeacherSupervisionInfoProjection> projections) {
        return projections.stream()
                .map(SupervisionAutoAssignRepository.TeacherSupervisionInfoProjection::getTeacherId)
                .toList();
    }

    private Map<Long, Set<WeekDay>> getBanDaysByTeacher(List<Long> teacherIds) {
        List<SupervisionAutoAssignRepository.SupervisionBanDayProjection> banDayProjections =
                autoAssignRepository.findBanDaysByTeacherIds(teacherIds);

        return banDayProjections.stream()
                .collect(Collectors.groupingBy(
                        SupervisionAutoAssignRepository.SupervisionBanDayProjection::getTeacherId,
                        Collectors.mapping(
                                SupervisionAutoAssignRepository.SupervisionBanDayProjection::getWeekDay,
                                Collectors.toSet()
                        )
                ));
    }

    private List<TeacherSupervisionInfo> buildTeacherSupervisionInfos(
            List<SupervisionAutoAssignRepository.TeacherSupervisionInfoProjection> projections,
            Map<Long, Set<WeekDay>> banDaysByTeacher) {
        return projections.stream()
                .map(projection -> createTeacherSupervisionInfo(projection, banDaysByTeacher))
                .toList();
    }

    private TeacherSupervisionInfo createTeacherSupervisionInfo(
            SupervisionAutoAssignRepository.TeacherSupervisionInfoProjection projection,
            Map<Long, Set<WeekDay>> banDaysByTeacher) {
        return TeacherSupervisionInfo.builder()
                .teacherId(projection.getTeacherId())
                .teacherName(projection.getTeacherName())
                .banDays(banDaysByTeacher.getOrDefault(projection.getTeacherId(), Set.of()))
                .lastSupervisionDate(projection.getLastSupervisionDate())
                .totalSupervisionCount(getSupervisionCount(projection))
                .supervisionCounts(new HashMap<>())
                .build();
    }

    private int getSupervisionCount(SupervisionAutoAssignRepository.TeacherSupervisionInfoProjection projection) {
        return projection.getTotalSupervisionCount() != null ? 
                projection.getTotalSupervisionCount().intValue() : 0;
    }
}