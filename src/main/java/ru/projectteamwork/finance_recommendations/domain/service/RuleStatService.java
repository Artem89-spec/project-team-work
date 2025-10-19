package ru.projectteamwork.finance_recommendations.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import ru.projectteamwork.finance_recommendations.domain.DynamicRuleStat;
import ru.projectteamwork.finance_recommendations.repository.DynamicRuleStatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Сервис сбора и предоставления статистики по срабатываниям динамических правил
 * <p>
 * Хранит количество срабатываний (fireCount) для каждого правила в таблице {@link DynamicRuleStat}.
 * Предоставляет операции инкремента счётчика и чтения его актуального значения.
 * Для ускорения повторных обращений использует кэш {@code ruleStatCache}.
 */
@Service
public class RuleStatService {

    /**
     * Репозиторий доступа к сущностям статистики правил.
     */
    private final DynamicRuleStatRepository statRepository;

    /**
     * Логгер сервиса.
     */
    private final Logger logger = LoggerFactory.getLogger(RuleStatService.class);

    /**
     * Менеджер кэшей Spring. Используется для ручного обновления значения счётчика
     * в кэше {@code ruleStatCache} после инкремента в БД
     */
    @Autowired
    private CacheManager cacheManager;

    /**
     * Конструктор сервиса.
     *
     * @param statRepository репозиторий статистики правил
     */
    public RuleStatService(DynamicRuleStatRepository statRepository) {
        this.statRepository = statRepository;
    }

    /**
     * Инкрементирует счётчик срабатываний для правила.
     * <p>
     * Если запись со статистикой по указанному {@code ruleId} отсутствует,
     * она создаётся с начальным значением {@code 0} и затем инкрементируется
     * После успешного сохранения значение счётчика также кладётся/обновляется
     * в кэше {@code ruleStatCache} под ключом {@code ruleId}.
     *
     * <p><b>Транзакционность:</b> операция выполняется в read-write транзакции.</p>
     *
     * @param ruleId идентификатор правила
     */
    @Transactional
    public void inc(UUID ruleId) {
        DynamicRuleStat stat = statRepository.findById(ruleId)
                .orElseGet(() -> {
                    DynamicRuleStat s = new DynamicRuleStat();
                    s.setRuleId(ruleId);
                    s.setFireCount(0L);
                    return s;
                });
        stat.setFireCount(stat.getFireCount() + 1);
        statRepository.save(stat);

        Cache cache = cacheManager.getCache("ruleStatCache");
        if (cache != null) {
            cache.put(ruleId, stat.getFireCount());
        } else {
            logger.info("кеш не был найден");
        }
    }

    /**
     * Возвращает текущее количество срабатываний правила.
     * <p>
     * Результат кэшируется в {@code ruleStatCache} с ключом {@code ruleId}.
     * При отсутствии записи в БД возвращает {@code 0}.
     *
     * @param ruleId идентификатор правила
     * @return количество срабатываний (fireCount) или {@code 0}, если данных нет
     */
    @Cacheable(value = "ruleStatCache", key = "#ruleId")
    public Long getFireCount(UUID ruleId) {
        return statRepository.findById(ruleId)
                .map(DynamicRuleStat::getFireCount)
                .orElse(0L);
    }
}
