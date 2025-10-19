package ru.projectteamwork.finance_recommendations.service.impl;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.projectteamwork.finance_recommendations.domain.service.RuleService;
import ru.projectteamwork.finance_recommendations.domain.service.RuleStatService;
import ru.projectteamwork.finance_recommendations.dto.RecommendationDTO;
import ru.projectteamwork.finance_recommendations.evaluator.DynamicRuleEvaluator;
import ru.projectteamwork.finance_recommendations.repository.RecommendationsRepository;
import ru.projectteamwork.finance_recommendations.rules.RecommendationsRuleSet;
import ru.projectteamwork.finance_recommendations.service.RecommendationsService;

import java.util.*;

/**
 * Реализация сервиса {@link RecommendationsService}, отвечающая за получение
 * персональных рекомендаций пользователя
 * <p>
 * Класс объединяет работу статических и динамических правил рекомендаций
 * Он обрабатывает оба типа правил, исключает дублирующиеся рекомендации,
 * а также использует кэширование для оптимизации повторных запросов
 * </p>
 *
 * <h3>Основные функции:</h3>
 * <ul>
 *   <li>Вычисление статических рекомендаций на основе {@link RecommendationsRuleSet};</li>
 *   <li>Оценка динамических правил через {@link DynamicRuleEvaluator};</li>
 *   <li>Инкремент статистики применения правил через {@link RuleStatService};</li>
 *   <li>Объединение и фильтрация всех рекомендаций без дублирования;</li>
 *   <li>Использование кэша для ускорения повторных обращений.</li>
 * </ul>
 *
 * <h3>Кэширование:</h3>
 * <p>
 * Метод {@link #getRecommendationsForUser(String)} помечен аннотацией
 * {@link Cacheable} с кэшем <b>"recommendationsCache"</b>, что позволяет избежать
 * повторных вычислений для одного и того же пользователя
 * </p>
 */
@Service
public class RecommendationsServiceImpl implements RecommendationsService {
    private final List<RecommendationsRuleSet> rules;
    private final RuleService ruleService;
    private final DynamicRuleEvaluator evaluator;
    private final RuleStatService ruleStatService;

    /**
     * Конструктор сервиса рекомендаций.
     *
     * @param rules список статических наборов правил {@link RecommendationsRuleSet}
     * @param ruleService сервис управления динамическими правилами
     * @param recommendationsRepository репозиторий для работы с данными рекомендаций
     * @param ruleStatService сервис для учёта статистики использования правил
     */
    public RecommendationsServiceImpl(List<RecommendationsRuleSet> rules,
                                      RuleService ruleService,
                                      RecommendationsRepository recommendationsRepository,
                                      RuleStatService ruleStatService) {
        this.rules = rules;
        this.ruleService = ruleService;
        this.evaluator = new DynamicRuleEvaluator(recommendationsRepository);
        this.ruleStatService = ruleStatService;
    }

    /**
     * Возвращает список персональных рекомендаций для указанного пользователя
     * <p>
     * Метод объединяет результаты из статических и динамических правил, исключая дубли.
     * Если пользователь уже запрашивал рекомендации ранее — результат берётся из кэша.
     * </p>
     *
     * @param userId идентификатор пользователя (или его имя)
     * @return список уникальных рекомендаций {@link RecommendationDTO}
     */
    @Override
    @Cacheable(value = "recommendationsCache", key = "#userId")
    public List<RecommendationDTO> getRecommendationsForUser(String userId) {
        List<RecommendationDTO> staticRecommendations = new ArrayList<>();
        List<RecommendationDTO> dynamicRecommendations = new ArrayList<>();

        // --- Проверка статических правил ---
        for (RecommendationsRuleSet rule : rules) {
            rule.checkRule(userId).ifPresent(staticRecommendations::add);
        }

        // --- Проверка динамических правил ---
        ruleService.findAllEntities().forEach(dynamicRule -> {
            if (evaluator.evaluate(dynamicRule, userId)) {
                ruleStatService.inc(dynamicRule.getId());
                dynamicRecommendations.add(new RecommendationDTO(
                        dynamicRule.getProductName(),
                        dynamicRule.getProductId().toString(),
                        dynamicRule.getProductText()
                ));
            }
        });

        // --- Объединение без дублирования ---
        Set<String> recommendationsID = new HashSet<>();
        List<RecommendationDTO> combinedRecommendations = new ArrayList<>();

        for (RecommendationDTO recommendation : staticRecommendations) {
            if (recommendationsID.add(recommendation.getId())) {
                combinedRecommendations.add(recommendation);
            }
        }

        for (RecommendationDTO recommendation : dynamicRecommendations) {
            if (recommendationsID.add(recommendation.getId())) {
                combinedRecommendations.add(recommendation);
            }
        }

        return combinedRecommendations;
    }
}