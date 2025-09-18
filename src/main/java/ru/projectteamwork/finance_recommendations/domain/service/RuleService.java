package ru.projectteamwork.finance_recommendations.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.projectteamwork.finance_recommendations.api.RuleRequest;
import ru.projectteamwork.finance_recommendations.api.RuleResponse;
import ru.projectteamwork.finance_recommendations.domain.DynamicRule;
import ru.projectteamwork.finance_recommendations.domain.repo.DynamicRuleRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RuleService {

    private final DynamicRuleRepository repo;

    public RuleService(DynamicRuleRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public RuleResponse create(RuleRequest req) {
        DynamicRule saved = repo.save(RuleMapper.toEntity(req));
        return RuleMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<RuleResponse> list() {
        return repo.findAll().stream().map(RuleMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void deleteByProductId(UUID productId) {
        repo.deleteByProductId(productId);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public java.util.List<DynamicRule> findAllEntities() {
        return repo.findAll();
    }
}