package solvit.teachmon.domain.team.domain.repository.querydsl.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.team.domain.repository.querydsl.TeamQueryDslRepository;
import solvit.teachmon.domain.team.presentation.dto.response.TeamResponseDto;
import solvit.teachmon.domain.team.presentation.dto.response.QTeamResponseDto;

import java.util.List;

import static solvit.teachmon.domain.team.domain.entity.QTeamEntity.teamEntity;

@Repository
@RequiredArgsConstructor
public class TeamQueryDslRepositoryImpl implements TeamQueryDslRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<TeamResponseDto> findTeamsBySearchKeyword(String keyword) {
        return queryFactory.select(
                new QTeamResponseDto(
                        teamEntity.id,
                        teamEntity.name
                )
        )
        .from(teamEntity)
        .where(teamEntity.name.containsIgnoreCase(keyword))
        .fetch();
    }
}
