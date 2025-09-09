package ru.projectteamwork.finance_recommendations.rules;

import org.springframework.stereotype.Component;
import ru.projectteamwork.finance_recommendations.dto.RecommendationDTO;
import ru.projectteamwork.finance_recommendations.repository.RecommendationsRepository;

import java.util.Optional;
import java.util.UUID;

@Component
public class TopSavingRecommendationRule implements RecommendationRuleSet {

    private final RecommendationsRepository repository;

    public TopSavingRecommendationRule(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDTO> checkRule(String userId) {
        UUID uid = UUID.fromString(userId);
        boolean hasSavings = repository.usersHasProductType(uid, "SAVINGS");
        if (!hasSavings) {
            return Optional.of(new RecommendationDTO(
                "OPEN_SAVINGS",
                "Откройте сберегательный продукт",
                "Мы не нашли у вас сберегательного продукта. Откройте "SAVINGS" для формирования подушки."
            ));
        }
        return Optional.empty();
    }
}
