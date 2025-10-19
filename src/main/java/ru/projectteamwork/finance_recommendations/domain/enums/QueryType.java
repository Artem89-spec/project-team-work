package ru.projectteamwork.finance_recommendations.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Перечисление {@code QueryType} описывает возможные типы запросов (условий),
 * используемых в динамических правилах рекомендаций.
 * <p>
 * Каждый элемент перечисления представляет собой логический тип проверки,
 * применяемой при вычислении правил в {@link ru.projectteamwork.finance_recommendations.evaluator.DynamicRuleEvaluator}.
 * </p>
 *
 * <h3>Доступные типы запросов:</h3>
 * <ul>
 *   <li><b>USER_OF</b> — проверяет, есть ли у пользователя продукт определённого типа;</li>
 *   <li><b>ACTIVE_USER_OF</b> — проверяет, является ли пользователь активным по продукту (например, имеет ≥5 транзакций);</li>
 *   <li><b>TRANSACTION_SUM_COMPARE</b> — сравнивает сумму транзакций по продукту с заданной константой;</li>
 *   <li><b>TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW</b> — сравнивает суммы депозитов и снятий по разным продуктам.</li>
 * </ul>
 *
 * <h3>Использование:</h3>
 * <p>Тип запроса указывается в объекте {@code DynamicRuleQuery} и используется при вычислении условий динамического правила.</p>
 *
 * <h3>Интеграция с JSON:</h3>
 * <ul>
 *   <li>{@link JsonValue} — указывает, что при сериализации в JSON используется строковое значение поля {@code value};</li>
 *   <li>{@link JsonCreator} — обеспечивает корректную десериализацию из строки в соответствующий элемент перечисления.</li>
 * </ul>
 *
 * <h3>Пример использования:</h3>
 * <pre>
 * QueryType type = QueryType.fromString("USER_OF");
 * System.out.println(type.getValue()); // USER_OF
 * </pre>
 *
 * @see ru.projectteamwork.finance_recommendations.domain.DynamicRuleQuery
 * @see ru.projectteamwork.finance_recommendations.evaluator.DynamicRuleEvaluator
 */
public enum QueryType {

    /** Проверка наличия у пользователя продукта указанного типа. */
    USER_OF("USER_OF"),

    /** Проверка активности пользователя по продукту (наличие определённого количества транзакций). */
    ACTIVE_USER_OF("ACTIVE_USER_OF"),

    /** Сравнение суммы транзакций по продукту с фиксированным значением. */
    TRANSACTION_SUM_COMPARE("TRANSACTION_SUM_COMPARE"),

    /** Сравнение суммы депозитов и снятий между двумя типами продуктов. */
    TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW("TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW");

    /** Текстовое значение, используемое при сериализации/десериализации JSON. */
    private final String value;

    /**
     * Конструктор, задающий строковое представление типа запроса.
     *
     * @param value строковое значение типа запроса
     */
    QueryType(String value) {
        this.value = value;
    }

    /**
     * Возвращает строковое представление типа запроса.
     *
     * @return строковое значение (например, "USER_OF")
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Преобразует строку в соответствующий элемент перечисления {@code QueryType}.
     * <p>Используется при десериализации JSON.</p>
     *
     * @param key строка с именем типа запроса
     * @return соответствующий элемент {@code QueryType}
     * @throws IllegalArgumentException если строка не соответствует ни одному известному типу
     */
    @JsonCreator
    public static QueryType fromString(String key) {
        for (QueryType type : values()) {
            if (type.value.equalsIgnoreCase(key)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Неизвестный тип запроса: " + key);
    }
}
