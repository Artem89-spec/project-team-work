package ru.projectteamwork.finance_recommendations.service.impl;

import org.springframework.stereotype.Service;
import ru.projectteamwork.finance_recommendations.dto.RecommendationDTO;
import ru.projectteamwork.finance_recommendations.rules.RecommendationsRuleSet;
import ru.projectteamwork.finance_recommendations.service.RecommendationsService;
import ru.projectteamwork.finance_recommendations.domain.service.RuleService;
import ru.projectteamwork.finance_recommendations.evaluator.DynamicRuleEvaluator;
import ru.projectteamwork.finance_recommendations.repository.RecommendationsRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecommendationsServiceImpl implements RecommendationsService {
    private final List<RecommendationsRuleSet> rules;

    private final RuleService ruleService;
    private final DynamicRuleEvaluator evaluator;

    public RecommendationsServiceImpl(
            List<RecommendationsRuleSet> rules,
            RuleService ruleService,
            RecommendationsRepository recommendationsRepository
    ) {
        this.rules = rules;
        this.ruleService = ruleService;
        this.evaluator = new DynamicRuleEvaluator(recommendationsRepository);
    }

    @Override
    public List<RecommendationDTO> getRecommendationsForUser(String userId) {
        List<RecommendationDTO> result = new ArrayList<>();

        for (RecommendationsRuleSet rule : rules) {
            Optional<RecommendationDTO> dto = rule.checkRule(userId);
            dto.ifPresent(result::add);
        }

        ruleService.findAllEntities().forEach(dr -> {
            if (evaluator.evaluate(dr, userId)) {
                result.add(new RecommendationDTO(
                        dr.getProductName(),
                        dr.getProductId().toString(),
                        dr.getProductText()
                ));
            }
        });

        return result;
    }
}
