package ru.projectteamwork.finance_recommendations.rules;

import org.springframework.stereotype.Component;
import ru.projectteamwork.finance_recommendations.dto.RecommendationDTO;
import ru.projectteamwork.finance_recommendations.repository.RecommendationsRepository;

import java.util.Optional;
import java.util.UUID;

@Component
public class CreditRecommendationRule implements RecommendationRuleSet {

    private final RecommendationsRepository repository;

    public CreditRecommendationRule(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDTO> checkRule(String userId) {
        UUID uid = UUID.fromString(userId);
        int creditExpenses = repository.getSumExpensesByProductType(uid, "CREDIT");
        if (creditExpenses > 5000) {
            return Optional.of(new RecommendationDTO(
                "CREDIT_LOAD",
                "Снизьте кредитную нагрузку",
                "Расходы по кредитным продуктам высокие. Рассмотрите рефинансирование или досрочное погашение."
            ));
        }
        return Optional.empty();
    }
}
