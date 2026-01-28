package solvit.teachmon.domain.team.domain.repository.querydsl.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.team.domain.entity.QTeamEntity;
import solvit.teachmon.domain.team.domain.repository.querydsl.TeamQueryDslRepository;
import solvit.teachmon.domain.team.presentation.dto.response.TeamResponseDto;
import solvit.teachmon.domain.team.presentation.dto.response.QTeamResponseDto;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TeamRepositoryImpl implements TeamQueryDslRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<TeamResponseDto> searchTeamByQuery(String query) {
        return queryFactory.select(
                new QTeamResponseDto(
                        QTeamEntity.teamEntity.id,
                        QTeamEntity.teamEntity.name
                )
        )
        .from(QTeamEntity.teamEntity)
        .where(QTeamEntity.teamEntity.name.containsIgnoreCase(query))
        .fetch();
    }
}
