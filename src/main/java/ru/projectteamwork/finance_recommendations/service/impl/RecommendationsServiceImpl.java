package ru.projectteamwork.finance_recommendations.service.impl;

import org.springframework.stereotype.Service;
import ru.projectteamwork.finance_recommendations.dto.RecommendationDTO;
import ru.projectteamwork.finance_recommendations.rules.RecommendationsRuleSet;
import ru.projectteamwork.finance_recommendations.service.RecommendationsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecommendationsServiceImpl implements RecommendationsService {
    private final List<RecommendationsRuleSet> rules;

    public RecommendationsServiceImpl(List<RecommendationsRuleSet> rules) {
        this.rules = rules;
    }

    @Override
    public List<RecommendationDTO> getRecommendationsForUser(String userId) {
        List<RecommendationDTO> result = new ArrayList<>();
        for (RecommendationsRuleSet rule: rules) {
            Optional<RecommendationDTO> dto = rule.checkRule(userId);
            dto.ifPresent(result::add);
        }
        return result;
    }
}
