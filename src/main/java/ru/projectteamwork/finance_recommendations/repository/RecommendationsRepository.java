package ru.projectteamwork.finance_recommendations.repository;

import java.util.UUID;

/**
 * Репозиторий для работы с финансовыми данными пользователей, используемый
 * при формировании персонализированных рекомендаций
 * <p>
 * Интерфейс определяет методы для получения агрегированных данных о доходах,
 * расходах, типах продуктов и количестве транзакций пользователя
 * </p>
 *
 * <h3>Назначение:</h3>
 * <ul>
 *   <li>Предоставлять сервисам и бизнес-правилам доступ к финансовым данным пользователя;</li>
 *   <li>Служить источником информации для расчёта условий и генерации рекомендаций;</li>
 *   <li>Инкапсулировать SQL-запросы и работу с кэшем данных.</li>
 * </ul>
 *
 * <h3>Типичные сценарии использования:</h3>
 * <ul>
 *   <li>Проверка, есть ли у пользователя активные продукты определённого типа (например, кредит или вклад);</li>
 *   <li>Вычисление суммы доходов или расходов по конкретной категории;</li>
 *   <li>Анализ активности пользователя на основе количества транзакций.</li>
 * </ul>
 *
 * @see ru.projectteamwork.finance_recommendations.rules.RecommendationsRuleSet
 * @see ru.projectteamwork.finance_recommendations.service.RecommendationsService
 */
public interface RecommendationsRepository {

    /**
     * Возвращает сумму доходов пользователя по указанному типу продукта
     *
     * @param userId      уникальный идентификатор пользователя
     * @param productType тип финансового продукта (например, "DEBIT", "SAVING", "INVEST")
     * @return сумма доходов (в условных единицах), либо {@code null}, если данных нет
     */
    Integer getSumIncomesByProductType(UUID userId, String productType);

    /**
     * Возвращает сумму расходов пользователя по указанному типу продукта.
     *
     * @param userId      уникальный идентификатор пользователя
     * @param productType тип финансового продукта
     * @return сумма расходов (в условных единицах), либо {@code null}, если данных нет
     */
    Integer getSumExpensesByProductType(UUID userId, String productType);

    /**
     * Проверяет, имеет ли пользователь хотя бы один продукт заданного типа.
     *
     * @param userId      уникальный идентификатор пользователя
     * @param productType тип продукта
     * @return {@code true}, если у пользователя есть продукт указанного типа; {@code false} — в противном случае
     */
    boolean userHasProductType(UUID userId, String productType);

    /**
     * Возвращает количество транзакций пользователя по указанному типу продукта.
     *
     * @param userId      уникальный идентификатор пользователя
     * @param productType тип продукта
     * @return количество транзакций по данному продукту
     */
    int countTransactionsByProductType(UUID userId, String productType);

    /**
     * Возвращает сумму операций пользователя по типу продукта и типу транзакции
     *
     * @param userId      уникальный идентификатор пользователя
     * @param productType тип продукта
     * @param txType      тип транзакции (например, "INCOME" или "EXPENSE")
     * @return сумма всех операций указанного типа
     */
    int sumAmountByProductAndTxType(UUID userId, String productType, String txType);

    /**
     * Очищает все кеши, связанные с финансовыми данными пользователей
     * <p>Метод используется для синхронизации данных при обновлениях в базе.</p>
     */
    void clearCaches();
}
