package ru.projectteamwork.finance_recommendations.evaluator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.projectteamwork.finance_recommendations.domain.DynamicRule;
import ru.projectteamwork.finance_recommendations.domain.DynamicRuleQuery;
import ru.projectteamwork.finance_recommendations.repository.RecommendationsRepository;

import java.util.List;
import java.util.UUID;

public class DynamicRuleEvaluator {

    private static final ObjectMapper om = new ObjectMapper();
    private final RecommendationsRepository repo;

    public DynamicRuleEvaluator(RecommendationsRepository repo) {
        this.repo = repo;
    }

    public boolean evaluate(DynamicRule rule, String userIdStr) {
        final UUID userId = UUID.fromString(userIdStr);
        for (DynamicRuleQuery q : rule.getQueries()) {
            boolean result = evalQuery(q, userId);
            if (q.isNegate()) result = !result;
            if (!result) return false;
        }
        return true;
    }

    private boolean evalQuery(DynamicRuleQuery q, UUID userId) {
        final String type = q.getQuery().trim().toUpperCase();
        final List<String> args = readArgs(q.getArgumentsJson());

        switch (type) {
            case "USER_OF": {
                String productType = args.get(0);
                return repo.existsTransactionsByProductType(userId, productType);
            }
            case "ACTIVE_USER_OF": {
                String productType = args.get(0);
                int count = repo.countTransactionsByProductType(userId, productType);
                return count >= 5;
            }
            case "TRANSACTION_SUM_COMPARE": {
                String productType = args.get(0);
                String txType = args.get(1);
                String op = args.get(2);
                int constant = Integer.parseInt(args.get(3));
                int sum = repo.sumAmountByProductAndTxType(userId, productType, txType);
                return compare(sum, op, constant);
            }
            case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW": {
                String productType = args.get(0);
                String op = args.get(1);
                int deposits = repo.sumAmountByProductAndTxType(userId, productType, "DEPOSIT");
                int withdraws = repo.sumAmountByProductAndTxType(userId, productType, "WITHDRAW");
                return compare(deposits, op, withdraws);
            }
            default:
                throw new IllegalArgumentException("Unknown query type: " + type);
        }
    }

    private boolean compare(int left, String op, int right) {
        return switch (op) {
            case ">"  -> left > right;
            case "<"  -> left < right;
            case "="  -> left == right;
            case ">=" -> left >= right;
            case "<=" -> left <= right;
            default   -> throw new IllegalArgumentException("Unsupported operator: " + op);
        };
    }

    private static List<String> readArgs(String json) {
        try { return om.readValue(json, new TypeReference<List<String>>(){}); }
        catch (Exception e) { throw new RuntimeException("Invalid arguments JSON: " + json, e); }
    }
}