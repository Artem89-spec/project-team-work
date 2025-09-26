package ru.projectteamwork.finance_recommendations.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.projectteamwork.finance_recommendations.dto.RecommendationDTO;
import ru.projectteamwork.finance_recommendations.dto.RecommendationsResponse;
import ru.projectteamwork.finance_recommendations.service.RecommendationsService;

import java.util.List;

@RestController
@RequestMapping("api")
public class DynamicRecommendationsController {
    private final RecommendationsService recommendationsService;

    public DynamicRecommendationsController(RecommendationsService recommendationsService) {
        this.recommendationsService = recommendationsService;
    }

    @GetMapping("/recommendations/dynamic/{userId}")
    public ResponseEntity<RecommendationsResponse> getDynamicRecommendations(@PathVariable String userId) {
        List<RecommendationDTO> recommendations = recommendationsService.getRecommendationsForUser(userId);
        RecommendationsResponse response = new RecommendationsResponse(userId, recommendations);
        return ResponseEntity.ok(response);
    }

}
