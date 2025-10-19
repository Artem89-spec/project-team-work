package ru.projectteamwork.finance_recommendations.domain.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.projectteamwork.finance_recommendations.api.QueryItem;
import ru.projectteamwork.finance_recommendations.api.RuleRequest;
import ru.projectteamwork.finance_recommendations.api.RuleResponse;
import ru.projectteamwork.finance_recommendations.domain.DynamicRule;
import ru.projectteamwork.finance_recommendations.domain.DynamicRuleQuery;
import ru.projectteamwork.finance_recommendations.domain.enums.QueryType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Утилитарный класс для преобразования между транспортными моделями API
 * ({@link RuleRequest}, {@link RuleResponse}, {@link QueryItem}) и доменными сущностями
 * ({@link DynamicRule}, {@link DynamicRuleQuery}).
 * <p>
 * Основные задачи:
 * <ul>
 *   <li>Собрать доменную сущность {@link DynamicRule} из запроса на создание/изменение правила ({@link RuleRequest}).</li>
 *   <li>Сформировать DTO ответа {@link RuleResponse} из доменной сущности {@link DynamicRule}
 *       для возврата на внешний интерфейс (REST/API).</li>
 * </ul>
 *
 * <h3>Особенности реализации</h3>
 * <ul>
 *   <li>Аргументы каждого условия правила хранятся в сущности как JSON-строка
 *       ({@code argumentsJson}) и конвертируются в/из {@code List<String>} с помощью Jackson.</li>
 *   <li>Тип запроса конвертируется через {@link QueryType#fromString(String)} с валидацией значения.</li>
 *   <li>Позиция условия в правиле ({@code position}) соответствует индексу в исходном списке {@link RuleRequest#rule()}.</li>
 * </ul>
 *
 * <h3>Потокобезопасность</h3>
 * Экземпляр {@link ObjectMapper} хранится в виде статического поля и переиспользуется.
 * Согласно документации Jackson, {@code ObjectMapper} потокобезопасен для операций чтения/записи
 * после завершения конфигурации.
 *
 * <h3>Пример</h3>
 * <pre>{@code
 * RuleRequest req = ...;
 * DynamicRule entity = RuleMapper.toEntity(req);
 * RuleResponse resp = RuleMapper.toResponse(entity);
 * }</pre>
 */
public class RuleMapper {
    /**
     * Общий экземпляр Jackson-мэппера, используемый для сериализации/десериализации
     * аргументов правил (списки строк в JSON и обратно).
     */
    private static final ObjectMapper om = new ObjectMapper();

    /**
     * Преобразует запрос на создание/обновление правила в доменную сущность {@link DynamicRule}
     * с вложенными условиями {@link DynamicRuleQuery}.
     *
     * @param request входной запрос с данными продукта и списком условий правила
     * @return собранная доменная сущность {@link DynamicRule}
     * @throws IllegalArgumentException если тип запроса в одном из элементов {@link QueryItem#query()}
     *                                  не распознан методом {@link QueryType#fromString(String)}
     * @throws RuntimeException         если произошла ошибка сериализации аргументов условия в JSON
     */
    public static DynamicRule toEntity(RuleRequest request) {
        DynamicRule e = new DynamicRule();
        e.setProductId(request.product_id());
        e.setProductName(request.product_name());
        e.setProductText(request.product_text());

        List<DynamicRuleQuery> queries = new ArrayList<>();
        if (request.rule() != null) {
            for (int i = 0; i < request.rule().size(); i++) {
                QueryItem qi = request.rule().get(i);
                DynamicRuleQuery dq = new DynamicRuleQuery();
                dq.setPosition(i);
                dq.setQuery(QueryType.fromString(String.valueOf(qi.query())));
                dq.setArgumentsJson(writeJson(qi.arguments())); // массив строк -> JSON
                dq.setNegate(qi.negate());
                dq.setRule(e);
                queries.add(dq);
            }
        }
        e.setQueries(queries);
        return e;
    }

    /**
     * Преобразует доменную сущность {@link DynamicRule} в DTO ответа {@link RuleResponse},
     * выполняя обратную конвертацию аргументов условий из JSON в {@code List<String>}.
     *
     * @param e доменная сущность правила
     * @return DTO ответа для внешнего API, включающее список {@link QueryItem}
     * @throws RuntimeException если произошла ошибка десериализации аргументов условия из JSON
     */
    public static RuleResponse toResponse(DynamicRule e) {
        List<QueryItem> items = e.getQueries().stream().map(dq ->
                new QueryItem(
                        dq.getQuery(),
                        readJsonList(dq.getArgumentsJson()),
                        dq.isNegate()
                )
        ).collect(Collectors.toList());
        return new RuleResponse(
                e.getId(),
                e.getProductName(),
                e.getProductId(),
                e.getProductText(),
                items
        );
    }

    /**
     * Сериализует произвольный объект (в данном случае — список строк аргументов)
     * в JSON-строку через Jackson.
     *
     * @param o объект для сериализации (обычно {@code List<String>})
     * @return JSON-представление объекта
     * @throws RuntimeException если сериализация завершилась ошибкой
     */
    private static String writeJson(Object o) {
        try { return om.writeValueAsString(o); }
        catch (Exception ex) { throw new RuntimeException(ex); }
    }

    /**
     * Десериализует JSON-строку в список строк аргументов правила.
     *
     * @param json JSON-строка (массив строк)
     * @return список строк, соответствующий аргументам условия
     * @throws RuntimeException если десериализация завершилась ошибкой
     */
    private static List<String> readJsonList(String json) {
        try {
            return om.readValue(
                    json,
                    new TypeReference<List<String>>(){
                    }); }
        catch (Exception ex) { throw new RuntimeException(ex); }
    }
}