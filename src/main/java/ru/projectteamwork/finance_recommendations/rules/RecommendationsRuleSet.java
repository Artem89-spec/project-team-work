package ru.projectteamwork.finance_recommendations.rules;

import ru.projectteamwork.finance_recommendations.dto.RecommendationDTO;
import java.util.Optional;

/**
 * Интерфейс, определяющий контракт для набора правил формирования рекомендаций
 * <p>
 * Каждая реализация данного интерфейса описывает отдельное бизнес-правило,
 * которое проверяет, соответствует ли пользователь условиям для получения
 * определённого финансового предложения
 * </p>
 *
 * <h3>Назначение:</h3>
 * <ul>
 *   <li>Инкапсулировать логику проверки конкретных условий;</li>
 *   <li>Возвращать персонализированную рекомендацию при их выполнении;</li>
 *   <li>Обеспечить единый интерфейс для различных типов правил (например, кредитных, инвестиционных, сберегательных).</li>
 * </ul>
 *
 * <p>
 * Метод {@link #checkRule(String)} используется для анализа состояния пользователя
 * и определения, подходит ли ему данное предложение.
 * </p>
 *
 * @see ru.projectteamwork.finance_recommendations.dto.RecommendationDTO
 * @see ru.projectteamwork.finance_recommendations.service.RecommendationsService
 */
public interface RecommendationsRuleSet {

    /**
     * Проверяет выполнение бизнес-правила для указанного пользователя
     *
     * @param userId уникальный идентификатор пользователя (в формате UUID)
     * @return {@link Optional} с {@link RecommendationDTO}, если условия правила выполнены;
     *         {@link Optional#empty()} — если условия не соблюдены
     */
    Optional<RecommendationDTO> checkRule(String userId);
}