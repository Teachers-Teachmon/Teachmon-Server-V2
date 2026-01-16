package solvit.teachmon.domain.branch.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;
import solvit.teachmon.domain.branch.domain.repository.BranchRepository;

@Service
@RequiredArgsConstructor
public class BranchService {
    private final BranchRepository branchRepository;

    public BranchEntity getBranch(Integer year, Integer branch) {
        return branchRepository.findByYearAndBranch(year, branch)
                .orElseThrow(() -> new IllegalArgumentException("해당 분기를 찾을 수 없습니다. 분기 설정을 먼저 해주세요"));
    }
}
