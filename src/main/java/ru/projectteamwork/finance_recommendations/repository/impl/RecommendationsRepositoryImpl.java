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
    public Integer getSumIncomesByProductType(UUID userId, String productType) {
        final String key = (userId + " " + productType).toUpperCase();
        try {
            return sumCache.get(key, k -> {
                final String sql = "SELECT COALESCE(SUM(amount), 0) " +
                        "FROM transactions t INNER JOIN products p ON t.product_id = p.id " +
                        "WHERE t.user_id = ? AND t.type = 'DEPOSIT' AND p.type = ?";
                return jdbcTemplate.queryForObject(sql, Integer.class, userId, productType);
            });
        } catch (DataAccessException e) {
            logger.error("Возникла ошибка при обращении к базе данных для получения суммы дохода " +
                    "с данными для userId={} и ProductType={}", userId, productType);
            throw new DataAccessLayerException("Ошибка получения суммы дохода при обращении к базе данных", e);
        }
    }

    @Override
    public Integer getSumExpensesByProductType(UUID userId, String productType) {
        final String key = (userId + " " + productType).toUpperCase();
        try {
            return sumCache.get(key, k -> {
                final String sql = "SELECT COALESCE(SUM(amount), 0) " +
                        "FROM transactions t INNER JOIN products p ON t.product_id = p.id " +
                        "WHERE t.user_id = ? AND t.type = 'WITHDRAW' AND p.type = ?";
                return jdbcTemplate.queryForObject(sql, Integer.class, userId, productType);
            });
        } catch (DataAccessException e) {
            logger.error("Возникла ошибка при обращении к базе данных для получения суммы расхода " +
                    "с данными userId={} и productType={}", userId, productType);
            throw new DataAccessLayerException("Ошибка получения суммы расходов при обращении к базе данных", e);
        }
    }

    @Override
    public boolean userHasProductType(UUID userId, String productType) {
        final String key = (userId + " " + productType).toUpperCase();
        try {
            return existsCache.get(key, k -> {
                String sql = "SELECT EXISTS (SELECT 1 " +
                        "FROM transactions t  INNER JOIN products p ON t.product_id = p.id " +
                        "WHERE t.user_id = ? AND p.type = ?)";
                Boolean exists = jdbcTemplate.queryForObject(sql, Boolean.class, userId, productType);
                return exists != null ? exists : false;
            });
        } catch (DataAccessException e) {
            logger.error("Возникла ошибка при обращении к базе данных для получения логического значения " +
                    "с данными userId={} и productType={}", userId, productType);
            throw new DataAccessLayerException("Ошибка получения логического значения при обращении к базе данных", e);
        }
    }

    @Override
    public int countTransactionsByProductType(UUID userId, String productType) {
        final String key = (userId + "  " + productType).toUpperCase();
        try {
            return countCache.get(key, k -> {
                final String sql =
                        "SELECT COUNT(*) " +
                                "FROM transactions t INNER JOIN products p ON t.product_id = p.id " +
                                "WHERE t.user_id = ? AND p.type = ?)";
                Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, productType);
                return count != null ? count : 0;
            });
        } catch (DataAccessException e) {
            logger.error("Возникла ошибка при обращении к базе данных для подсчета транзакций: userId={}, productType={}", userId, productType);
            throw new DataAccessLayerException("Ошибка подсчёта транзакций", e);
        }
    }

    @Override
    public Integer sumAmountByProductAndTxType(UUID userId, String productType, String transactionsType) {
        final String key = (userId + " " + productType + " " + transactionsType).toUpperCase();
        try {
            return sumCache.get(key, k -> {
                final String sql = "SELECT COALESCE(SUM(amount), 0) " +
                        "FROM transactions t INNER JOIN products p ON t.product_id = p.id " +
                        "WHERE t.user_id = ? AND t.type = ? AND p.type = ?";
                return jdbcTemplate.queryForObject(sql, Integer.class, userId, transactionsType, productType);
            });
        } catch (DataAccessException e) {
            logger.error("Ошибка при получении суммы по userId={}, productType={}, transactionsType={}", userId, productType, transactionsType);
            throw new DataAccessLayerException("Ошибка получения суммы транзакций по типу", e);
        }
    }
}


