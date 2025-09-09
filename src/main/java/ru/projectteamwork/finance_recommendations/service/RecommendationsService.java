package ru.projectteamwork.finance_recommendations.service;

import ru.projectteamwork.finance_recommendations.dto.RecommendationDTO;

import java.util.List;

public interface RecommendationsService {
    List<RecommendationDTO> getRecommendationsForUser(String userId);
}
