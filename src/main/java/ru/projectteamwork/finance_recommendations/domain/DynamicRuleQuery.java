package ru.projectteamwork.finance_recommendations.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "dynamic_rule_query")
public class DynamicRuleQuery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // BIGINT IDENTITY в Liquibase
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    private DynamicRule rule;

    @Column(name = "position", nullable = false)
    private Integer position;

    @Column(name = "query", nullable = false, length = 64)
    private String query;

    @Column(name = "arguments", nullable = false, length = 2000)
    private String argumentsJson;

    @Column(name = "negate", nullable = false)
    private boolean negate;

    public Long getId() { return id; }

    public DynamicRule getRule() { return rule; }
    public void setRule(DynamicRule rule) { this.rule = rule; }

    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public String getArgumentsJson() { return argumentsJson; }
    public void setArgumentsJson(String argumentsJson) { this.argumentsJson = argumentsJson; }

    public boolean isNegate() { return negate; }
    public void setNegate(boolean negate) { this.negate = negate; }
}