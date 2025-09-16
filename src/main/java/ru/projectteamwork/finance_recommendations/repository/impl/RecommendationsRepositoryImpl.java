package ru.projectteamwork.finance_recommendations.repository.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.projectteamwork.finance_recommendations.exception.DataAccessLayerException;
import ru.projectteamwork.finance_recommendations.repository.RecommendationsRepository;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
public class RecommendationsRepositoryImpl implements RecommendationsRepository {
    private final JdbcTemplate jdbcTemplate;
    private final Logger logger = LoggerFactory.getLogger(RecommendationsRepositoryImpl.class);

    private final Cache<String, Integer> sumCache =
            Caffeine.newBuilder().maximumSize(10_000).expireAfterWrite(10, TimeUnit.MINUTES).build();
    private final Cache<String, Boolean> existsCache =
            Caffeine.newBuilder().maximumSize(10_000).expireAfterWrite(10, TimeUnit.MINUTES).build();
    private final Cache<String, Integer> countCache =
            Caffeine.newBuilder().maximumSize(10_000).expireAfterWrite(10, TimeUnit.MINUTES).build();

    public RecommendationsRepositoryImpl(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int sumAmountByProductAndTxType(UUID userId, String productType, String txType) {
        final String key = (userId + "|" + productType + "|" + txType).toUpperCase();
        try {
            return sumCache.get(key, k -> {
                final String sql =
                        "SELECT COALESCE(SUM(t.amount), 0) " +
                                "FROM transactions t INNER JOIN products p ON t.product_id = p.id " +
                                "WHERE t.user_id = ? AND UPPER(t.type) = UPPER(?) AND UPPER(p.type) = UPPER(?)";
                Integer sum = jdbcTemplate.queryForObject(sql, Integer.class, userId, txType, productType);
                return sum != null ? sum : 0;
            });
        } catch (DataAccessException e) {
            logger.error("Ошибка БД при суммировании: userId={}, productType={}, txType={}", userId, productType, txType);
            throw new DataAccessLayerException("Ошибка суммирования транзакций", e);
        }
    }

    @Override
    public boolean existsTransactionsByProductType(UUID userId, String productType) {
        final String key = (userId + "|" + productType).toUpperCase();
        try {
            return existsCache.get(key, k -> {
                final String sql =
                        "SELECT EXISTS ( " +
                                "  SELECT 1 FROM transactions t " +
                                "  INNER JOIN products p ON t.product_id = p.id " +
                                "  WHERE t.user_id = ? AND UPPER(p.type) = UPPER(?) " +
                                ")";
                Boolean exists = jdbcTemplate.queryForObject(sql, Boolean.class, userId, productType);
                return exists != null && exists;
            });
        } catch (DataAccessException e) {
            logger.error("Ошибка БД при exists: userId={}, productType={}", userId, productType);
            throw new DataAccessLayerException("Ошибка проверки наличия транзакций", e);
        }
    }

    @Override
    public int countTransactionsByProductType(UUID userId, String productType) {
        final String key = (userId + "|" + productType).toUpperCase();
        try {
            return countCache.get(key, k -> {
                final String sql =
                        "SELECT COUNT(*) " +
                                "FROM transactions t INNER JOIN products p ON t.product_id = p.id " +
                                "WHERE t.user_id = ? AND UPPER(p.type) = UPPER(?)";
                Integer cnt = jdbcTemplate.queryForObject(sql, Integer.class, userId, productType);
                return cnt != null ? cnt : 0;
            });
        } catch (DataAccessException e) {
            logger.error("Ошибка БД при count: userId={}, productType={}", userId, productType);
            throw new DataAccessLayerException("Ошибка подсчёта транзакций", e);
        }
    }

    @Override
    public Integer getSumIncomesByProductType(UUID userId, String productType) {
        return sumAmountByProductAndTxType(userId, productType, "DEPOSIT");
    }

    @Override
    public Integer getSumExpensesByProductType(UUID userId, String productType) {
        return sumAmountByProductAndTxType(userId, productType, "WITHDRAW");
    }

    @Override
    public boolean userHasProductType(UUID userId, String productType) {
        return existsTransactionsByProductType(userId, productType);
    }
}

