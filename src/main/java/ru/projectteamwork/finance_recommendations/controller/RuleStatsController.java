package ru.projectteamwork.finance_recommendations.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.projectteamwork.finance_recommendations.domain.DynamicRule;
import ru.projectteamwork.finance_recommendations.domain.DynamicRuleStat;
import ru.projectteamwork.finance_recommendations.domain.service.RuleStatService;
import ru.projectteamwork.finance_recommendations.repository.DynamicRuleStatRepository;
import ru.projectteamwork.finance_recommendations.domain.service.RuleService;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rule")
public class RuleStatsController {

    private final DynamicRuleStatRepository statRepository;
    private final RuleStatService ruleStatService;
    private final RuleService ruleService;

    public RuleStatsController(DynamicRuleStatRepository statRepository, RuleService ruleService, RuleStatService  ruleStatService) {
        this.statRepository = statRepository;
        this.ruleService = ruleService;
        this.ruleStatService = ruleStatService;

    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        List<DynamicRule> allRules = ruleService.findAllEntities();

        List<Map<String, Object>> stats = allRules.stream()
                .map(rule -> {
                    Long fireCount = ruleStatService.getFireCount(rule.getId());
                    return Map.<String, Object>of(
                            "rule_id", rule.getId().toString(),
                            "count", fireCount
                    );
                })
                .collect(Collectors.toList());

        return Map.of("stats", stats);
    }
}
