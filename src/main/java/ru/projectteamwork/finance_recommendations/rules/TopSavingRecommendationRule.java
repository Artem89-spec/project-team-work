package ru.projectteamwork.finance_recommendations.rules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.projectteamwork.finance_recommendations.dto.RecommendationDTO;
import ru.projectteamwork.finance_recommendations.repository.RecommendationsRepository;

import java.util.Optional;
import java.util.UUID;

@Component
public class TopSavingRecommendationRule implements RecommendationsRuleSet {
    private final RecommendationsRepository recommendationsRepository;
    private final Logger logger = LoggerFactory.getLogger(TopSavingRecommendationRule.class);

    public TopSavingRecommendationRule(RecommendationsRepository recommendationsRepository) {
        this.recommendationsRepository = recommendationsRepository;
    }

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
