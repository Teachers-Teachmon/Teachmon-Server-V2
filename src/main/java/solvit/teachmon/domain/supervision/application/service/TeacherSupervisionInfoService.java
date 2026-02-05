package solvit.teachmon.domain.supervision.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.supervision.domain.repository.SupervisionAutoAssignRepository;
import solvit.teachmon.domain.supervision.domain.vo.SupervisionBanDayVo;
import solvit.teachmon.domain.supervision.domain.vo.TeacherSupervisionInfo;
import solvit.teachmon.domain.supervision.domain.vo.TeacherSupervisionInfoVo;
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
        List<TeacherSupervisionInfoVo> teacherProjections = 
                getTeacherProjections();
        
        List<Long> teacherIds = extractTeacherIds(teacherProjections);
        Map<Long, Set<WeekDay>> banDaysByTeacher = getBanDaysByTeacher(teacherIds);
        
        return buildTeacherSupervisionInfos(teacherProjections, banDaysByTeacher);
    }

    private List<TeacherSupervisionInfoVo> getTeacherProjections() {
        List<TeacherSupervisionInfoVo> teacherProjections =
                autoAssignRepository.findTeacherSupervisionInfoByRole(Role.TEACHER);

        if (teacherProjections.isEmpty()) {
            throw new InsufficientTeachersException("감독 배정 가능한 교사가 없습니다.");
        }
        
        return teacherProjections;
    }

    private List<Long> extractTeacherIds(List<TeacherSupervisionInfoVo> projections) {
        return projections.stream()
                .map(TeacherSupervisionInfoVo::teacherId)
                .toList();
    }

    private Map<Long, Set<WeekDay>> getBanDaysByTeacher(List<Long> teacherIds) {
        List<SupervisionBanDayVo> banDayProjections =
                autoAssignRepository.findBanDaysByTeacherIds(teacherIds);

        return banDayProjections.stream()
                .collect(Collectors.groupingBy(
                        SupervisionBanDayVo::teacherId,
                        Collectors.mapping(
                                SupervisionBanDayVo::weekDay,
                                Collectors.toSet()
                        )
                ));
    }

    private List<TeacherSupervisionInfo> buildTeacherSupervisionInfos(
            List<TeacherSupervisionInfoVo> projections,
            Map<Long, Set<WeekDay>> banDaysByTeacher) {
        return projections.stream()
                .map(projection -> createTeacherSupervisionInfo(projection, banDaysByTeacher))
                .toList();
    }

    private TeacherSupervisionInfo createTeacherSupervisionInfo(
            TeacherSupervisionInfoVo projection,
            Map<Long, Set<WeekDay>> banDaysByTeacher) {
        return TeacherSupervisionInfo.builder()
                .teacherId(projection.teacherId())
                .teacherName(projection.teacherName())
                .banDays(banDaysByTeacher.getOrDefault(projection.teacherId(), Set.of()))
                .lastSupervisionDate(projection.lastSupervisionDate())
                .totalSupervisionCount(getSupervisionCount(projection))
                .supervisionCounts(new HashMap<>())
                .build();
    }

    private int getSupervisionCount(TeacherSupervisionInfoVo projection) {
        return projection.totalSupervisionCount() != null ? 
                projection.totalSupervisionCount().intValue() : 0;
    }
}