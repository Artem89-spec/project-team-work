package ru.projectteamwork.finance_recommendations.api;

import java.util.List;
import java.util.UUID;

/**
 * <h2>RuleResponse — DTO-ответ с информацией о динамическом правиле рекомендаций</h2>
 *
 * <p>Этот класс используется для возврата данных о созданных или существующих
 * динамических правилах рекомендаций через REST API.</p>
 *
 * <h3>Назначение</h3>
 * <ul>
 *     <li>Возвращается контроллером {@link RuleController} в ответ на запросы
 *         <code>POST /rule</code> и <code>GET /rule</code>.</li>
 *     <li>Представляет клиенту полное описание правила, включая связанные условия ({@link QueryItem}).</li>
 *     <li>Формируется из сущности {@link ru.projectteamwork.finance_recommendations.domain.DynamicRule}
 *         с помощью {@link ru.projectteamwork.finance_recommendations.domain.service.RuleMapper#toResponse}.</li>
 * </ul>
 *
 * <h3>Поля</h3>
 * <ul>
 *     <li><b>{@code id}</b> — уникальный идентификатор динамического правила в системе.</li>
 *     <li><b>{@code product_name}</b> — название продукта, к которому относится правило (например, "Invest 500").</li>
 *     <li><b>{@code product_id}</b> — идентификатор продукта, связанного с данным правилом.</li>
 *     <li><b>{@code product_text}</b> — текст рекомендации, который будет показан пользователю при срабатывании правила.</li>
 *     <li><b>{@code rule}</b> — список логических условий ({@link QueryItem}),
 *         описывающих, при каких обстоятельствах рекомендация должна быть активирована.</li>
 * </ul>
 *
 * <h3>Пример JSON-ответа</h3>
 * <pre>{@code
 * {
 *   "id": "5f6f8e92-3c2d-4c53-abc4-9f7f6a47f98a",
 *   "product_name": "Invest 500",
 *   "product_id": "147f6a0f-3b91-413b-ab99-87f081d60d5a",
 *   "product_text": "Начни инвестировать с минимальными рисками!",
 *   "rule": [
 *     {
 *       "query": "USER_OF",
 *       "arguments": ["DEBIT"],
 *       "negate": false
 *     },
 *     {
 *       "query": "TRANSACTION_SUM_COMPARE",
 *       "arguments": ["DEBIT", "DEPOSIT", ">", "100000"],
 *       "negate": false
 *     }
 *   ]
 * }
 * }</pre>
 *
 * <h3>Использование</h3>
 * <ul>
 *     <li>Формируется в сервисе {@link ru.projectteamwork.finance_recommendations.domain.service.RuleService}
 *         при создании или запросе правил.</li>
 *     <li>Используется клиентами (например, веб-интерфейсом или Telegram-ботом)
 *         для отображения текущих правил и связанных рекомендаций.</li>
 * </ul>
 *
 * @param id уникальный идентификатор динамического правила
 * @param product_name название продукта, связанного с правилом
 * @param product_id UUID продукта, для которого создаётся рекомендация
 * @param product_text текст рекомендации, который показывается пользователю
 * @param rule список условий ({@link QueryItem}), определяющих, когда рекомендация активируется
 *
 * @see RuleController
 * @see RuleRequest
 * @see QueryItem
 * @see ru.projectteamwork.finance_recommendations.domain.DynamicRule
 * @see ru.projectteamwork.finance_recommendations.domain.service.RuleMapper
 */
public record RuleResponse(
        UUID id,
        String product_name,
        UUID product_id,
        String product_text,
        List<QueryItem> rule
) {}
