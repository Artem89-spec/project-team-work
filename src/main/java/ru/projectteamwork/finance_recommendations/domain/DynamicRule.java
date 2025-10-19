package ru.projectteamwork.finance_recommendations.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * <h2>DynamicRule — сущность динамического правила рекомендаций</h2>
 *
 * <p>Класс описывает правило, по которому пользователю предлагается тот или иной финансовый продукт.
 * Каждое правило связано с конкретным продуктом и содержит один или несколько логических условий
 * ({@link DynamicRuleQuery}), определяющих, когда именно рекомендация должна быть показана.</p>
 *
 * <h3>Назначение</h3>
 * <p>Динамические правила используются для автоматической генерации персональных рекомендаций
 * на основе анализа данных пользователя (доходы, расходы, наличие продуктов, активность и т.д.).</p>
 *
 * <h3>Связи и каскадирование</h3>
 * <ul>
 *     <li>Связь «один ко многим» с {@link DynamicRuleQuery} по полю {@code rule}.</li>
 *     <li>Каскадирование {@link CascadeType#ALL} — при сохранении/удалении правила
 *     соответствующие запросы также сохраняются или удаляются автоматически.</li>
 *     <li>{@link FetchType#EAGER} — запросы подгружаются сразу при загрузке правила.</li>
 *     <li>{@code orphanRemoval = true} — удалённые из списка запросы также удаляются из базы данных.</li>
 * </ul>
 *
 * <h3>Поля таблицы {@code dynamic_rule}</h3>
 * <ul>
 *   <li><b>id</b> — уникальный идентификатор правила (UUID, генерируется Hibernate автоматически).</li>
 *   <li><b>product_id</b> — идентификатор связанного продукта, для которого создаётся рекомендация.</li>
 *   <li><b>product_name</b> — человекочитаемое имя продукта (до 255 символов).</li>
 *   <li><b>product_text</b> — текст описания продукта или рекламное сообщение (до 2000 символов).</li>
 *   <li><b>queries</b> — список логических условий, задающих поведение правила.</li>
 * </ul>
 *
 * <h3>Методы класса</h3>
 * <ul>
 *     <li>{@link #addQuery(DynamicRuleQuery)} — добавляет одно условие, автоматически выставляя позицию
 *     и обратную ссылку на правило.</li>
 *     <li>{@link #setQueries(List)} — переопределяет весь список условий с актуализацией позиций и ссылок.</li>
 *     <li>Геттеры и сеттеры предоставляют доступ к полям для ORM и сервисного слоя.</li>
 * </ul>
 *
 * <h3>Пример использования</h3>
 * <pre>{@code
 * DynamicRule rule = new DynamicRule();
 * rule.setProductId(UUID.randomUUID());
 * rule.setProductName("Premium Credit Card");
 * rule.setProductText("Специальное предложение с кэшбэком 10%");
 *
 * DynamicRuleQuery query = new DynamicRuleQuery();
 * query.setQuery(QueryType.USER_OF);
 * query.setArgumentsJson("[\"DEBIT\"]");
 * query.setNegate(false);
 * rule.addQuery(query);
 * }</pre>
 *
 * @see DynamicRuleQuery
 * @see ru.projectteamwork.finance_recommendations.domain.enums.QueryType
 */
@Entity
@Table(name = "dynamic_rule")
public class DynamicRule {

    /**
     * Уникальный идентификатор правила (первичный ключ).
     * <p>Тип: UUID. Генерация значения выполняется автоматически Hibernate.</p>
     */
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "UUID", nullable = false)
    private UUID id;

    /**
     * Идентификатор продукта, на который распространяется это правило.
     * <p>Связывает правило с записью о продукте в другой системе или таблице.</p>
     */
    @Column(name = "product_id", columnDefinition = "UUID", nullable = false)
    private UUID productId;

    /**
     * Название продукта (например, «Invest 500» или «Top Saving»).
     */
    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    /**
     * Описание продукта, используемое при формировании текстовой рекомендации пользователю.
     */
    @Column(name = "product_text", nullable = false, length = 2000)
    private String productText;

    /**
     * Список запросов (условий), входящих в правило.
     * <p>Каждое условие описывает конкретную проверку, например:
     * «пользователь имеет продукт типа DEBIT» или
     * «сумма транзакций больше 1000».</p>
     *
     * <p>Загружается немедленно при обращении к правилу.</p>
     */
    @OneToMany(
            mappedBy = "rule",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @OrderBy("position ASC")
    private List<DynamicRuleQuery> queries = new ArrayList<>();

    /**
     * Добавляет одно условие к правилу, автоматически устанавливая:
     * <ul>
     *     <li>ссылку на текущее правило ({@code q.setRule(this)})</li>
     *     <li>позицию условия в списке ({@code position = queries.size()})</li>
     * </ul>
     *
     * @param q объект {@link DynamicRuleQuery}, описывающий новое условие
     */
    public void addQuery(DynamicRuleQuery q) {
        q.setRule(this);
        q.setPosition(queries.size());
        queries.add(q);
    }

    /**
     * Устанавливает новый список условий для правила.
     * <p>Предыдущие условия полностью очищаются, а каждому новому элементу
     * проставляется актуальная позиция и ссылка на текущее правило.</p>
     *
     * @param list список условий ({@link DynamicRuleQuery})
     */
    public void setQueries(List<DynamicRuleQuery> list) {
        this.queries.clear();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                DynamicRuleQuery q = list.get(i);
                q.setRule(this);
                q.setPosition(i);
                this.queries.add(q);
            }
        }
    }

    /** Возвращает уникальный идентификатор правила. */
    public UUID getId() {
        return id;
    }

    /** Устанавливает уникальный идентификатор правила. */
    public void setId(UUID id) {
        this.id = id;
    }

    /** Возвращает идентификатор продукта. */
    public UUID getProductId() {
        return productId;
    }

    /** Устанавливает идентификатор продукта. */
    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    /** Возвращает название продукта. */
    public String getProductName() {
        return productName;
    }

    /** Устанавливает название продукта. */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /** Возвращает текстовое описание продукта. */
    public String getProductText() {
        return productText;
    }

    /** Устанавливает текстовое описание продукта. */
    public void setProductText(String productText) {
        this.productText = productText;
    }

    /** Возвращает список условий, входящих в правило. */
    public List<DynamicRuleQuery> getQueries() {
        return queries;
    }
}