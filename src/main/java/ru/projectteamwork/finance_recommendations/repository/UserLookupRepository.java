package ru.projectteamwork.finance_recommendations.repository;

import java.util.Optional;
import java.util.UUID;

public interface UserLookupRepository {
    Optional<UUID> findSingleUserIdByFullName(String fullName);
}
