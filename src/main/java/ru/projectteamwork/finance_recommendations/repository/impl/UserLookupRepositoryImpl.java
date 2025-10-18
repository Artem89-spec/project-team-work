package ru.projectteamwork.finance_recommendations.repository.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.projectteamwork.finance_recommendations.repository.UserLookupRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserLookupRepositoryImpl implements UserLookupRepository {

    private final JdbcTemplate jdbc;

    public UserLookupRepositoryImpl(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    @Cacheable(cacheNames = "userIdCache", key = "#fullName")
    public Optional<UUID> findSingleUserIdByFullName(String fullName) {
        String sql = """
            SELECT u.id
            FROM users u
            WHERE UPPER(u.first_name) = UPPER(SPLIT_PART(?, ' ', 1))
              AND UPPER(u.last_name)  = UPPER(SPLIT_PART(?, ' ', 2))
        """;
        List<UUID> ids = jdbc.query(sql, (rs, i) -> UUID.fromString(rs.getString(1)), fullName.trim());
        return ids.size() == 1 ? Optional.of(ids.get(0)) : Optional.empty();
    }
}