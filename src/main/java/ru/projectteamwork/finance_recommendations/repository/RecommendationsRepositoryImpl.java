package ru.projectteamwork.finance_recommendations.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class RecommendationsRepositoryImpl implements RecommendationsRepository {

    private final JdbcTemplate jdbcTemplate;

    public RecommendationsRepositoryImpl(@org.springframework.beans.factory.annotation.Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Integer getSumDepositsByProductType(UUID userId, String productType) {
        String sql = """            SELECT COALESCE(SUM(t.AMOUNT), 0)
            FROM TRANSACTIONS t
            JOIN PRODUCTS p ON p.ID = t.PRODUCT_ID
            WHERE t.USER_ID = ?
              AND UPPER(t.TYPE) = 'DEPOSIT'
              AND p.TYPE = ?
        """;
        Integer sum = jdbcTemplate.queryForObject(sql, Integer.class, userId.toString(), productType);
        return sum != null ? sum : 0;
    }

    @Override
    public Integer getSumExpensesByProductType(UUID userId, String productType) {
        String sql = """            SELECT COALESCE(SUM(t.AMOUNT), 0)
            FROM TRANSACTIONS t
            JOIN PRODUCTS p ON p.ID = t.PRODUCT_ID
            WHERE t.USER_ID = ?
              AND UPPER(t.TYPE) = 'EXPENSE'
              AND p.TYPE = ?
        """;
        Integer sum = jdbcTemplate.queryForObject(sql, Integer.class, userId.toString(), productType);
        return sum != null ? sum : 0;
    }

    @Override
    public Boolean usersHasProductType(UUID userId, String productType) {
        String sql = """            SELECT EXISTS (
                SELECT 1
                FROM TRANSACTIONS t
                JOIN PRODUCTS p ON p.ID = t.PRODUCT_ID
                WHERE t.USER_ID = ? AND p.TYPE = ?
            )
        """;
        Boolean exists = jdbcTemplate.queryForObject(sql, Boolean.class, userId.toString(), productType);
        return exists != null && exists;
    }
}
