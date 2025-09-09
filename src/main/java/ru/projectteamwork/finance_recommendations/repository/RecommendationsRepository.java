package ru.projectteamwork.finance_recommendations.repository;

import java.util.UUID;

public interface RecommendationsRepository {
    Integer getSumIncomesByProductType(UUID userId, String productType);

    Integer getSumExpensesByProductType(UUID userId, String productType);

    boolean userHasProductType(UUID userId, String productType);

}
