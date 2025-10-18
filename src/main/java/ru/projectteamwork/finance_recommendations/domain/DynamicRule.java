package ru.projectteamwork.finance_recommendations.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "dynamic_rule")
public class DynamicRule {

    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "UUID", nullable = false)
    private UUID id;

    @Column(name = "product_id", columnDefinition = "UUID", nullable = false)
    private UUID productId;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "product_text", nullable = false, length = 2000)
    private String productText;

    @OneToMany(
            mappedBy = "rule",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @OrderBy("position ASC")
    private List<DynamicRuleQuery> queries = new ArrayList<>();

    public void addQuery(DynamicRuleQuery q) {
        q.setRule(this);
        q.setPosition(queries.size());
        queries.add(q);
    }
    public void setQueries(List<DynamicRuleQuery> list) {
        this.queries.clear();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                DynamicRuleQuery q = list.get(i);
                q.setRule(this);
                q.setPosition(i);
                this.queries.add(q);
            }
        }
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProductId() {
        return productId;
    }
    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductText() {
        return productText;
    }
    public void setProductText(String productText) {
        this.productText = productText;
    }

    public List<DynamicRuleQuery> getQueries() {
        return queries;
    }
}