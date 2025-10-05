package ru.projectteamwork.finance_recommendations.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "dynamic_rule_stat")
public class DynamicRuleStat {

    @Id
    @Column(name = "rule_id", nullable = false)
    private UUID ruleId;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "rule_id", nullable = false)
    private DynamicRule rule;

    @Column(name = "fire_count", nullable = false)
    private long fireCount = 0L;

    protected DynamicRuleStat() {
    }

    public DynamicRuleStat(DynamicRule rule) {
        this.rule = rule;
        this.ruleId = rule.getId();
    }

    public UUID getRuleId() {
        return ruleId;
    }

    public DynamicRule getRule() {
        return rule;
    }

    public void setRule(DynamicRule rule) {
        this.rule = rule;
        this.ruleId = rule != null ? rule.getId() : null;
    }

    public long getFireCount() {
        return fireCount;
    }

    public void setFireCount(long fireCount) {
        this.fireCount = fireCount;
    }

    public void increment() {
        this.fireCount++;
    }
}
