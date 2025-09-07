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
@RequestMapping("/api")
public class RecommendationsController {
    private final RecommendationsService recommendationsService;

    public RecommendationsController(RecommendationsService recommendationsService) {
        this.recommendationsService = recommendationsService;
    }

    @GetMapping("/recommendations/{userId}")
    public ResponseEntity<RecommendationsResponse> getRecommendations(@PathVariable String userId) {
        List<RecommendationDTO> recommendations = recommendationsService.getRecommendationsForUser(userId);
        RecommendationsResponse response = new RecommendationsResponse(userId, recommendations);
        return ResponseEntity.ok(response);
    }

}
