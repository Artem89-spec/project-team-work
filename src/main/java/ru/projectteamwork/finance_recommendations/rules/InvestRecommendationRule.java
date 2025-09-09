package ru.projectteamwork.finance_recommendations.rules;

import org.springframework.stereotype.Component;
import ru.projectteamwork.finance_recommendations.dto.RecommendationDTO;
import ru.projectteamwork.finance_recommendations.repository.RecommendationsRepository;

import java.util.Optional;
import java.util.UUID;

@Component
public class InvestRecommendationRule implements RecommendationRuleSet {

    private final RecommendationsRepository repository;

    public InvestRecommendationRule(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDTO> checkRule(String userId) {
        UUID uid = UUID.fromString(userId);
        // Простая эвристика: если суммарные депозиты по типу "INVEST" меньше 1000, предложить начать инвестировать
        int deposits = repository.getSumDepositsByProductType(uid, "INVEST");
        if (deposits < 1000) {
            return Optional.of(new RecommendationDTO(
                "INVEST_START",
                "Начните инвестировать",
                "У вас пока небольшие инвестиционные депозиты. Рассмотрите пополнение инвестиционного продукта."
            ));
        }
        return Optional.empty();
    }
}
