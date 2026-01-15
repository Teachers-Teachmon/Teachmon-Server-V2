package solvit.teachmon.domain.oauth2.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.oauth2.domain.entity.TokenEntity;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, String> {
}
