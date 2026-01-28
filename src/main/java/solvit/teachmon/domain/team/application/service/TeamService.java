package solvit.teachmon.domain.team.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.management.student.domain.repository.StudentRepository;
import solvit.teachmon.domain.management.student.exception.StudentNotFoundException;
import solvit.teachmon.domain.team.domain.entity.TeamEntity;
import solvit.teachmon.domain.team.domain.repository.TeamRepository;
import solvit.teachmon.domain.team.exception.TeamNotFoundException;
import solvit.teachmon.domain.team.presentation.dto.request.TeamCreateRequestDto;
import solvit.teachmon.domain.team.presentation.dto.request.TeamDeleteRequestDto;
import solvit.teachmon.domain.team.presentation.dto.request.TeamUpdateRequestDto;
import solvit.teachmon.domain.team.presentation.dto.request.TeamUpdateStudentDto;
import solvit.teachmon.domain.team.presentation.dto.response.TeamResponseDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final StudentRepository studentRepository;

    public List<TeamResponseDto> searchTeamByQuery(String query) {
        return teamRepository.findTeamsBySearchKeyword(query);
    }

    @Transactional
    public void createTeam(TeamCreateRequestDto requestDto) {
        List<StudentEntity> students = validateAndGetStudents(requestDto.students());
        
        TeamEntity team = TeamEntity.builder()
                .name(requestDto.name())
                .build();
        
        TeamEntity savedTeam = teamRepository.save(team);
        addStudentsToTeam(savedTeam, students);
    }

    @Transactional
    public void updateTeam(TeamUpdateRequestDto requestDto) {
        TeamEntity team = teamRepository.findById(requestDto.id())
                .orElseThrow(TeamNotFoundException::new);
        
        team.updateName(requestDto.name());
        
        List<Long> studentIds = extractStudentIds(requestDto.students());
        List<StudentEntity> students = validateAndGetStudents(studentIds);
        
        team.removeAllStudents();
        addStudentsToTeam(team, students);
    }

    private List<StudentEntity> validateAndGetStudents(List<Long> studentIds) {
        List<StudentEntity> students = studentRepository.findAllById(studentIds);
        if (students.size() != studentIds.size()) {
            throw new StudentNotFoundException();
        }
        return students;
    }

    private void addStudentsToTeam(TeamEntity team, List<StudentEntity> students) {
        students.forEach(team::addStudent);
    }

    @Transactional
    public void deleteTeam(TeamDeleteRequestDto requestDto) {
        TeamEntity team = teamRepository.findById(requestDto.id())
                .orElseThrow(TeamNotFoundException::new);
        
        teamRepository.delete(team);
    }

    private List<Long> extractStudentIds(List<TeamUpdateStudentDto> students) {
        return students.stream()
                .map(TeamUpdateStudentDto::id)
                .toList();
    }
}
