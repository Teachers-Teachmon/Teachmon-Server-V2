package solvit.teachmon.domain.team.domain.repository.querydsl.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.team.domain.repository.querydsl.TeamQueryDslRepository;
import solvit.teachmon.domain.team.presentation.dto.response.TeamResponseDto;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TeamQueryDslRepositoryImpl implements TeamQueryDslRepository {

    @Override
    public List<TeamResponseDto> findTeamsBySearchKeyword(String keyword) {
        // TODO: QueryDSL Q클래스 생성 후 구현
        return List.of();
    }
}
