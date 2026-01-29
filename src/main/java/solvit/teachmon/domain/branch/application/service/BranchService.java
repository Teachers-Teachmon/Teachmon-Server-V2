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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        
        Map<Integer, BranchEntity> existingBranchMap = findExistingBranchesAsMap(currentYear);
        
        BranchEntity branchToSave = determineCreateOrUpdate(requestDto, existingBranchMap, currentYear);
        
        branchRepository.saveAll(List.of(branchToSave));
    }

    private Map<Integer, BranchEntity> findExistingBranchesAsMap(int currentYear) {
        return branchRepository.findByYearOrderByBranch(currentYear).stream()
                .collect(Collectors.toMap(BranchEntity::getBranch, Function.identity()));
    }

    private BranchEntity determineCreateOrUpdate(BranchRequestDto requestDto, Map<Integer, BranchEntity> branchMap, int currentYear) {
        final BranchEntity existingBranch = branchMap.get(requestDto.getNumber());
        
        return existingBranch != null 
            ? updateExistingBranch(existingBranch, requestDto)
            : createNewBranch(requestDto, currentYear);
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