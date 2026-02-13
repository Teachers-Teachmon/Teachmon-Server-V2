package solvit.teachmon.domain.team.domain.repository.querydsl.impl;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.team.domain.repository.querydsl.TeamQueryDslRepository;
import solvit.teachmon.domain.team.presentation.dto.response.TeamWithMembersResponseDto;
import solvit.teachmon.domain.team.presentation.dto.response.TeamMemberDto;
import solvit.teachmon.domain.team.presentation.dto.response.QTeamMemberDto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static solvit.teachmon.domain.team.domain.entity.QTeamEntity.teamEntity;
import static solvit.teachmon.domain.team.domain.entity.QTeamParticipationEntity.teamParticipationEntity;
import static solvit.teachmon.domain.management.student.domain.entity.QStudentEntity.studentEntity;

@Repository
@RequiredArgsConstructor
public class TeamRepositoryImpl implements TeamQueryDslRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<TeamWithMembersResponseDto> searchTeamsByKeyword(String keyword) {
        List<Tuple> queryResults = fetchTeamsWithMembers(keyword);
        return groupResultsByTeam(queryResults);
    }

    private List<Tuple> fetchTeamsWithMembers(String keyword) {
        return queryFactory
                .select(
                        teamEntity.id,
                        teamEntity.name,
                        createTeamMemberProjection()
                )
                .from(teamEntity)
                .leftJoin(teamEntity.teamParticipationList, teamParticipationEntity)
                .leftJoin(teamParticipationEntity.student, studentEntity)
                .where(teamEntity.name.containsIgnoreCase(keyword))
                .fetch();
    }

    private QTeamMemberDto createTeamMemberProjection() {
        return new QTeamMemberDto(
                studentEntity.id,
                studentEntity.number,
                studentEntity.name,
                studentEntity.grade,
                studentEntity.classNumber
        );
    }

    private List<TeamWithMembersResponseDto> groupResultsByTeam(List<Tuple> queryResults) {
        Map<Long, List<TeamMemberDto>> teamMembersMap = new LinkedHashMap<>();
        Map<Long, String> teamNamesMap = new LinkedHashMap<>();

        for (Tuple tuple : queryResults) {
            Long teamId = tuple.get(teamEntity.id);
            String teamName = tuple.get(teamEntity.name);
            TeamMemberDto member = tuple.get(2, TeamMemberDto.class);

            teamNamesMap.putIfAbsent(teamId, teamName);
            
            if (member != null) {
                teamMembersMap.computeIfAbsent(teamId, id -> new ArrayList<>()).add(member);
            }
        }

        return teamNamesMap.entrySet().stream()
                .map(entry -> {
                    Long teamId = entry.getKey();
                    String teamName = entry.getValue();
                    List<TeamMemberDto> members = teamMembersMap.getOrDefault(teamId, new ArrayList<>());
                    return new TeamWithMembersResponseDto(teamId, teamName, members);
                })
                .toList();
    }
}
