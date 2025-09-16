package ru.projectteamwork.finance_recommendations.domain.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.projectteamwork.finance_recommendations.domain.DynamicRule;

import java.util.UUID;

public interface DynamicRuleRepository extends JpaRepository<DynamicRule, UUID> {
    void deleteByProductId(UUID productId);
}