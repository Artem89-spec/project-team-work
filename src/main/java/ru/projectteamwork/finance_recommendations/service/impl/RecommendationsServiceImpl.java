package ru.projectteamwork.finance_recommendations.service.impl;

import org.springframework.stereotype.Service;
import ru.projectteamwork.finance_recommendations.dto.RecommendationDTO;
import ru.projectteamwork.finance_recommendations.rules.RecommendationsRuleSet;
import ru.projectteamwork.finance_recommendations.service.RecommendationsService;
import ru.projectteamwork.finance_recommendations.domain.service.RuleService;
import ru.projectteamwork.finance_recommendations.evaluator.DynamicRuleEvaluator;
import ru.projectteamwork.finance_recommendations.repository.RecommendationsRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RecommendationsServiceImpl implements RecommendationsService {
    private final List<RecommendationsRuleSet> rules;

    private final RuleService ruleService;
    private final DynamicRuleEvaluator evaluator;

    public RecommendationsServiceImpl(List<RecommendationsRuleSet> rules,
                                      RuleService ruleService,
                                      RecommendationsRepository recommendationsRepository) {
        this.rules = rules;
        this.ruleService = ruleService;
        this.evaluator = new DynamicRuleEvaluator(recommendationsRepository);
    }

    @Override
    public List<RecommendationDTO> getRecommendationsForUser(String userId) {
        List<RecommendationDTO> staticRecommendations = new ArrayList<>();
        List<RecommendationDTO> dynamicRecommendations = new ArrayList<>();

        for (RecommendationsRuleSet rule : rules) {
            Optional<RecommendationDTO> recommendationDTO = rule.checkRule(userId);
            recommendationDTO.ifPresent(staticRecommendations::add);
        }

        ruleService.findAllEntities().forEach(dynamicRule -> {
            if (evaluator.evaluate(dynamicRule, userId)) {
                dynamicRecommendations.add(new RecommendationDTO(
                        dynamicRule.getProductName(),
                        dynamicRule.getProductId().toString(),
                        dynamicRule.getProductText()
                ));
            }
        });

        Set<String> recommendationsID = new HashSet<>();
        List<RecommendationDTO> combinedRecommendations = new ArrayList<>();

        for (RecommendationDTO recommendation : staticRecommendations) {
            if (recommendationsID.add(recommendation.getId())) {
                combinedRecommendations.add(recommendation);
            }
        }

        for (RecommendationDTO recommendation : dynamicRecommendations) {
            if (recommendationsID.add(recommendation.getId())) {
                combinedRecommendations.add(recommendation);
            }
        }
        return combinedRecommendations;
    }
}
