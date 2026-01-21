package solvit.teachmon.domain.team.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.team.exception.InvalidTeamInfoException;
import solvit.teachmon.global.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "team")
public class TeamEntity extends BaseEntity {
    private String name;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamParticipationEntity> teamParticipationList = new ArrayList<>();

    public void addStudent(StudentEntity student) {
        TeamParticipationEntity teamParticipation = TeamParticipationEntity.builder()
                .team(this)
                .student(student)
                .build();
        teamParticipationList.add(teamParticipation);
    }

    public void removeStudent(StudentEntity student) {
        TeamParticipationEntity found = teamParticipationList.stream()
                .filter(p -> p.getStudent().equals(student))
                .findFirst()
                .orElseThrow(() -> new InvalidTeamInfoException("팀에 참여하지 않은 학생입니다."));
        teamParticipationList.remove(found);
    }

    public void updateName(String name) {
        validateNameField(name);
        this.name = name;
    }

    public void removeAllStudents() {
        teamParticipationList.clear();
    }

    @Builder
    public TeamEntity(String name) {
        validateNameField(name);

        this.name = name;
        this.teamParticipationList = new ArrayList<>();
    }

    private void validateNameField(String name) {
        if(name == null) throw new InvalidTeamInfoException("이름은 비어 있을 수 없습니다.");
    }
}
