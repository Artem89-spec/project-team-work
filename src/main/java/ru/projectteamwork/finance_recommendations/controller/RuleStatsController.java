package ru.projectteamwork.finance_recommendations.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.projectteamwork.finance_recommendations.domain.DynamicRule;
import ru.projectteamwork.finance_recommendations.domain.DynamicRuleStat;
import ru.projectteamwork.finance_recommendations.repository.DynamicRuleStatRepository;
import ru.projectteamwork.finance_recommendations.domain.service.RuleService;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rule")
public class RuleStatsController {

    private final DynamicRuleStatRepository statRepository;
    private final RuleService ruleService;

    public RuleStatsController(DynamicRuleStatRepository statRepository, RuleService ruleService) {
        this.statRepository = statRepository;
        this.ruleService = ruleService;
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        List<DynamicRule> allRules = ruleService.findAllEntities();
        Map<UUID, Long> statMap = statRepository.findAll().stream()
                .collect(Collectors.toMap(DynamicRuleStat::getRuleId, DynamicRuleStat::getFireCount));

        List<Map<String, Object>> stats = allRules.stream()
                .map(rule -> Map.<String, Object>of(
                        "rule_id", rule.getId().toString(),
                        "count", statMap.getOrDefault(rule.getId(), 0L)
                ))
                .collect(Collectors.toList());

        return Map.of("stats", stats);
    }
}
