package solvit.teachmon.domain.branch.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import solvit.teachmon.domain.branch.application.service.BranchService;
import solvit.teachmon.domain.branch.presentation.dto.request.BranchRequestDto;
import solvit.teachmon.domain.branch.presentation.dto.response.BranchResponseDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/branch")
public class BranchController {
    private final BranchService branchService;

@GetMapping
public ResponseEntity<List<BranchResponseDto>> getBranches() {
    List<BranchResponseDto> branches = branchService.getAllBranchesByCurrentYear();
    return ResponseEntity.ok(branches);
}

@PostMapping
public ResponseEntity<Void> createOrUpdateBranch(@Valid @RequestBody BranchRequestDto requestDto) {
    branchService.createOrUpdateBranch(requestDto);
    return ResponseEntity.noContent().build();
}
}
