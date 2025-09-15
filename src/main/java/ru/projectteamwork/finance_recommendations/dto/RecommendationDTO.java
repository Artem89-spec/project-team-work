package ru.projectteamwork.finance_recommendations.dto;

public class RecommendationDTO {
    private final String name;
    private final String id;
    private final String text;

    public RecommendationDTO(String name, String id, String text) {
        this.name = name;
        this.id = id;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
