package solvit.teachmon.domain.place.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import solvit.teachmon.domain.place.domain.repository.PlaceRepository;
import solvit.teachmon.domain.place.presentation.dto.response.PlaceSearchResponseDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchPlaceService {
    private final PlaceRepository placeRepository;

    public List<PlaceSearchResponseDto> searchPlaceByQuery(String query) {
        return placeRepository.searchPlacesByKeyword(query);
    }
}