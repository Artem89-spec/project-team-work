package ru.projectteamwork.finance_recommendations.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.projectteamwork.finance_recommendations.dto.RecommendationDTO;
import ru.projectteamwork.finance_recommendations.dto.RecommendationsResponse;
import ru.projectteamwork.finance_recommendations.service.RecommendationsService;

import java.util.List;

/**
 * <h2>DynamicRecommendationsController — REST-контроллер для получения динамических рекомендаций</h2>
 *
 * <p>Данный контроллер предоставляет HTTP API для получения персональных финансовых рекомендаций
 * на основе динамических правил и данных пользователя.</p>
 *
 * <h3>Назначение</h3>
 * <p>Контроллер обрабатывает запросы к эндпоинту <b>/api/recommendations/dynamic/{userId}</b>,
 * вызывает {@link RecommendationsService}, выполняет бизнес-логику формирования рекомендаций
 * и возвращает результат в виде {@link RecommendationsResponse}.</p>
 *
 * <h3>Маршруты</h3>
 * <ul>
 *     <li><b>GET /api/recommendations/dynamic/{userId}</b> —
 *     получение списка рекомендаций для конкретного пользователя по его {@code userId}.</li>
 * </ul>
 *
 * <h3>Пример запроса</h3>
 * <pre>{@code
 * GET /api/recommendations/dynamic/123e4567-e89b-12d3-a456-426614174000
 * }</pre>
 *
 * <h3>Пример успешного ответа</h3>
 * <pre>{@code
 * {
 *   "user_id": "123e4567-e89b-12d3-a456-426614174000",
 *   "recommendations": [
 *     {
 *       "name": "Top Saving",
 *       "id": "59efc529-2fff-41af-baff-90ccd7402925",
 *       "text": "Откройте свою собственную «Копилку»..."
 *     },
 *     {
 *       "name": "Invest 500",
 *       "id": "147f6a0f-3b91-413b-ab99-87f081d60d5a",
 *       "text": "Откройте свой путь к успеху..."
 *     }
 *   ]
 * }
 * }</pre>
 *
 * <h3>Особенности</h3>
 * <ul>
 *     <li>Использует {@link RecommendationsService} для вычисления актуальных рекомендаций.</li>
 *     <li>Результат кэшируется на уровне сервисного слоя.</li>
 *     <li>Возвращает стандартный HTTP-ответ со статусом {@code 200 OK} и телом в формате JSON.</li>
 * </ul>
 *
 * @see RecommendationsService
 * @see RecommendationDTO
 * @see RecommendationsResponse
 */
@RestController
@RequestMapping("api")
public class DynamicRecommendationsController {

    /**
     * Сервис для вычисления персональных рекомендаций
     */
    private final RecommendationsService recommendationsService;

    /**
     * Конструктор с внедрением зависимости.
     *
     * @param recommendationsService сервис, предоставляющий рекомендации пользователю
     */
    public DynamicRecommendationsController(RecommendationsService recommendationsService) {
        this.recommendationsService = recommendationsService;
    }

    /**
     * Обрабатывает GET-запрос для получения динамических рекомендаций пользователя.
     *
     * <p>Метод принимает идентификатор пользователя, вызывает сервис рекомендаций и возвращает
     * список продуктов, подходящих данному пользователю по его финансовому профилю.</p>
     *
     * @param userId идентификатор пользователя (UUID в строковом формате)
     * @return объект {@link RecommendationsResponse} со списком рекомендаций
     */
    @GetMapping("/recommendations/dynamic/{userId}")
    public ResponseEntity<RecommendationsResponse> getDynamicRecommendations(@PathVariable String userId) {
        List<RecommendationDTO> recommendations = recommendationsService.getRecommendationsForUser(userId);
        RecommendationsResponse response = new RecommendationsResponse(userId, recommendations);
        return ResponseEntity.ok(response);
    }
}

