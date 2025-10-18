package ru.projectteamwork.finance_recommendations.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "dynamic_rule_stat")
public class DynamicRuleStat {

    @Id
    @Column(name = "rule_id", nullable = false)
    private UUID ruleId;

    @Column(name = "fire_count", nullable = false)
    private long fireCount = 0L;

    public DynamicRuleStat() {}

    public DynamicRuleStat(UUID ruleId) {
        this.ruleId = ruleId;
        this.fireCount = 0L;
    }

    public UUID getRuleId() {
        return ruleId;
    }

    public void setRuleId(UUID ruleId) {
        this.ruleId = ruleId;
    }

    public long getFireCount() {
        return fireCount;
    }

    public void setFireCount(long fireCount) {
        this.fireCount = fireCount;
    }

    public void inc() {
        this.fireCount++;
    }
}
