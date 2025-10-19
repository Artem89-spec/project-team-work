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
 * <h2>RecommendationsController — REST-контроллер для получения персональных рекомендаций</h2>
 *
 * <p>Контроллер отвечает за предоставление пользователю списка рекомендаций
 * на основе его уникального идентификатора. Рекомендации могут включать
 * как статические (предопределённые), так и динамические предложения, рассчитанные
 * на основе пользовательских данных.</p>
 *
 * <h3>Назначение</h3>
 * <p>Служит основным интерфейсом для взаимодействия клиентских приложений
 * (веб, мобильных, чат-ботов и т.д.) с модулем рекомендаций.</p>
 *
 * <h3>Маршрут</h3>
 * <ul>
 *     <li><b>GET /api/recommendations/{userId}</b> —
 *     возвращает все релевантные рекомендации для указанного пользователя.</li>
 * </ul>
 *
 * <h3>Пример запроса</h3>
 * <pre>{@code
 * GET /api/recommendations/123e4567-e89b-12d3-a456-426614174000
 * }</pre>
 *
 * <h3>Пример успешного ответа</h3>
 * <pre>{@code
 * {
 *   "user_id": "123e4567-e89b-12d3-a456-426614174000",
 *   "recommendations": [
 *     {
 *       "name": "Invest 500",
 *       "id": "147f6a0f-3b91-413b-ab99-87f081d60d5a",
 *       "text": "Откройте свой путь к успеху с индивидуальным инвестиционным счётом..."
 *     },
 *     {
 *       "name": "Top Saving",
 *       "id": "59efc529-2fff-41af-baff-90ccd7402925",
 *       "text": "Начните использовать «Копилку» уже сегодня..."
 *     }
 *   ]
 * }
 * }</pre>
 *
 * <h3>Особенности</h3>
 * <ul>
 *     <li>Использует {@link RecommendationsService} для вычисления и агрегации рекомендаций.</li>
 *     <li>Возвращает результат в виде {@link RecommendationsResponse}.</li>
 *     <li>Метод кэшируется на уровне сервисного слоя для повышения производительности.</li>
 *     <li>Поддерживает UUID пользователей в строковом формате.</li>
 * </ul>
 *
 * @see RecommendationsService
 * @see RecommendationDTO
 * @see RecommendationsResponse
 */
@RestController
@RequestMapping("/api")
public class RecommendationsController {

    /**
     * Сервис, отвечающий за бизнес-логику получения рекомендаций.
     */
    private final RecommendationsService recommendationsService;

    /**
     * Конструктор контроллера с внедрением зависимостей.
     *
     * @param recommendationsService сервис для расчёта и получения рекомендаций
     */
    public RecommendationsController(RecommendationsService recommendationsService) {
        this.recommendationsService = recommendationsService;
    }

    /**
     * Обрабатывает запрос на получение списка персональных рекомендаций для пользователя
     *
     * <p>Метод принимает идентификатор пользователя, переданный в URL,
     * вызывает {@link RecommendationsService}, который возвращает список рекомендаций,
     * и упаковывает их в объект {@link RecommendationsResponse}.</p>
     *
     * @param userId идентификатор пользователя (UUID в виде строки)
     * @return HTTP-ответ со списком рекомендаций в формате JSON
     */
    @GetMapping("/recommendations/{userId}")
    public ResponseEntity<RecommendationsResponse> getRecommendations(@PathVariable String userId) {
        List<RecommendationDTO> recommendations = recommendationsService.getRecommendationsForUser(userId);
        RecommendationsResponse response = new RecommendationsResponse(userId, recommendations);
        return ResponseEntity.ok(response);
    }
}

