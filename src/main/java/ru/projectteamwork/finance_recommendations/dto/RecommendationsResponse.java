package ru.projectteamwork.finance_recommendations.dto;

import java.util.List;

public class RecommendationsResponse {
    private String user_id;
    private List<RecommendationDTO> recommendations;

    public RecommendationsResponse(String user_id, List<RecommendationDTO> recommendations) {
        this.user_id = user_id;
        this.recommendations = recommendations;
    }

    public String getUser_id() {
        return user_id;
    }

    public List<RecommendationDTO> getRecommendations() {
        return recommendations;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setRecommendations(List<RecommendationDTO> recommendations) {
        this.recommendations = recommendations;
    }
}
