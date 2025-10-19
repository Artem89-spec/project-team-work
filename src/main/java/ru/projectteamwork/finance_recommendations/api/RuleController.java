package ru.projectteamwork.finance_recommendations.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.projectteamwork.finance_recommendations.domain.service.RuleService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <h2>RuleController — REST-контроллер для управления динамическими правилами рекомендаций</h2>
 *
 * <p>Данный контроллер предоставляет API-эндпоинты для создания, получения и удаления
 * динамических правил, которые используются системой для генерации персональных финансовых рекомендаций.</p>
 *
 * <h3>Назначение</h3>
 * <ul>
 *     <li>Позволяет создавать новые правила через POST-запросы.</li>
 *     <li>Возвращает список всех существующих правил.</li>
 *     <li>Позволяет удалять правила по идентификатору связанного продукта.</li>
 * </ul>
 *
 * <h3>Базовый URL</h3>
 * <pre><code>/rule</code></pre>
 *
 * <h3>Эндпоинты</h3>
 *
 * <h4>1. Создание нового правила</h4>
 * <pre><code>POST /rule</code></pre>
 * <p>Создаёт новое динамическое правило на основе переданного запроса.</p>
 * <b>Пример запроса:</b>
 * <pre>{@code
 * POST /rule
 * Content-Type: application/json
 *
 * {
 *   "product_id": "123e4567-e89b-12d3-a456-426614174000",
 *   "product_name": "Invest Plan 2025",
 *   "product_text": "Выгодные инвестиции с низким риском",
 *   "rule": [
 *     {
 *       "query": "USER_OF",
 *       "arguments": ["DEBIT"],
 *       "negate": false
 *     },
 *     {
 *       "query": "TRANSACTION_SUM_COMPARE",
 *       "arguments": ["DEBIT", "DEPOSIT", ">", "50000"],
 *       "negate": false
 *     }
 *   ]
 * }
 * }</pre>
 *
 * <b>Ответ (200 OK):</b>
 * <pre>{@code
 * {
 *   "id": "5f6f8e92-3c2d-4c53-abc4-9f7f6a47f98a",
 *   "product_name": "Invest Plan 2025",
 *   "product_id": "123e4567-e89b-12d3-a456-426614174000",
 *   "product_text": "Выгодные инвестиции с низким риском",
 *   "queries": [...]
 * }
 * }</pre>
 *
 * <h4>2. Получение списка всех правил</h4>
 * <pre><code>GET /rule</code></pre>
 * <p>Возвращает список всех динамических правил, доступных в системе.</p>
 *
 * <b>Ответ (200 OK):</b>
 * <pre>{@code
 * {
 *   "data": [
 *     {
 *       "id": "...",
 *       "product_name": "...",
 *       "product_id": "...",
 *       "product_text": "...",
 *       "queries": [...]
 *     }
 *   ]
 * }
 * }</pre>
 *
 * <h4>3. Удаление правила по ID продукта</h4>
 * <pre><code>DELETE /rule/{productId}</code></pre>
 * <p>Удаляет динамическое правило, связанное с указанным продуктом.</p>
 * <b>Пример:</b> <code>DELETE /rule/123e4567-e89b-12d3-a456-426614174000</code>
 * <br>Возвращает статус <b>204 No Content</b> при успешном удалении.
 *
 * <h3>Архитектура и зависимости</h3>
 * <ul>
 *     <li>Использует сервис {@link RuleService} для бизнес-логики управления правилами.</li>
 *     <li>Возвращает ответы в формате {@link ResponseEntity} для стандартной обработки HTTP-статусов.</li>
 * </ul>
 *
 * <h3>Кэширование</h3>
 * <p>Данные правил могут кэшироваться на уровне {@link RuleService} с помощью Spring Cache
 * для оптимизации работы при большом количестве запросов.</p>
 *
 * @see RuleService
 * @see RuleRequest
 * @see RuleResponse
 * @see QueryItem
 */
@RestController
public class RuleController {

    private final RuleService ruleService;

    /**
     * Конструктор контроллера, внедряющий {@link RuleService}.
     *
     * @param ruleService сервис управления динамическими правилами
     */
    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    /**
     * Создаёт новое правило рекомендаций.
     *
     * @param request объект запроса с параметрами правила
     * @return созданное правило в виде {@link RuleResponse}
     */
    @PostMapping("/rule")
    public ResponseEntity<RuleResponse> create(@RequestBody RuleRequest request) {
        return ResponseEntity.ok(ruleService.create(request));
    }

    /**
     * Возвращает список всех существующих правил.
     *
     * @return карта с ключом "data" и списком правил
     */
    @GetMapping("/rule")
    public ResponseEntity<Map<String, Object>> list() {
        Map<String, Object> body = new HashMap<>();
        body.put("data", ruleService.list());
        return ResponseEntity.ok(body);
    }

    /**
     * Удаляет правило по идентификатору связанного продукта.
     *
     * @param productId UUID продукта, для которого нужно удалить правило
     * @return HTTP-ответ без содержимого (204 No Content)
     */
    @DeleteMapping("/rule/{productId}")
    public ResponseEntity<Void> delete(@PathVariable UUID productId) {
        ruleService.deleteByProductId(productId);
        return ResponseEntity.noContent().build();
    }
}
