package ru.projectteamwork.finance_recommendations.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <h2>CacheConfig — конфигурация системы кэширования приложения</h2>
 *
 * <p>Этот класс настраивает менеджер кэша, используемый в модуле финансовых рекомендаций
 * Он отвечает за хранение и управление временными данными, чтобы снизить нагрузку на базу данных
 * и ускорить выполнение часто повторяющихся запросов.</p>
 *
 * <h3>Назначение</h3>
 * <ul>
 *     <li>Определяет список кэшей, используемых в приложении.</li>
 *     <li>Создаёт {@link CacheManager}, управляющий кэшами на основе {@link ConcurrentMapCacheManager}.</li>
 *     <li>Обеспечивает поддержку аннотаций {@code @Cacheable}, {@code @CacheEvict} и {@code @CachePut} в сервисах и репозиториях.</li>
 * </ul>
 *
 * <h3>Используемые кэши</h3>
 * <ul>
 *     <li><b>recommendationsCache</b> — хранит результаты вычислений рекомендаций для пользователей.</li>
 *     <li><b>ruleCache</b> — кэширует динамические правила, полученные из базы данных.</li>
 *     <li><b>ruleStatCache</b> — хранит статистику срабатываний правил (fire count).</li>
 *     <li><b>ruleEvaluationCache</b> — сохраняет результаты проверки правил для конкретных пользователей.</li>
 *     <li><b>userIdCache</b> — кэширует соответствия "Имя Фамилия → UUID пользователя" для быстрого доступа.</li>
 * </ul>
 *
 * <h3>Особенности</h3>
 * <ul>
 *     <li>Реализован с использованием потокобезопасного {@link ConcurrentMapCacheManager}.</li>
 *     <li>Кэш хранится в памяти приложения (in-memory), что подходит для лёгких и POC-сценариев.</li>
 *     <li>Для продакшена рекомендуется использовать распределённые решения, например Redis или Caffeine.</li>
 * </ul>
 *
 * <h3>Пример использования:</h3>
 * <pre>{@code
 * @Cacheable(value = "recommendationsCache", key = "#userId")
 * public List<RecommendationDTO> getRecommendationsForUser(String userId) {
 *     // обращение к базе данных выполняется только при первом вызове
 * }
 * }</pre>
 *
 * @see CacheManager
 * @see ConcurrentMapCacheManager
 * @see org.springframework.cache.annotation.Cacheable
 * @see org.springframework.cache.annotation.CacheEvict
 */
@Configuration
public class CacheConfig {

    /**
     * Создаёт и настраивает менеджер кэшей приложения.
     *
     * <p>Каждый кэш идентифицируется по имени и хранится в памяти JVM.
     * Менеджер используется Spring автоматически при встрече аннотаций
     * {@code @Cacheable}, {@code @CacheEvict} и {@code @CachePut}.</p>
     *
     * @return экземпляр {@link CacheManager}, управляющий всеми кэшами приложения
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                "recommendationsCache",
                "ruleCache",
                "ruleStatCache",
                "ruleEvaluationCache",
                "userIdCache"
        );
    }
}

