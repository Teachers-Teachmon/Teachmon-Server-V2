package solvit.teachmon.domain.team.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.team.domain.entity.TeamEntity;
import solvit.teachmon.domain.team.domain.repository.querydsl.TeamQueryDslRepository;

@Repository
public interface TeamRepository extends JpaRepository<TeamEntity, Long>, TeamQueryDslRepository {

}
