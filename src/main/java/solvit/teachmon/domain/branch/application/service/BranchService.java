package solvit.teachmon.domain.branch.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;
import solvit.teachmon.domain.branch.domain.repository.BranchRepository;
import solvit.teachmon.domain.branch.presentation.dto.request.BranchRequestDto;
import solvit.teachmon.domain.branch.presentation.dto.response.BranchResponseDto;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BranchService {
    private final BranchRepository branchRepository;

    public List<BranchResponseDto> getAllBranchesByCurrentYear() {
        int currentYear = LocalDate.now().getYear();
        
        return branchRepository.findByYearOrderByBranch(currentYear).stream()
                .map(branch -> new BranchResponseDto(
                        branch.getBranch(),
                        branch.getStartDay(),
                        branch.getEndDay()
                ))
                .toList();
    }

    @Transactional
    public void createOrUpdateBranch(BranchRequestDto requestDto) {
        int currentYear = LocalDate.now().getYear();
        
        BranchEntity branchToSave = branchRepository.findByYearAndBranch(currentYear, requestDto.getNumber())
                .map(branch -> updateExistingBranch(branch, requestDto))
                .orElseGet(() -> createNewBranch(requestDto, currentYear));

        branchRepository.save(branchToSave);
    }

    private BranchEntity updateExistingBranch(BranchEntity branch, BranchRequestDto requestDto) {
        branch.updateBranch(requestDto.getStartDay(), requestDto.getEndDay());
        return branch;
    }

    private BranchEntity createNewBranch(BranchRequestDto requestDto, int currentYear) {
        return BranchEntity.builder()
                .branch(requestDto.getNumber())
                .startDay(requestDto.getStartDay())
                .endDay(requestDto.getEndDay())
                .year(currentYear)
                .build();
    }
}
