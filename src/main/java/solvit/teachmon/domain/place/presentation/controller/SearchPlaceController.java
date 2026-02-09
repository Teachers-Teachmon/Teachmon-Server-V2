package solvit.teachmon.domain.place.presentation.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import solvit.teachmon.domain.place.application.service.SearchPlaceService;
import solvit.teachmon.domain.place.presentation.dto.response.PlaceSearchResponseDto;

import java.util.List;

@Validated
@RestController
@RequestMapping("/place")
@RequiredArgsConstructor
public class SearchPlaceController {
    private final SearchPlaceService searchPlaceService;

    @GetMapping("/search")
    public ResponseEntity<List<PlaceSearchResponseDto>> searchPlace(@RequestParam @NotNull(message = "검색어는 필수입니다.") String query) {
        return ResponseEntity.ok(searchPlaceService.searchPlaceByQuery(query));
    }
}