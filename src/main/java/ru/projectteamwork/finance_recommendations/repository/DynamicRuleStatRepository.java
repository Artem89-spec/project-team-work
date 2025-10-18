package ru.projectteamwork.finance_recommendations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.projectteamwork.finance_recommendations.domain.DynamicRuleStat;

import java.util.UUID;

public interface DynamicRuleStatRepository extends JpaRepository<DynamicRuleStat, UUID> {
}