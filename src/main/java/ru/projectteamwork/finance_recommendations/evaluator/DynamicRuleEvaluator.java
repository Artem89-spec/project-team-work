package ru.projectteamwork.finance_recommendations.evaluator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.projectteamwork.finance_recommendations.domain.DynamicRule;
import ru.projectteamwork.finance_recommendations.domain.DynamicRuleQuery;
import ru.projectteamwork.finance_recommendations.repository.RecommendationsRepository;

import java.util.List;
import java.util.UUID;

public class DynamicRuleEvaluator {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final RecommendationsRepository repository;

    public DynamicRuleEvaluator(RecommendationsRepository repository) {
        this.repository = repository;
    }

    public boolean evaluate(DynamicRule rule, String userIdStr) {
        final UUID userId = UUID.fromString(userIdStr);
        for (DynamicRuleQuery query : rule.getQueries()) {
            boolean result = evalQuery(query, userId);
            if (query.isNegate()) result = !result;
            if (!result) return false;
        }
        return true;
    }

    private boolean evalQuery(DynamicRuleQuery q, UUID userId) {
        final String type = q.getQuery().getValue().trim().toUpperCase();
        final List<String> args = readArgs(q.getArgumentsJson());

        switch (type) {
            case "USER_OF": {
                requireArgs(type, args, 1);
                String productType = args.get(0);
                return repository.userHasProductType(userId, productType);
            }
            case "ACTIVE_USER_OF": {
                requireArgs(type, args, 1);
                String productType = args.get(0);
                int count = repository.countTransactionsByProductType(userId, productType);
                return count >= 5;
            }
            case "TRANSACTION_SUM_COMPARE": {
                // [productType, transactionsType, operator, constant]
                requireArgs(type, args, 4);
                String productType = args.get(0);
                String transactionsType = args.get(1); // e.g. DEPOSIT / WITHDRAW
                String operator = args.get(2);         // >, <, =, >=, <=
                int constant = Integer.parseInt(args.get(3));
                int sum = repository.sumAmountByProductAndTxType(userId, productType, transactionsType);
                return compare(sum, operator, constant);
            }
            case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW": {
                requireArgs(type, args, 5);
                String productTypeFirst = args.get(0);
                String transactionsTypeFirst = args.get(1);
                String operator = args.get(2);
                String productTypeSecond = args.get(3);
                String transactionsTypeSecond = args.get(4);

                int deposits = repository.sumAmountByProductAndTxType(userId, productTypeFirst, transactionsTypeFirst);
                int withdraws = repository.sumAmountByProductAndTxType(userId, productTypeSecond, transactionsTypeSecond);

                return compare(deposits, operator, withdraws);
            }
            default:
                throw new IllegalArgumentException("Неизвестный тип запроса: " + type);
        }
    }

    private static void requireArgs(String type, List<String> args, int expected) {
        if (args == null || args.size() < expected) {
            throw new IllegalArgumentException(
                    "Неверное количество аргументов для " + type +
                            ": нужно " + expected + ", получено " + (args == null ? 0 : args.size())
            );
        }
    }

    private boolean compare(int left, String operator, int right) {
        return switch (operator) {
            case ">" -> left > right;
            case "<" -> left < right;
            case "=" -> left == right;
            case ">=" -> left >= right;
            case "<=" -> left <= right;
            default -> throw new IllegalArgumentException("Неизвестный оператор: " + operator);
        };
    }

    private static List<String> readArgs(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<List<String>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Недопустимые аргументы JSON: " + json, e);
        }
    }
}