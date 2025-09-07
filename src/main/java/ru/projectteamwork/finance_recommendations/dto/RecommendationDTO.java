package ru.projectteamwork.finance_recommendations.dto;

public class RecommendationDTO {
    private final String id;
    private final String name;
    private final String text;

    public RecommendationDTO(String id, String name, String text) {
        this.id = id;
        this.name = name;
        this.text = text;
    }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getText() { return text; }
}
