package ru.projectteamwork.finance_recommendations.repository;

import java.util.UUID;

public interface RecommendationsRepository {
    Integer getSumDepositsByProductType(UUID userId, String productType);
    Integer getSumExpensesByProductType(UUID userId, String productType);
    Boolean usersHasProductType(UUID userId, String productType);
}
