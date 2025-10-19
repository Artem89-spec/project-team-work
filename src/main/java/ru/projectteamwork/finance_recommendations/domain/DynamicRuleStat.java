package ru.projectteamwork.finance_recommendations.domain;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * <h2>DynamicRuleStat — статистика срабатываний динамических правил</h2>
 *
 * <p>Данная сущность хранит количество срабатываний (исполнений)
 * каждого динамического правила {@link DynamicRule}.
 * Используется для аналитики и оптимизации системы рекомендаций.</p>
 *
 * <h3>Назначение</h3>
 * <p>Когда динамическое правило успешно выполняется для пользователя
 * (т.е. все его условия {@link DynamicRuleQuery} возвращают true),
 * счётчик {@code fireCount} для этого правила увеличивается на единицу.</p>
 *
 * <h3>Структура таблицы {@code dynamic_rule_stat}</h3>
 * <ul>
 *   <li><b>rule_id</b> — уникальный идентификатор правила (UUID), совпадает с {@link DynamicRule#getId()}.</li>
 *   <li><b>fire_count</b> — количество раз, когда правило было выполнено успешно (≥ 0).</li>
 * </ul>
 *
 * <h3>Особенности реализации</h3>
 * <ul>
 *   <li>Поле {@link #ruleId} используется как первичный ключ.</li>
 *   <li>Значение {@link #fireCount} по умолчанию равно 0 и увеличивается при каждом вызове {@link #inc()}.</li>
 *   <li>Класс управляется через сервис {@code RuleStatService}, который отвечает за инкремент и кэширование значений.</li>
 * </ul>
 *
 * <h3>Пример использования</h3>
 * <pre>{@code
 * DynamicRuleStat stat = new DynamicRuleStat(UUID.randomUUID());
 * stat.inc();  // Увеличить счётчик на 1
 * long total = stat.getFireCount();  // Получить текущее значение
 * }</pre>
 *
 * @see DynamicRule
 * @see ru.projectteamwork.finance_recommendations.domain.service.RuleStatService
 */
@Entity
@Table(name = "dynamic_rule_stat")
public class DynamicRuleStat {

    /**
     * Идентификатор динамического правила, для которого ведётся статистика.
     * <p>Совпадает с {@link DynamicRule#getId()} и является первичным ключом записи.</p>
     */
    @Id
    @Column(name = "rule_id", nullable = false)
    private UUID ruleId;

    /**
     * Счётчик количества успешных срабатываний данного правила.
     * <p>Инициализируется нулём и увеличивается при каждом успешном применении правила.</p>
     */
    @Column(name = "fire_count", nullable = false)
    private long fireCount = 0L;

    /** Конструктор без параметров (требуется для JPA). */
    public DynamicRuleStat() {}

    /**
     * Создаёт новую статистическую запись для указанного правила.
     *
     * @param ruleId идентификатор правила
     */
    public DynamicRuleStat(UUID ruleId) {
        this.ruleId = ruleId;
        this.fireCount = 0L;
    }

    /** Возвращает идентификатор правила, для которого хранится статистика. */
    public UUID getRuleId() {
        return ruleId;
    }

    /** Устанавливает идентификатор правила, для которого ведётся статистика. */
    public void setRuleId(UUID ruleId) {
        this.ruleId = ruleId;
    }

    /** Возвращает количество успешных срабатываний правила. */
    public long getFireCount() {
        return fireCount;
    }

    /** Устанавливает значение счётчика срабатываний вручную. */
    public void setFireCount(long fireCount) {
        this.fireCount = fireCount;
    }

    /**
     * Увеличивает счётчик срабатываний на единицу.
     * <p>Используется при каждом успешном выполнении правила.</p>
     */
    public void inc() {
        this.fireCount++;
    }
}

