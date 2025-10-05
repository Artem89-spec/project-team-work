package ru.projectteamwork.finance_recommendations.domain.service;

import ru.projectteamwork.finance_recommendations.domain.DynamicRuleStat;
import ru.projectteamwork.finance_recommendations.domain.repo.DynamicRuleStatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class RuleStatService {

    private final DynamicRuleStatRepository statRepository;

    public RuleStatService(DynamicRuleStatRepository statRepository) {
        this.statRepository = statRepository;
    }

    @Transactional
    public void inc(UUID ruleId) {
        DynamicRuleStat stat = statRepository.findById(ruleId)
                .orElseGet(() -> {
                    DynamicRuleStat s = new DynamicRuleStat();
                    s.setRuleId(ruleId);
                    s.setFireCount(0L);
                    return s;
                });
        stat.setFireCount(stat.getFireCount() + 1);
        statRepository.save(stat);
    }
}