package ru.projectteamwork.finance_recommendations.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.projectteamwork.finance_recommendations.exception.DataAccessLayerException;
import ru.projectteamwork.finance_recommendations.repository.RecommendationsRepository;

import java.util.UUID;

@Repository
public class RecommendationsRepositoryImpl implements RecommendationsRepository {
    private final JdbcTemplate jdbcTemplate;
    private final Logger logger = LoggerFactory.getLogger(RecommendationsRepositoryImpl.class);

    public RecommendationsRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Integer getSumIncomesByProductType(UUID userId, String productType) {
        String sql = "SELECT COALESCE(SUM(amount), 0) " +
                "FROM transactions t INNER JOIN products p ON t.product_id = p.id " +
                "WHERE t.user_id = ? AND t.type = 'DEPOSIT' AND p.type = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, userId, productType);
        } catch (DataAccessException e) {
            logger.error("Возникла ошибка при обращении к базе данных для получения суммы дохода " +
                    "с данными для userId={} и ProductType={}", userId, productType);
            throw new DataAccessLayerException("Ошибка получения суммы дохода при обращении к базе данных", e);
        }
    }

    @Override
    public Integer getSumExpensesByProductType(UUID userId, String productType) {
        String sql = "SELECT COALESCE(SUM(amount), 0) " +
                "FROM transactions t INNER JOIN products p ON t.product_id = p.id " +
                "WHERE t.user_id = ? AND t.type = 'WITHDRAW' AND p.type = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, userId, productType);
        } catch (DataAccessException e) {
            logger.error("Возникла ошибка при обращении к базе данных для получения суммы расхода " +
                    "с данными userId={} и productType={}", userId, productType);
            throw  new DataAccessLayerException("Ошибка получения суммы расходов при обращении к базе данных", e);
        }
    }


    @Override
    public boolean userHasProductType(UUID userId, String productType) {
        String sql = "SELECT EXISTS (SELECT 1 " +
                "FROM transactions t  INNER JOIN products p ON t.product_id = p.id " +
                "WHERE t.user_id = ? AND p.type = ?)";
        try {
            Boolean exists = jdbcTemplate.queryForObject(sql, Boolean.class, userId, productType);
            return exists != null ? exists : false;
        } catch (DataAccessException e) {
            logger.error("Возникла ошибка при обращении к базе данных для получения логического значения " +
                    "с данными userId={} и productType={}", userId, productType);
            throw new DataAccessLayerException("Ошибка получения логического значения при обращении к базе данных", e);
        }
    }
}
