package ru.projectteamwork.finance_recommendations.rules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.projectteamwork.finance_recommendations.dto.RecommendationDTO;
import ru.projectteamwork.finance_recommendations.repository.RecommendationsRepository;

import java.util.Optional;
import java.util.UUID;

@Component
public class CreditRecommendationRule implements RecommendationsRuleSet {
    private final RecommendationsRepository repository;
    private final Logger logger = LoggerFactory.getLogger(CreditRecommendationRule.class);

    public CreditRecommendationRule(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDTO> checkRule(String userId) {
        UUID userUUID ;
        try {
            userUUID = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            logger.error("Некорректная UUID строка: {}", userId, e);
            return Optional.empty();
        }

        String productTypeCredit = "CREDIT";
        String productTypeDebit = "DEBIT";
        int limit = 100_000;

        boolean hasCreditProduct = repository.userHasProductType(userUUID, productTypeCredit);

        int incomesByDebitsProducts = repository.getSumIncomesByProductType(userUUID, productTypeDebit);
        int expensesByDebitsProducts = repository.getSumExpensesByProductType(userUUID, productTypeDebit);
        boolean incomesMoreExpensesByDebitsProducts = incomesByDebitsProducts > expensesByDebitsProducts;

        boolean amountExpensesByDebitProductMoreThanLimit = expensesByDebitsProducts > limit;

        if (!hasCreditProduct && incomesMoreExpensesByDebitsProducts && amountExpensesByDebitProductMoreThanLimit) {
            return Optional.of(new RecommendationDTO(
                    "Простой кредит",
                    "ab138afb-f3ba-4a93-b74f-0fcee86d447f",
                    "Откройте мир выгодных кредитов с нами! " +
                            "Ищете способ быстро и без лишних хлопот получить нужную сумму? " +
                            "Тогда наш выгодный кредит — именно то, что вам нужно! " +
                            "Мы предлагаем низкие процентные ставки, гибкие условия и индивидуальный подход к каждому клиенту. " +
                            "Почему выбирают нас: " +
                            "Быстрое рассмотрение заявки. Мы ценим ваше время, поэтому процесс рассмотрения заявки занимает всего несколько часов." +
                            "Удобное оформление. Подать заявку на кредит можно онлайн на нашем сайте или в мобильном приложении." +
                            "Широкий выбор кредитных продуктов. Мы предлагаем кредиты на различные цели: " +
                            "покупку недвижимости, автомобиля, образование, лечение и многое другое. " +
                            "Не упустите возможность воспользоваться выгодными условиями кредитования от нашей компании!"
            ));
        }
        return Optional.empty();
    }
}
