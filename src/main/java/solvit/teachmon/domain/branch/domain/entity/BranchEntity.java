package solvit.teachmon.domain.branch.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solvit.teachmon.global.entity.BaseEntity;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "branch")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BranchEntity extends BaseEntity {
    @Column(name = "start_day", nullable = false)
    private LocalDate startDay;

    @Column(name = "end_day", nullable = false)
    private LocalDate endDay;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "branch", nullable = false)
    private Integer branch;
}
