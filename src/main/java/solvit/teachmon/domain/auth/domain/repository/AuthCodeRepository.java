package solvit.teachmon.domain.auth.domain.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.auth.domain.entity.AuthCodeEntity;

@Repository
public interface AuthCodeRepository extends CrudRepository<AuthCodeEntity, String> {
}
