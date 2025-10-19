package ru.projectteamwork.finance_recommendations.evaluator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.Cacheable;
import ru.projectteamwork.finance_recommendations.domain.DynamicRule;
import ru.projectteamwork.finance_recommendations.domain.DynamicRuleQuery;
import ru.projectteamwork.finance_recommendations.repository.RecommendationsRepository;

import java.util.List;
import java.util.UUID;

/**
 * Класс {@code DynamicRuleEvaluator} отвечает за интерпретацию и выполнение динамических правил рекомендаций.
 * <p>
 * Динамические правила позволяют описывать условия для генерации персонализированных рекомендаций
 * в виде набора параметров (query-объектов), которые проверяются для конкретного пользователя.
 * </p>
 *
 * <h3>Основные функции:</h3>
 * <ul>
 *   <li>Проверка набора условий {@link DynamicRuleQuery} для пользователя;</li>
 *   <li>Поддержка различных типов выражений, таких как:
 *       <ul>
 *           <li><b>USER_OF</b> — наличие у пользователя продукта определённого типа;</li>
 *           <li><b>ACTIVE_USER_OF</b> — наличие не менее пяти транзакций по продукту;</li>
 *           <li><b>TRANSACTION_SUM_COMPARE</b> — сравнение суммы транзакций с константой;</li>
 *           <li><b>TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW</b> — сравнение сумм депозитов и снятий по разным продуктам.</li>
 *       </ul>
 *   </li>
 *   <li>Поддержка отрицания условия с помощью флага {@code negate};</li>
 *   <li>Кэширование результатов проверки для повышения производительности.</li>
 * </ul>
 *
 * <h3>Кэширование:</h3>
 * Результаты вычислений кэшируются с помощью Spring Cache
 * (аннотация {@link Cacheable}) в кэше <b>"ruleEvaluationCache"</b>,
 * где ключ формируется на основе ID правила и ID пользователя.
 *
 * <h3>Используемые зависимости:</h3>
 * <ul>
 *   <li>{@link RecommendationsRepository} — для получения информации о транзакциях и продуктах пользователя;</li>
 *   <li>{@link ObjectMapper} — для десериализации аргументов условий из JSON.</li>
 * </ul>
 *
 * <h3>Типичное использование:</h3>
 * <pre>
 * DynamicRuleEvaluator evaluator = new DynamicRuleEvaluator(recommendationsRepository);
 * boolean matches = evaluator.evaluate(rule, userId);
 * if (matches) {
 *     // добавить рекомендацию пользователю
 * }
 * </pre>
 *
 * @see ru.projectteamwork.finance_recommendations.domain.DynamicRule
 * @see ru.projectteamwork.finance_recommendations.domain.DynamicRuleQuery
 * @see ru.projectteamwork.finance_recommendations.repository.RecommendationsRepository
 */
public class DynamicRuleEvaluator {

    /** JSON-парсер для чтения аргументов правил. */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /** Репозиторий для получения данных о продуктах и транзакциях пользователя. */
    private final RecommendationsRepository repository;

    /**
     * Конструктор для внедрения зависимостей.
     *
     * @param repository репозиторий с данными о транзакциях и продуктах
     */
    public DynamicRuleEvaluator(RecommendationsRepository repository) {
        this.repository = repository;
    }

    /**
     * Выполняет оценку (проверку) динамического правила для конкретного пользователя
     * <p>
     * Если все условия {@link DynamicRuleQuery} возвращают {@code true},
     * правило считается выполненным, и для пользователя может быть создана рекомендация.
     * </p>
     *
     * @param rule      динамическое правило, содержащее список запросов
     * @param userIdStr идентификатор пользователя в формате UUID (строка)
     * @return {@code true}, если все условия правила выполнены; иначе — {@code false}
     * @throws IllegalArgumentException если формат UUID некорректен
     */
    @Cacheable(cacheNames = "ruleEvaluationCache", key = "#rule.id + '-' + #userIdStr")
    public boolean evaluate(DynamicRule rule, String userIdStr) {
        final UUID userId = UUID.fromString(userIdStr);
        for (DynamicRuleQuery query : rule.getQueries()) {
            boolean result = evalQuery(query, userId);
            if (query.isNegate()) result = !result;
            if (!result) return false;
        }
        return true;
    }

    /**
     * Выполняет отдельное условие {@link DynamicRuleQuery} для пользователя.
     * <p>
     * Поддерживает несколько типов логических операций, определённых в поле {@code query}.
     * </p>
     *
     * @param q      объект запроса динамического правила
     * @param userId идентификатор пользователя
     * @return {@code true}, если условие выполняется; иначе — {@code false}
     * @throws IllegalArgumentException если тип запроса неизвестен или аргументы некорректны
     */
    private boolean evalQuery(DynamicRuleQuery q, UUID userId) {
        final String type = q.getQuery().getValue().trim().toUpperCase();
        final List<String> args = readArgs(q.getArgumentsJson());

        switch (type) {
            case "USER_OF" -> {
                requireArgs(type, args, 1);
                String productType = args.get(0);
                return repository.userHasProductType(userId, productType);
            }
            case "ACTIVE_USER_OF" -> {
                requireArgs(type, args, 1);
                String productType = args.get(0);
                int count = repository.countTransactionsByProductType(userId, productType);
                return count >= 5;
            }
            case "TRANSACTION_SUM_COMPARE" -> {
                requireArgs(type, args, 4);
                String productType = args.get(0);
                String transactionsType = args.get(1);
                String operator = args.get(2);
                int constant = Integer.parseInt(args.get(3));
                int sum = repository.sumAmountByProductAndTxType(userId, productType, transactionsType);
                return compare(sum, operator, constant);
            }
            case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW" -> {
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
            default -> throw new IllegalArgumentException("Неизвестный тип запроса: " + type);
        }
    }

    /**
     * Проверяет корректность количества аргументов в запросе.
     *
     * @param type     тип запроса (например, "USER_OF")
     * @param args     список аргументов
     * @param expected ожидаемое количество аргументов
     * @throws IllegalArgumentException если аргументов меньше, чем требуется
     */
    private static void requireArgs(String type, List<String> args, int expected) {
        if (args == null || args.size() < expected) {
            throw new IllegalArgumentException(
                    "Неверное количество аргументов для " + type +
                            ": нужно " + expected + ", получено " + (args == null ? 0 : args.size())
            );
        }
    }

    /**
     * Сравнивает два целочисленных значения в соответствии с заданным оператором.
     *
     * @param left     левое значение
     * @param operator оператор сравнения (>, <, =, >=, <=)
     * @param right    правое значение
     * @return результат сравнения в виде булевого значения
     * @throws IllegalArgumentException если оператор не поддерживается
     */
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

    /**
     * Преобразует JSON-строку аргументов в список строковых значений
     *
     * @param json строка в формате JSON, содержащая аргументы
     * @return список аргументов в виде {@link List<String>}
     * @throws RuntimeException если JSON некорректен
     */
    private static List<String> readArgs(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Недопустимые аргументы JSON: " + json, e);
        }
    }
}
