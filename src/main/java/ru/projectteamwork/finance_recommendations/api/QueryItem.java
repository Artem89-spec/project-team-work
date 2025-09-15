package ru.projectteamwork.finance_recommendations.api;

import java.util.List;

public record QueryItem(
        String query,
        List<String> arguments,
        boolean negate
) {}