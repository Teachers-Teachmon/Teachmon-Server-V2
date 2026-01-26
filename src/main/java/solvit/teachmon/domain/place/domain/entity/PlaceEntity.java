package solvit.teachmon.domain.place.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solvit.teachmon.global.entity.BaseEntity;

@Getter
@Entity
@Table(name = "place")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceEntity extends BaseEntity {
    @Column(name = "floor", nullable = false)
    private Integer floor;

    @Column(name = "name", nullable = false)
    private String name;
}
