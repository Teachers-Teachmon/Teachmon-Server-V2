package solvit.teachmon.domain.team.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.team.exception.InvalidTeamParticipationInfoException;
import solvit.teachmon.global.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "team_participation")
public class TeamParticipationEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private TeamEntity team;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private StudentEntity student;

    @Builder
    public TeamParticipationEntity(TeamEntity team, StudentEntity student) {
        validateTeamField(team);
        validateStudentField(student);

        this.team = team;
        this.student = student;
    }

    private void validateTeamField(TeamEntity team) {
        if (team == null)
            throw new InvalidTeamParticipationInfoException("팀은 비어 있을 수 없습니다.");
    }

    private void validateStudentField(StudentEntity student) {
        if(student == null)
            throw new InvalidTeamParticipationInfoException("학생은 비어 있을 수 없습니다.");
    }
}
