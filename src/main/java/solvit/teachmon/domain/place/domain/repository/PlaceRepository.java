package solvit.teachmon.domain.place.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;

public interface PlaceRepository extends JpaRepository<PlaceEntity, Long> {
}
