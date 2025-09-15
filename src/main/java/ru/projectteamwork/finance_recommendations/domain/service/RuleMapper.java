package ru.projectteamwork.finance_recommendations.domain.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.projectteamwork.finance_recommendations.api.QueryItem;
import ru.projectteamwork.finance_recommendations.api.RuleRequest;
import ru.projectteamwork.finance_recommendations.api.RuleResponse;
import ru.projectteamwork.finance_recommendations.domain.DynamicRule;
import ru.projectteamwork.finance_recommendations.domain.DynamicRuleQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RuleMapper {
    private static final ObjectMapper om = new ObjectMapper();

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
                dq.setQuery(qi.query());
                dq.setArgumentsJson(writeJson(qi.arguments())); // массив строк -> JSON
                dq.setNegate(qi.negate());
                dq.setRule(e);
                queries.add(dq);
            }
        }
        e.setQueries(queries);
        return e;
    }

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

    private static String writeJson(Object o) {
        try { return om.writeValueAsString(o); }
        catch (Exception ex) { throw new RuntimeException(ex); }
    }

    private static List<String> readJsonList(String json) {
        try { return om.readValue(json, new TypeReference<List<String>>(){}); }
        catch (Exception ex) { throw new RuntimeException(ex); }
    }
}
