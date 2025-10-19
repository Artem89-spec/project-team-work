package ru.projectteamwork.finance_recommendations.domain;

import jakarta.persistence.*;
import ru.projectteamwork.finance_recommendations.domain.enums.QueryType;

/**
 * <h2>DynamicRuleQuery — условие (запрос) динамического правила</h2>
 *
 * <p>Класс описывает одно конкретное условие, входящее в состав {@link DynamicRule}.
 * Каждое условие задаёт логическую проверку, которая используется при вычислении,
 * нужно ли показывать пользователю определённую рекомендацию.</p>
 *
 * <h3>Назначение</h3>
 * <p>Сущность хранит тип проверки (см. {@link QueryType}), список аргументов
 * в виде JSON-строки и признак отрицания (логическое «НЕ»).</p>
 * <p>Например, условие может означать:
 * «пользователь имеет продукт типа DEBIT» или «сумма транзакций > 1000».</p>
 *
 * <h3>Связь с {@link DynamicRule}</h3>
 * <ul>
 *     <li>Каждый объект {@code DynamicRuleQuery} связан с одним {@link DynamicRule}
 *     через поле {@link #rule}.</li>
 *     <li>При удалении правила все связанные условия также удаляются (см. каскад в {@code DynamicRule}).</li>
 * </ul>
 *
 * <h3>Поля таблицы {@code dynamic_rule_query}</h3>
 * <ul>
 *   <li><b>id</b> — уникальный идентификатор условия (генерируется автоматически).</li>
 *   <li><b>rule_id</b> — внешний ключ, связывающий условие с конкретным правилом.</li>
 *   <li><b>position</b> — порядковый номер условия в рамках одного правила (используется для сортировки).</li>
 *   <li><b>query</b> — тип проверки, определяющий, какую логику применить (см. {@link QueryType}).</li>
 *   <li><b>arguments</b> — параметры запроса в формате JSON (например, ["DEBIT", "WITHDRAW", ">", "1000"]).</li>
 *   <li><b>negate</b> — логический флаг инверсии результата проверки.</li>
 * </ul>
 *
 * <h3>Пример JSON аргументов</h3>
 * <pre>{@code
 * ["DEBIT", "WITHDRAW", ">", "50000"]
 * }</pre>
 * <p>Это означает: проверить сумму снятий (WITHDRAW) по дебетовым продуктам (DEBIT),
 * и вернуть true, если она больше 50 000.</p>
 *
 * <h3>Пример использования</h3>
 * <pre>{@code
 * DynamicRuleQuery query = new DynamicRuleQuery();
 * query.setQuery(QueryType.TRANSACTION_SUM_COMPARE);
 * query.setArgumentsJson("[\"DEBIT\", \"DEPOSIT\", \">\", \"1000\"]");
 * query.setNegate(false);
 * query.setPosition(0);
 * }</pre>
 *
 * @see DynamicRule
 * @see QueryType
 */
@Entity
@Table(name = "dynamic_rule_query")
public class DynamicRuleQuery {

    /**
     * Уникальный идентификатор условия (первичный ключ).
     * <p>Тип: {@link Long}. Генерация осуществляется стратегией {@link GenerationType#IDENTITY}.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Ссылка на родительское правило, которому принадлежит данное условие.
     * <p>Связь: многие к одному ({@link ManyToOne}), ленивое извлечение.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    private DynamicRule rule;

    /**
     * Порядковый номер условия в списке правил.
     * <p>Определяет последовательность выполнения проверок.</p>
     */
    @Column(name = "position", nullable = false)
    private Integer position;

    /**
     * Тип логической проверки, выполняемой данным условием.
     * <p>См. перечисление {@link QueryType} (например, USER_OF, TRANSACTION_SUM_COMPARE).</p>
     */
    @Column(name = "query", nullable = false, length = 64)
    @Enumerated(EnumType.STRING)
    private QueryType query;

    /**
     * Аргументы проверки, сериализованные в JSON.
     * <p>Например, ["DEBIT", "DEPOSIT", ">", "1000"].</p>
     */
    @Column(name = "arguments", nullable = false, length = 2000)
    private String argumentsJson;

    /**
     * Флаг отрицания результата проверки.
     * <p>Если true — результат вычисления инвертируется (логическое "НЕ").</p>
     */
    @Column(name = "negate", nullable = false)
    private boolean negate;

    /** Возвращает идентификатор условия. */
    public Long getId() {
        return id;
    }

    /** Возвращает правило, которому принадлежит данное условие. */
    public DynamicRule getRule() {
        return rule;
    }

    /** Устанавливает ссылку на правило, к которому относится условие. */
    public void setRule(DynamicRule rule) {
        this.rule = rule;
    }

    /** Возвращает позицию (порядковый номер) условия в списке. */
    public Integer getPosition() {
        return position;
    }

    /** Устанавливает позицию (порядковый номер) условия в списке. */
    public void setPosition(Integer position) {
        this.position = position;
    }

    /** Возвращает тип запроса (условия). */
    public QueryType getQuery() {
        return query;
    }

    /** Устанавливает тип запроса (условия). */
    public void setQuery(QueryType query) {
        this.query = query;
    }

    /** Возвращает аргументы условия в формате JSON. */
    public String getArgumentsJson() {
        return argumentsJson;
    }

    /** Устанавливает аргументы условия в формате JSON. */
    public void setArgumentsJson(String argumentsJson) {
        this.argumentsJson = argumentsJson;
    }

    /** Возвращает флаг инверсии (негирования) результата проверки. */
    public boolean isNegate() {
        return negate;
    }

    /** Устанавливает флаг инверсии (негирования) результата проверки. */
    public void setNegate(boolean negate) {
        this.negate = negate;
    }
}
