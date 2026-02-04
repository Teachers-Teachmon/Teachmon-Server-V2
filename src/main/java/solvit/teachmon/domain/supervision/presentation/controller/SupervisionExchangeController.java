package solvit.teachmon.domain.supervision.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import solvit.teachmon.domain.supervision.application.service.SupervisionExchangeService;
import solvit.teachmon.domain.supervision.presentation.dto.request.SupervisionExchangeAcceptRequestDto;
import solvit.teachmon.domain.supervision.presentation.dto.request.SupervisionExchangeRejectRequestDto;
import solvit.teachmon.domain.supervision.presentation.dto.request.SupervisionExchangeRequestDto;
import solvit.teachmon.domain.supervision.presentation.dto.response.SupervisionExchangeResponseDto;
import solvit.teachmon.global.security.user.TeachmonUserDetails;

import java.util.List;

@RestController
@RequestMapping("/supervision")
@RequiredArgsConstructor
public class SupervisionExchangeController {

    private final SupervisionExchangeService supervisionExchangeService;

    @GetMapping("/exchange")
    public ResponseEntity<List<SupervisionExchangeResponseDto>> getSupervisionExchanges() {
        List<SupervisionExchangeResponseDto> exchanges = supervisionExchangeService.getSupervisionExchanges();
        return ResponseEntity.ok(exchanges);
    }

    @PostMapping("/exchange")
    public ResponseEntity<Void> createSupervisionExchangeRequest(
            @Valid @RequestBody SupervisionExchangeRequestDto requestDto,
            @AuthenticationPrincipal TeachmonUserDetails userDetails) {
        supervisionExchangeService.createSupervisionExchangeRequest(requestDto, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/exchange/accept")
    public ResponseEntity<Void> acceptSupervisionExchange(
            @Valid @RequestBody SupervisionExchangeAcceptRequestDto requestDto,
            @AuthenticationPrincipal TeachmonUserDetails userDetails) {
        supervisionExchangeService.acceptSupervisionExchange(requestDto, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/exchange/reject")
    public ResponseEntity<Void> rejectSupervisionExchange(
            @Valid @RequestBody SupervisionExchangeRejectRequestDto requestDto,
            @AuthenticationPrincipal TeachmonUserDetails userDetails) {
        supervisionExchangeService.rejectSupervisionExchange(requestDto, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}