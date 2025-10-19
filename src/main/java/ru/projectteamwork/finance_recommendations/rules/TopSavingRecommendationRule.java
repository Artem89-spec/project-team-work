package ru.projectteamwork.finance_recommendations.rules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.projectteamwork.finance_recommendations.dto.RecommendationDTO;
import ru.projectteamwork.finance_recommendations.repository.RecommendationsRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Правило формирования рекомендации по продукту «Top Saving».
 * <p>
 * Данный класс реализует интерфейс {@link RecommendationsRuleSet} и определяет,
 * когда пользователю следует предложить сберегательный продукт
 * на основе анализа его финансового поведения.
 * </p>
 *
 * <h3>Условия рекомендации:</h3>
 * <ul>
 *   <li>У пользователя есть дебетовый продукт;</li>
 *   <li>Баланс по дебетовым продуктам положительный (доходы превышают расходы);</li>
 *   <li>Баланс по дебетовым или сберегательным продуктам превышает лимит (50 000 ₽);</li>
 * </ul>
 *
 * <p>
 * При выполнении всех указанных условий пользователю возвращается объект {@link RecommendationDTO}
 * с предложением открыть продукт «Top Saving» — инструмент для накопления средств.
 * </p>
 *
 * <h3>Назначение:</h3>
 * <ul>
 *   <li>Мотивировать клиентов к использованию накопительных продуктов;</li>
 *   <li>Анализировать финансовые привычки клиента и предлагать релевантные услуги;</li>
 *   <li>Повысить вовлечённость пользователей в продукты долгосрочного сбережения.</li>
 * </ul>
 *
 * @see ru.projectteamwork.finance_recommendations.rules.RecommendationsRuleSet
 * @see ru.projectteamwork.finance_recommendations.dto.RecommendationDTO
 * @see ru.projectteamwork.finance_recommendations.repository.RecommendationsRepository
 */
@Component
public class TopSavingRecommendationRule implements RecommendationsRuleSet {
    private final RecommendationsRepository recommendationsRepository;
    private final Logger logger = LoggerFactory.getLogger(TopSavingRecommendationRule.class);

    /**
     * Конструктор правила «Top Saving»
     *
     * @param recommendationsRepository репозиторий, предоставляющий доступ
     *                                  к информации о продуктах и транзакциях пользователя
     */
    public TopSavingRecommendationRule(RecommendationsRepository recommendationsRepository) {
        this.recommendationsRepository = recommendationsRepository;
    }

    /**
     * Проверяет, удовлетворяет ли пользователь условиям для получения рекомендации
     * по продукту «Top Saving».
     *
     * @param userId строковое представление {@link UUID} пользователя
     * @return {@link Optional} с {@link RecommendationDTO}, если условия выполнены;
     *         {@link Optional#empty()} — если не выполнены
     */
    @Override
    public Optional<RecommendationDTO> checkRule(String userId) {
        UUID userUUID;
        try {
            userUUID = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            logger.error("Некорректная UUID строка: {}", userId, e);
            return Optional.empty();
        }

        String productTypeDebit = "DEBIT";
        String productTypeSaving = "SAVING";
        int limit = 50_000;

        boolean hasDebitProduct = recommendationsRepository.userHasProductType(userUUID, productTypeDebit);

        int amountOfDepositsFromDebitProducts = recommendationsRepository.getSumIncomesByProductType(userUUID, productTypeDebit);
        boolean balanceFromDebitProductsMoreLimit = amountOfDepositsFromDebitProducts > limit;

        int amountOfDepositsFromSavingProducts = recommendationsRepository.getSumIncomesByProductType(userUUID, productTypeSaving);
        boolean balanceFromSavingProductsMoreLimit = amountOfDepositsFromSavingProducts > limit;

        int amountOfExpensesFromDebitProducts = recommendationsRepository.getSumExpensesByProductType(userUUID, productTypeDebit);
        boolean positiveBalanceOnDebitProducts = amountOfDepositsFromDebitProducts > amountOfExpensesFromDebitProducts;

        if (hasDebitProduct && positiveBalanceOnDebitProducts && (balanceFromDebitProductsMoreLimit || balanceFromSavingProductsMoreLimit)) {
            return Optional.of(new RecommendationDTO(
                    "Top Saving",
                    "59efc529-2fff-41af-baff-90ccd7402925",
                    "Откройте свою собственную «Копилку» с нашим банком! " +
                            "«Копилка» — это уникальный банковский инструмент, который поможет вам легко и удобно накапливать деньги на важные цели. " +
                            "Больше никаких забытых чеков и потерянных квитанций — всё под контролем! " +
                            "Преимущества «Копилки»: " +
                            "Накопление средств на конкретные цели. Установите лимит и срок накопления, " +
                            "и банк будет автоматически переводить определенную сумму на ваш счет. " +
                            "Прозрачность и контроль. Отслеживайте свои доходы и расходы, контролируйте процесс накопления и " +
                            " корректируйте стратегию при необходимости. " +
                            "Безопасность и надежность. Ваши средства находятся под защитой банка, " +
                            "а доступ к ним возможен только через мобильное приложение или интернет-банкинг. " +
                            "Начните использовать «Копилку» уже сегодня и станьте ближе к своим финансовым целям!"
            ));
        }
        return Optional.empty();
    }
}

