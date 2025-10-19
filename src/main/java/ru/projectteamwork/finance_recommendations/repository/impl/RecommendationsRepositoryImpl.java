package ru.projectteamwork.finance_recommendations.repository.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.projectteamwork.finance_recommendations.exception.DataAccessLayerException;
import ru.projectteamwork.finance_recommendations.repository.RecommendationsRepository;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Реализация {@link RecommendationsRepository}, обеспечивающая доступ к финансовым данным пользователей
 * <p>
 * Данный класс отвечает за выполнение SQL-запросов к таблицам <b>transactions</b> и <b>products</b>
 * для получения агрегированной информации о доходах, расходах и транзакциях по типам продуктов
 * </p>
 *
 * <h3>Основные функции:</h3>
 * <ul>
 *   <li>Вычисление сумм доходов и расходов по продуктам пользователя;</li>
 *   <li>Проверка наличия у пользователя определённых типов продуктов;</li>
 *   <li>Подсчёт количества транзакций по продукту;</li>
 *   <li>Кэширование результатов для оптимизации производительности.</li>
 * </ul>
 *
 * <h3>Особенности реализации:</h3>
 * <ul>
 *   <li>Используется {@link JdbcTemplate} для безопасного выполнения SQL-запросов;</li>
 *   <li>Применяется библиотека {@link Caffeine} для локального кэширования часто запрашиваемых данных;</li>
 *   <li>Результаты также аннотируются с помощью {@link Cacheable} для интеграции со Spring Cache;</li>
 *   <li>Все ошибки работы с базой данных перехватываются и преобразуются в {@link DataAccessLayerException};</li>
 *   <li>Метод {@link #clearCaches()} позволяет вручную очистить все локальные кэши.</li>
 * </ul>
 *
 * <h3>Используемые таблицы:</h3>
 * <ul>
 *   <li><b>transactions</b> — хранит информацию о транзакциях пользователя;</li>
 *   <li><b>products</b> — содержит данные о типах финансовых продуктов.</li>
 * </ul>
 *
 * @see ru.projectteamwork.finance_recommendations.repository.RecommendationsRepository
 * @see org.springframework.jdbc.core.JdbcTemplate
 * @see com.github.benmanes.caffeine.cache.Cache
 */
@Repository
public class RecommendationsRepositoryImpl implements RecommendationsRepository {

    private final JdbcTemplate jdbcTemplate;
    private final Logger logger = LoggerFactory.getLogger(RecommendationsRepositoryImpl.class);

    /** Локальный кэш для сумм транзакций. */
    private final Cache<String, Integer> sumCache =
            Caffeine.newBuilder().maximumSize(10_000).expireAfterWrite(10, TimeUnit.MINUTES).build();

    /** Локальный кэш для проверки существования продуктов. */
    private final Cache<String, Boolean> existsCache =
            Caffeine.newBuilder().maximumSize(10_000).expireAfterWrite(10, TimeUnit.MINUTES).build();

    /** Локальный кэш для количества транзакций. */
    private final Cache<String, Integer> countCache =
            Caffeine.newBuilder().maximumSize(10_000).expireAfterWrite(10, TimeUnit.MINUTES).build();

    /**
     * Конструктор с внедрением {@link JdbcTemplate} для работы с базой данных.
     *
     * @param jdbcTemplate экземпляр {@link JdbcTemplate}, настроенный на источник данных рекомендаций
     */
    public RecommendationsRepositoryImpl(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Возвращает сумму транзакций по заданным параметрам пользователя, типа продукта и типа транзакции.
     * <p>
     * Данные извлекаются из таблицы <b>transactions</b> и объединяются с таблицей <b>products</b>.
     * </p>
     *
     * @param userId      уникальный идентификатор пользователя
     * @param productType тип продукта (например, "DEBIT", "SAVING", "INVEST")
     * @param txType      тип транзакции ("DEPOSIT" или "WITHDRAW")
     * @return сумма всех транзакций данного типа; 0, если данных нет
     * @throws DataAccessLayerException при ошибках доступа к базе данных
     */
    @Override
    @Cacheable(cacheNames = "recommendationsCache", key = "#userId + '_' + #productType + '_' + #txType")
    public int sumAmountByProductAndTxType(UUID userId, String productType, String txType) {
        final String key = (userId + " " + productType + " " + txType).toUpperCase();
        try {
            return sumCache.get(key, k -> {
                final String sql =
                        "SELECT COALESCE(SUM(t.amount), 0) " +
                                "FROM transactions t INNER JOIN products p ON t.product_id = p.id " +
                                "WHERE t.user_id = ? AND t.type = ? AND p.type = ?";
                Integer sum = jdbcTemplate.queryForObject(sql, Integer.class, userId, txType, productType);
                return sum != null ? sum : 0;
            });
        } catch (DataAccessException e) {
            logger.error("Ошибка БД при суммировании: userId={}, productType={}, txType={}", userId, productType, txType);
            throw new DataAccessLayerException("Ошибка суммирования транзакций", e);
        }
    }

    /**
     * Проверяет, имеет ли пользователь хотя бы один продукт заданного типа.
     *
     * @param userId      уникальный идентификатор пользователя
     * @param productType тип продукта (например, "CREDIT", "SAVING")
     * @return {@code true}, если продукт найден; {@code false}, если нет
     * @throws DataAccessLayerException при ошибках работы с базой данных
     */
    @Override
    @Cacheable(cacheNames = "recommendationsCache", key = "#userId + '_' + #productType")
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
            logger.error("Ошибка при получении логического значения: userId={}, productType={}", userId, productType);
            throw new DataAccessLayerException("Ошибка получения данных из базы", e);
        }
    }

    /**
     * Подсчитывает количество транзакций пользователя по указанному типу продукта.
     *
     * @param userId      уникальный идентификатор пользователя
     * @param productType тип продукта
     * @return количество транзакций
     * @throws DataAccessLayerException при ошибках работы с базой данных
     */
    @Override
    @Cacheable(cacheNames = "recommendationsCache", key = "#userId + '_' + #productType")
    public int countTransactionsByProductType(UUID userId, String productType) {
        final String key = (userId + " " + productType).toUpperCase();
        try {
            return countCache.get(key, k -> {
                final String sql =
                        "SELECT COUNT(*) " +
                                "FROM transactions t INNER JOIN products p ON t.product_id = p.id " +
                                "WHERE t.user_id = ? AND p.type = ?";
                Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, productType);
                return count != null ? count : 0;
            });
        } catch (DataAccessException e) {
            logger.error("Ошибка при подсчёте транзакций: userId={}, productType={}", userId, productType);
            throw new DataAccessLayerException("Ошибка подсчёта транзакций", e);
        }
    }

    /**
     * Возвращает сумму доходов пользователя по указанному типу продукта.
     *
     * @param userId      уникальный идентификатор пользователя
     * @param productType тип продукта
     * @return сумма доходов
     */
    @Override
    public Integer getSumIncomesByProductType(UUID userId, String productType) {
        return sumAmountByProductAndTxType(userId, productType, "DEPOSIT");
    }

    /**
     * Возвращает сумму расходов пользователя по указанному типу продукта.
     *
     * @param userId      уникальный идентификатор пользователя
     * @param productType тип продукта
     * @return сумма расходов
     */
    @Override
    public Integer getSumExpensesByProductType(UUID userId, String productType) {
        return sumAmountByProductAndTxType(userId, productType, "WITHDRAW");
    }

    /**
     * Очищает все внутренние кэши, используемые в репозитории.
     * <p>
     * Используется при необходимости сброса данных (например, при обновлении БД).
     * </p>
     */
    @Override
    public void clearCaches() {
        long s1 = sumCache.estimatedSize();
        long s2 = existsCache.estimatedSize();
        long s3 = countCache.estimatedSize();

        sumCache.invalidateAll();
        existsCache.invalidateAll();
        countCache.invalidateAll();

        logger.info("Recommendation caches cleared (sum={}, exists={}, count={})", s1, s2, s3);
    }
}

