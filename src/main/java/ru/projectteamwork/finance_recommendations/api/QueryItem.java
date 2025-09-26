package ru.projectteamwork.finance_recommendations.api;

import ru.projectteamwork.finance_recommendations.domain.enums.QueryType;

import java.util.List;

public record QueryItem(
        QueryType query,
        List<String> arguments,
        boolean negate
) {}