package ru.projectteamwork.finance_recommendations.dto;

import java.util.List;

/**
 * Класс {@code RecommendationsResponse} представляет собой объект ответа,
 * содержащий рекомендации, сформированные для конкретного пользователя
 * <p>
 * Используется для передачи данных между сервисом рекомендаций и внешними компонентами,
 * такими как REST-контроллер или Telegram-бот.
 * </p>
 *
 * <h3>Структура данных:</h3>
 * <ul>
 *   <li>{@code user_id} — идентификатор пользователя, для которого сформированы рекомендации;</li>
 *   <li>{@code recommendations} — список объектов {@link RecommendationDTO},
 *       содержащих информацию о каждой персональной рекомендации.</li>
 * </ul>
 *
 * <h3>Назначение:</h3>
 * <ul>
 *   <li>Используется как объект-ответ (response DTO) для REST-запросов или API Telegram-бота;</li>
 *   <li>Позволяет удобно сериализовать результат в JSON при взаимодействии с клиентом;</li>
 *   <li>Хранит набор рекомендаций, сгенерированных на основе правил и данных пользователя.</li>
 * </ul>
 *
 * <h3>Пример использования:</h3>
 * <pre>
 * List&lt;RecommendationDTO&gt; recos = recommendationsService.getRecommendationsForUser(userId);
 * RecommendationsResponse response = new RecommendationsResponse(userId, recos);
 * </pre>
 *
 * @see ru.projectteamwork.finance_recommendations.dto.RecommendationDTO
 * @see ru.projectteamwork.finance_recommendations.service.RecommendationsService
 */
public class RecommendationsResponse {

    /** Уникальный идентификатор пользователя, для которого сформированы рекомендации. */
    private String user_id;

    /** Список персональных рекомендаций для пользователя. */
    private List<RecommendationDTO> recommendations;

    /**
     * Конструктор для создания объекта ответа с рекомендациями.
     *
     * @param user_id         идентификатор пользователя
     * @param recommendations список рекомендаций {@link RecommendationDTO}
     */
    public RecommendationsResponse(String user_id, List<RecommendationDTO> recommendations) {
        this.user_id = user_id;
        this.recommendations = recommendations;
    }

    /** @return идентификатор пользователя */
    public String getUser_id() {
        return user_id;
    }

    /** @return список рекомендаций */
    public List<RecommendationDTO> getRecommendations() {
        return recommendations;
    }

    /**
     * Устанавливает идентификатор пользователя.
     *
     * @param user_id новый идентификатор пользователя
     */
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    /**
     * Устанавливает список рекомендаций.
     *
     * @param recommendations новый список рекомендаций
     */
    public void setRecommendations(List<RecommendationDTO> recommendations) {
        this.recommendations = recommendations;
    }
}

