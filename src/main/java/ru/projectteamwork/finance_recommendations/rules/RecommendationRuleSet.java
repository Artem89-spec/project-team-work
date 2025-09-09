package ru.projectteamwork.finance_recommendations.rules;

import ru.projectteamwork.finance_recommendations.dto.RecommendationDTO;
import java.util.Optional;

public interface RecommendationRuleSet {
    Optional<RecommendationDTO> checkRule(String userId);
}
