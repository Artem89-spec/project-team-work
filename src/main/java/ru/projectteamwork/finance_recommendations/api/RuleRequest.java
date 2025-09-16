package ru.projectteamwork.finance_recommendations.api;

import java.util.List;
import java.util.UUID;

public record RuleRequest(
        String product_name,
        UUID product_id,
        String product_text,
        List<QueryItem> rule
) {}