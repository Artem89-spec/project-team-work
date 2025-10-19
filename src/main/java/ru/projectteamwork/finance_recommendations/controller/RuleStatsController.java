package ru.projectteamwork.finance_recommendations.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.projectteamwork.finance_recommendations.domain.DynamicRule;
import ru.projectteamwork.finance_recommendations.domain.service.RuleStatService;
import ru.projectteamwork.finance_recommendations.repository.DynamicRuleStatRepository;
import ru.projectteamwork.finance_recommendations.domain.service.RuleService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <h2>RuleStatsController — контроллер статистики срабатываний динамических правил</h2>
 *
 * <p>Контроллер отвечает за предоставление информации о том, сколько раз
 * каждое динамическое правило было применено (сработало) в системе рекомендаций.</p>
 *
 * <h3>Назначение</h3>
 * <p>Используется для мониторинга эффективности и частоты применения динамических правил,
 * что помогает аналитикам и разработчикам оценить, какие правила наиболее востребованы.</p>
 *
 * <h3>Маршрут</h3>
 * <ul>
 *     <li><b>GET /rule/stats</b> — возвращает статистику по всем динамическим правилам,
 *     включая их идентификаторы и количество срабатываний.</li>
 * </ul>
 *
 * <h3>Пример успешного ответа</h3>
 * <pre>{@code
 * {
 *   "stats": [
 *     {
 *       "rule_id": "1f2b3c4d-5678-9abc-def0-1234567890ab",
 *       "count": 42
 *     },
 *     {
 *       "rule_id": "98765432-10fe-dcba-1234-567890abcdef",
 *       "count": 5
 *     }
 *   ]
 * }
 * }</pre>
 *
 * <h3>Особенности</h3>
 * <ul>
 *     <li>Объединяет данные из {@link RuleService} (список правил)
 *     и {@link RuleStatService} (количество срабатываний каждого правила).</li>
 *     <li>Результат возвращается в виде JSON-объекта с полем <b>"stats"</b>,
 *     содержащим список правил и их статистику.</li>
 *     <li>Используется в административных и аналитических интерфейсах приложения.</li>
 * </ul>
 *
 * @see RuleService
 * @see RuleStatService
 * @see DynamicRuleStatRepository
 * @see DynamicRule
 */
@RestController
@RequestMapping("/rule")
public class RuleStatsController {

    /** Репозиторий для прямого доступа к данным статистики (используется для интеграции). */
    private final DynamicRuleStatRepository statRepository;

    /** Сервис для управления статистикой срабатываний правил. */
    private final RuleStatService ruleStatService;

    /** Сервис для получения списка всех динамических правил. */
    private final RuleService ruleService;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param statRepository репозиторий статистики срабатываний правил
     * @param ruleService сервис для работы с динамическими правилами
     * @param ruleStatService сервис для получения количества срабатываний каждого правила
     */
    public RuleStatsController(DynamicRuleStatRepository statRepository,
                               RuleService ruleService,
                               RuleStatService ruleStatService) {
        this.statRepository = statRepository;
        this.ruleService = ruleService;
        this.ruleStatService = ruleStatService;
    }

    /**
     * Возвращает список всех динамических правил и количество их срабатываний.
     *
     * <p>Метод получает все правила из {@link RuleService}, для каждого вычисляет
     * количество срабатываний с помощью {@link RuleStatService#getFireCount(UUID)},
     * и формирует агрегированный ответ в виде JSON-объекта.</p>
     *
     * @return {@link Map} с ключом <b>"stats"</b>, содержащим список объектов:
     * <ul>
     *     <li><b>rule_id</b> — UUID правила;</li>
     *     <li><b>count</b> — количество срабатываний.</li>
     * </ul>
     */
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        List<DynamicRule> allRules = ruleService.findAllEntities();

        List<Map<String, Object>> stats = allRules.stream()
                .map(rule -> {
                    Long fireCount = ruleStatService.getFireCount(rule.getId());
                    return Map.<String, Object>of(
                            "rule_id", rule.getId().toString(),
                            "count", fireCount
                    );
                })
                .collect(Collectors.toList());

        return Map.of("stats", stats);
    }
}

