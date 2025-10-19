package ru.projectteamwork.finance_recommendations.api;

import java.util.List;
import java.util.UUID;

/**
 * <h2>RuleRequest — DTO-запрос для создания динамического правила рекомендаций</h2>
 *
 * <p>Этот класс представляет структуру входных данных, которые клиент
 * (например, веб-интерфейс или тестовый скрипт) отправляет в систему при создании нового
 * динамического правила через эндпоинт {@code POST /rule}.</p>
 *
 * <h3>Назначение</h3>
 * <ul>
 *     <li>Используется в контроллере {@link RuleController} для приёма тела запроса.</li>
 *     <li>Передаёт параметры, описывающие продукт и набор логических условий ({@link QueryItem}).</li>
 *     <li>Конвертируется в сущность {@link ru.projectteamwork.finance_recommendations.domain.DynamicRule}
 *     при помощи {@link ru.projectteamwork.finance_recommendations.domain.service.RuleMapper}.</li>
 * </ul>
 *
 * <h3>Поля</h3>
 * <ul>
 *     <li><b>{@code product_name}</b> — название продукта, к которому привязано правило (например, "Top Saving").</li>
 *     <li><b>{@code product_id}</b> — уникальный идентификатор продукта в системе.</li>
 *     <li><b>{@code product_text}</b> — описание продукта или текст рекомендации, который будет отображён пользователю.</li>
 *     <li><b>{@code rule}</b> — список условий, описывающих, при каких обстоятельствах рекомендация должна сработать.
 *         Каждый элемент — это объект {@link QueryItem}.</li>
 * </ul>
 *
 * <h3>Пример JSON-запроса</h3>
 * <pre>{@code
 * {
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
 * <h3>Взаимодействие</h3>
 * <ul>
 *     <li>Передаётся в метод {@code RuleController.create()} при вызове API.</li>
 *     <li>Обрабатывается {@link ru.projectteamwork.finance_recommendations.domain.service.RuleMapper#toEntity(RuleRequest)}
 *         для преобразования в объект сущности {@link ru.projectteamwork.finance_recommendations.domain.DynamicRule}.</li>
 * </ul>
 *
 * @param product_name название продукта, для которого создаётся правило
 * @param product_id уникальный идентификатор продукта
 * @param product_text описание продукта / текст рекомендации
 * @param rule список условий (см. {@link QueryItem}), определяющих, когда рекомендация активируется
 *
 * @see RuleController
 * @see QueryItem
 * @see ru.projectteamwork.finance_recommendations.domain.DynamicRule
 * @see ru.projectteamwork.finance_recommendations.domain.service.RuleMapper
 */
public record RuleRequest(
        String product_name,
        UUID product_id,
        String product_text,
        List<QueryItem> rule
) {}
