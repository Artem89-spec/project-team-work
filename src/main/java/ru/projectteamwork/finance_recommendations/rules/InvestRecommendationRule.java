package ru.projectteamwork.finance_recommendations.rules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.projectteamwork.finance_recommendations.dto.RecommendationDTO;
import ru.projectteamwork.finance_recommendations.repository.RecommendationsRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Правило формирования рекомендации по продукту «Invest 500»
 * <p>
 * Данный класс реализует интерфейс {@link RecommendationsRuleSet} и содержит
 * бизнес-логику, определяющую, когда пользователю следует предложить
 * инвестиционный продукт
 * </p>
 *
 * <h3>Условия рекомендации:</h3>
 * <ul>
 *   <li>У пользователя есть дебетовый продукт;</li>
 *   <li>У пользователя отсутствует инвестиционный продукт;</li>
 *   <li>Сумма доходов по сберегательным продуктам превышает установленный лимит (1 000 ₽);</li>
 * </ul>
 *
 * <p>
 * При выполнении всех условий пользователю формируется рекомендация с продуктом
 * «Invest 500» в виде объекта {@link RecommendationDTO}.
 * </p>
 *
 * <h3>Назначение:</h3>
 * <ul>
 *   <li>Повышение вовлечённости клиентов в инвестиционные продукты;</li>
 *   <li>Использование существующих данных о транзакциях для формирования персонализированных предложений;</li>
 *   <li>Применение бизнес-логики рекомендаций в автоматизированном режиме.</li>
 * </ul>
 *
 * @see ru.projectteamwork.finance_recommendations.rules.RecommendationsRuleSet
 * @see ru.projectteamwork.finance_recommendations.dto.RecommendationDTO
 * @see ru.projectteamwork.finance_recommendations.repository.RecommendationsRepository
 */
@Component
public class InvestRecommendationRule implements RecommendationsRuleSet {
    private final RecommendationsRepository recommendationsRepository;
    private final Logger logger = LoggerFactory.getLogger(InvestRecommendationRule.class);

    /**
     * Конструктор правила инвестиционных рекомендаций
     *
     * @param recommendationsRepository репозиторий для получения данных
     *                                  о транзакциях и продуктах пользователя
     */
    public InvestRecommendationRule(RecommendationsRepository recommendationsRepository) {
        this.recommendationsRepository = recommendationsRepository;
    }

    /**
     * Проверяет, подходит ли пользователь под условия для получения
     * рекомендации по инвестиционному продукту
     *
     * @param userId строковое представление {@link UUID} пользователя
     * @return {@link Optional} с {@link RecommendationDTO}, если условия выполнены;
     *         {@link Optional#empty()} — если условия не соблюдены
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
        String productTypeInvest = "INVEST";
        String productTypeSaving = "SAVING";
        int limit = 1_000;

        boolean hasDebitProduct = recommendationsRepository.userHasProductType(userUUID, productTypeDebit);
        boolean hasInvestProduct = recommendationsRepository.userHasProductType(userUUID, productTypeInvest);
        int incomeFromSavingProducts = recommendationsRepository.getSumIncomesByProductType(userUUID, productTypeSaving);
        boolean incomeFromSavingProductsMoreLimit = incomeFromSavingProducts > limit;

        if (hasDebitProduct && !hasInvestProduct && incomeFromSavingProductsMoreLimit) {
            return Optional.of(new RecommendationDTO(
                    "Invest 500",
                    "147f6a0f-3b91-413b-ab99-87f081d60d5a",
                    "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! " +
                            "Воспользуйтесь налоговыми льготами и начните инвестировать с умом. " +
                            "Пополните счет до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде. " +
                            "Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными рыночными тенденциями. " +
                            "Откройте ИИС сегодня и станьте ближе к финансовой независимости!"
            ));
        }
        return Optional.empty();
    }
}