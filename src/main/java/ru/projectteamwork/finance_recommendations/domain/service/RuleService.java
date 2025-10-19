package ru.projectteamwork.finance_recommendations.domain.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.projectteamwork.finance_recommendations.api.RuleRequest;
import ru.projectteamwork.finance_recommendations.api.RuleResponse;
import ru.projectteamwork.finance_recommendations.domain.DynamicRule;
import ru.projectteamwork.finance_recommendations.domain.repo.DynamicRuleRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис управления динамическими правилами рекомендаций.
 * <p>
 * Отвечает за создание, получение списка и удаление правил по идентификатору продукта.
 * Для обмена с внешним слоем использует DTO-модели {@link RuleRequest} и {@link RuleResponse},
 * а конвертацию к/из доменных сущностей выполняет {@link RuleMapper}.
 *
 * <h3>Кэширование</h3>
 * Методы сервиса используют Spring Cache:
 * <ul>
 *     <li>{@link #create(RuleRequest)} — кэширует результат с ключом, равным всему объекту запроса
 *         (см. {@code @Cacheable(cacheNames = "ruleCache", key = "#req")}).</li>
 *     <li>{@link #list()} — кэшируется под фиксированным ключом {@code "list"}.</li>
 *     <li>{@link #deleteByProductId(UUID)} — очищает все записи кэша {@code ruleCache},
 *         поскольку удаление влия́ет на валидность ранее закэшированных списков/правил.</li>
 *     <li>{@link #findAllEntities()} — кэшируется под ключом {@code "allEntities"} и возвращает именно доменные сущности.</li>
 * </ul>
 *
 * <h3>Транзакционность</h3>
 * <ul>
 *     <li>Создание и удаление правил выполняются в read-write транзакциях
 *         (аннотация {@link Transactional} без параметров).</li>
 *     <li>Чтение списков помечено как {@code readOnly = true} для оптимизации взаимодействия с JPA/БД.</li>
 * </ul>
 *
 * <h3>Назначение методов</h3>
 * <ul>
 *     <li>{@link #create(RuleRequest)} — сохраняет новое правило и возвращает его представление для API.</li>
 *     <li>{@link #list()} — отдает список всех правил в виде DTO для внешнего интерфейса.</li>
 *     <li>{@link #deleteByProductId(UUID)} — удаляет правила, привязанные к конкретному продукту.</li>
 *     <li>{@link #findAllEntities()} — возвращает все правила как доменные сущности {@link DynamicRule} (полезно для внутренних вычислений).</li>
 * </ul>
 */
@Service
public class RuleService {

    /**
     * Репозиторий для CRUD-операций над сущностью {@link DynamicRule}.
     */
    private final DynamicRuleRepository repo;

    /**
     * Создает экземпляр сервиса правил.
     *
     * @param repo репозиторий динамических правил
     */
    public RuleService(DynamicRuleRepository repo) {
        this.repo = repo;
    }

    /**
     * Создает новое динамическое правило на основе входного запроса,
     * сохраняет его и возвращает DTO ответа для API.
     * <p>
     * Результат кэшируется по ключу, равному объекту {@code req}.
     *
     * @param req данные продукта и набор условий для правила
     * @return сохранённое правило в формате {@link RuleResponse}
     */
    @Transactional
    @Cacheable(cacheNames = "ruleCache", key = "#req")
    public RuleResponse create(RuleRequest req) {
        DynamicRule saved = repo.save(RuleMapper.toEntity(req));
        return RuleMapper.toResponse(saved);
    }

    /**
     * Возвращает список всех правил в формате DTO для внешнего API.
     * <p>
     * Результат кэшируется под ключом {@code "list"}.
     *
     * @return список правил как {@link RuleResponse}
     */
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "ruleCache", key = "'list'")
    public List<RuleResponse> list() {
        return repo.findAll().stream().map(RuleMapper::toResponse).collect(Collectors.toList());
    }

    /**
     * Удаляет все правила, связанные с указанным продуктом, и
     * полностью очищает кэш {@code ruleCache} из-за возможной потери актуальности данных.
     *
     * @param productId идентификатор продукта, к которому привязаны правила
     */
    @Transactional
    @CacheEvict(cacheNames = "ruleCache", allEntries = true)
    public void deleteByProductId(UUID productId) {
        repo.deleteByProductId(productId);
    }

    /**
     * Возвращает все правила в виде доменных сущностей {@link DynamicRule}.
     * <p>
     * Результат кэшируется под ключом {@code "allEntities"}.
     * Этот метод предназначен для внутренних вычислений (например, для оценки правил движком).
     *
     * @return список доменных сущностей {@link DynamicRule}
     */
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "ruleCache", key = "'allEntities'")
    public java.util.List<DynamicRule> findAllEntities() {
        return repo.findAll();
    }
}
