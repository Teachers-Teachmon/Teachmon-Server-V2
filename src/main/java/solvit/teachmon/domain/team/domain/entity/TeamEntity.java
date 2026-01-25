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

/**
 * 팀(학생 그룹) 정보를 관리하는 엔티티
 * Entity for managing teams (student groups)
 * 
 * <p>팀은 여러 학생을 그룹으로 묶어 관리하는 기능을 제공합니다.
 * Teams provide functionality to group and manage multiple students together.
 * 
 * <p>학생과의 관계:
 * Relationship with students:
 * <ul>
 *   <li>다대다(Many-to-Many) 관계를 TeamParticipation 조인 테이블로 구현 (Many-to-many relationship implemented via TeamParticipation join table)</li>
 *   <li>양방향 관계로 팀에서 학생 접근, 학생에서 팀 접근 가능 (Bidirectional relationship allows access from both sides)</li>
 *   <li>orphanRemoval=true: 팀-학생 관계가 끊어지면 TeamParticipation 자동 삭제 (orphanRemoval=true auto-deletes TeamParticipation when relationship breaks)</li>
 *   <li>CascadeType.ALL: 팀 삭제 시 모든 TeamParticipation도 삭제 (CascadeType.ALL deletes all TeamParticipations when team is deleted)</li>
 * </ul>
 * 
 * <p>중요 메서드:
 * Important methods:
 * <ul>
 *   <li>addStudent: 학생을 팀에 추가 (양방향 관계 설정) (Adds student to team with bidirectional relationship)</li>
 *   <li>removeStudent: 학생을 팀에서 제거 (특정 학생만 제거) (Removes specific student from team)</li>
 *   <li>removeAllStudents: 모든 학생을 팀에서 제거 (팀원 전체 교체 시 사용) (Removes all students, used for full member replacement)</li>
 * </ul>
 * 
 * @see TeamParticipationEntity
 * @see StudentEntity
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "team")
public class TeamEntity extends BaseEntity {
    /** 팀 이름 (Team name) */
    private String name;

    /**
     * 팀 참여 정보 목록 (학생과의 다대다 관계를 위한 조인 테이블)
     * List of team participations (join table for many-to-many relationship with students)
     * 
     * <p>orphanRemoval=true: 리스트에서 제거된 TeamParticipation은 자동으로 DB에서 삭제됨
     * orphanRemoval=true: TeamParticipations removed from list are automatically deleted from DB
     * 
     * <p>CascadeType.ALL: 팀의 모든 변경사항이 TeamParticipation에 전파됨
     * CascadeType.ALL: All changes to team are cascaded to TeamParticipations
     */
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamParticipationEntity> teamParticipationList = new ArrayList<>();

    /**
     * 팀에 학생 추가
     * Adds a student to the team
     * 
     * <p>양방향 관계를 올바르게 설정하기 위해 TeamParticipation 엔티티를 생성합니다.
     * Creates TeamParticipation entity to properly establish bidirectional relationship.
     * 
     * <p>이 메서드는 TeamParticipation 생성자에서 자동으로 양방향 관계를 설정합니다:
     * This method automatically sets up bidirectional relationship in TeamParticipation constructor:
     * <ul>
     *   <li>team.teamParticipationList에 추가 (Adds to team.teamParticipationList)</li>
     *   <li>student.teamParticipationList에도 추가 (Also adds to student.teamParticipationList)</li>
     * </ul>
     * 
     * @param student 추가할 학생 엔티티 (Student entity to add)
     */
    public void addStudent(StudentEntity student) {
        TeamParticipationEntity teamParticipation = TeamParticipationEntity.builder()
                .team(this)
                .student(student)
                .build();
        teamParticipationList.add(teamParticipation);
    }

    /**
     * 팀에서 특정 학생 제거
     * Removes a specific student from the team
     * 
     * <p>orphanRemoval=true 설정으로 인해 리스트에서 제거하면 DB에서도 자동 삭제됩니다.
     * Due to orphanRemoval=true, removing from list automatically deletes from DB.
     * 
     * @param student 제거할 학생 엔티티 (Student entity to remove)
     * @throws InvalidTeamInfoException 학생이 이 팀에 참여하지 않은 경우 (If student is not a member of this team)
     */
    public void removeStudent(StudentEntity student) {
        TeamParticipationEntity found = teamParticipationList.stream()
                .filter(p -> p.getStudent().equals(student))
                .findFirst()
                .orElseThrow(() -> new InvalidTeamInfoException("팀에 참여하지 않은 학생입니다."));
        teamParticipationList.remove(found);
    }

    /**
     * 팀 이름 수정
     * Updates team name
     * 
     * @param name 새 팀 이름 (New team name)
     * @throws InvalidTeamInfoException 이름이 null인 경우 (If name is null)
     */
    public void updateName(String name) {
        validateName(name);
        this.name = name;
    }

    /**
     * 팀의 모든 학생 제거
     * Removes all students from the team
     * 
     * <p>팀원 전체를 교체할 때 사용됩니다 (TeamService.updateTeam에서 사용).
     * Used when replacing all team members (used in TeamService.updateTeam).
     * 
     * <p>orphanRemoval=true로 인해 clear() 호출 시 모든 TeamParticipation이 DB에서 삭제됩니다.
     * Due to orphanRemoval=true, clear() deletes all TeamParticipations from DB.
     */
    public void removeAllStudents() {
        teamParticipationList.clear();
    }

    /**
     * 팀 생성자
     * Team constructor
     * 
     * @param name 팀 이름 (Team name, must not be null)
     * @throws InvalidTeamInfoException 이름이 null인 경우 (If name is null)
     */
    @Builder
    public TeamEntity(String name) {
        validateName(name);

        this.name = name;
        this.teamParticipationList = new ArrayList<>();
    }

    /**
     * 팀 이름 유효성 검증
     * Validates team name
     * 
     * @param name 검증할 팀 이름 (Team name to validate)
     * @throws InvalidTeamInfoException 이름이 null인 경우 (If name is null)
     */
    private void validateName(String name) {
        if(name == null) throw new InvalidTeamInfoException("이름은 비어 있을 수 없습니다.");
    }
}
