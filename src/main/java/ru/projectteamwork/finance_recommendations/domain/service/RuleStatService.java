package ru.projectteamwork.finance_recommendations.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import ru.projectteamwork.finance_recommendations.domain.DynamicRuleStat;
import ru.projectteamwork.finance_recommendations.repository.DynamicRuleStatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.projectteamwork.finance_recommendations.repository.impl.RecommendationsRepositoryImpl;

import java.util.UUID;

@Service
public class RuleStatService {

    private final DynamicRuleStatRepository statRepository;

    private final Logger logger = LoggerFactory.getLogger(RuleStatService.class);

    @Autowired
    private CacheManager cacheManager;

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

        Cache cache = cacheManager.getCache("ruleStatCache");
        if (cache != null) {
            cache.put(ruleId, stat.getFireCount());
        } else {
            logger.info("кеш не был найден");
        }
    }

    @Cacheable(value = "ruleStatCache", key = "#ruleId")
    public Long getFireCount(UUID ruleId) {
        return statRepository.findById(ruleId)
                .map(DynamicRuleStat::getFireCount)
                .orElse(0L);
    }
}