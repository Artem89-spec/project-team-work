package ru.projectteamwork.finance_recommendations.api;

import ru.projectteamwork.finance_recommendations.domain.enums.QueryType;

import java.util.List;

/**
 * <h2>QueryItem — элемент условия динамического правила</h2>
 *
 * <p>Данный класс описывает одно логическое условие (часть правила),
 * применяемое при формировании персональных рекомендаций пользователю.</p>
 *
 * <p>Используется как часть запроса {@link ru.projectteamwork.finance_recommendations.api.RuleRequest}
 * и хранится внутри сущности {@link ru.projectteamwork.finance_recommendations.domain.DynamicRuleQuery}.</p>
 *
 * <h3>Назначение</h3>
 * <ul>
 *     <li>Определяет тип проверки, выполняемой над данными пользователя (например, наличие продукта, сравнение сумм и т.д.).</li>
 *     <li>Содержит список аргументов, необходимых для выполнения запроса.</li>
 *     <li>Позволяет инвертировать результат условия с помощью флага {@code negate}.</li>
 * </ul>
 *
 * <h3>Поля</h3>
 * <ul>
 *     <li><b>{@code query}</b> — тип запроса ({@link QueryType}), определяющий, какую операцию выполняет условие.
 *         Например:
 *         <ul>
 *             <li>{@code USER_OF} — проверяет, есть ли у пользователя продукт заданного типа.</li>
 *             <li>{@code ACTIVE_USER_OF} — проверяет активность пользователя по продукту.</li>
 *             <li>{@code TRANSACTION_SUM_COMPARE} — сравнивает сумму транзакций с константой.</li>
 *         </ul>
 *     </li>
 *     <li><b>{@code arguments}</b> — список строковых аргументов, необходимых для выполнения проверки.
 *         Пример: ["DEBIT", "DEPOSIT", ">", "1000"].</li>
 *     <li><b>{@code negate}</b> — логический флаг. Если установлен в {@code true}, то результат условия инвертируется.</li>
 * </ul>
 *
 * <h3>Пример использования</h3>
 * <pre>{@code
 * new QueryItem(
 *     QueryType.TRANSACTION_SUM_COMPARE,
 *     List.of("DEBIT", "DEPOSIT", ">", "10000"),
 *     false
 * );
 * }</pre>
 *
 * <h3>Взаимодействие</h3>
 * <ul>
 *     <li>Обрабатывается классом {@link ru.projectteamwork.finance_recommendations.evaluator.DynamicRuleEvaluator}.</li>
 *     <li>Конвертируется в JSON для хранения в БД через {@link ru.projectteamwork.finance_recommendations.domain.service.RuleMapper}.</li>
 * </ul>
 *
 * @param query тип запроса, определяющий вид проверки
 * @param arguments список аргументов, необходимых для выполнения запроса
 * @param negate флаг инверсии результата проверки
 *
 * @see ru.projectteamwork.finance_recommendations.api.RuleRequest
 * @see ru.projectteamwork.finance_recommendations.domain.DynamicRuleQuery
 * @see ru.projectteamwork.finance_recommendations.evaluator.DynamicRuleEvaluator
 */
public record QueryItem(
        QueryType query,
        List<String> arguments,
        boolean negate
) {}
