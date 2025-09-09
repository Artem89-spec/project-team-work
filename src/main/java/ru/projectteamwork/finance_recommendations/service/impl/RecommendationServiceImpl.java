package ru.projectteamwork.finance_recommendations.service.impl;

import org.springframework.stereotype.Service;
import ru.projectteamwork.finance_recommendations.dto.RecommendationDTO;
import ru.projectteamwork.finance_recommendations.rules.RecommendationRuleSet;
import ru.projectteamwork.finance_recommendations.service.RecommendationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final List<RecommendationRuleSet> rules;

    public RecommendationServiceImpl(List<RecommendationRuleSet> rules) {
        this.rules = rules;
    }

    @Override
    public List<RecommendationDTO> getRecommendationsForUser(String userId) {
        List<RecommendationDTO> result = new ArrayList<>();
        for (RecommendationRuleSet rule : rules) {
            Optional<RecommendationDTO> dto = rule.checkRule(userId);
            dto.ifPresent(result::add);
        }
        return result;
    }
}
