package ru.projectteamwork.finance_recommendations.controller;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h2>ManagementController — служебный контроллер для администрирования приложения</h2>
 *
 * <p>Контроллер предоставляет административные эндпоинты, предназначенные
 * для технического обслуживания и управления состоянием приложения,
 * в частности — для очистки кэшированных данных.</p>
 *
 * <h3>Назначение</h3>
 * <p>Позволяет сбросить все внутренние кэши, используемые сервисами рекомендаций и правил
 * Это необходимо при отладке, обновлении данных, изменении правил или после деплоя новой версии приложения.</p>
 *
 * <h3>Маршрут</h3>
 * <ul>
 *     <li><b>POST /management/clear-caches</b> —
 *     очищает все внутренние кэши, используемые в системе.</li>
 * </ul>
 *
 * <h3>Кэши, которые очищаются:</h3>
 * <ul>
 *     <li><b>recommendationsCache</b> — кэш рекомендаций для пользователей;</li>
 *     <li><b>ruleCache</b> — кэш динамических правил;</li>
 *     <li><b>ruleStatCache</b> — кэш статистики срабатываний правил;</li>
 *     <li><b>ruleEvaluationCache</b> — кэш результатов вычислений правил.</li>
 * </ul>
 *
 * <h3>Пример запроса</h3>
 * <pre>{@code
 * POST /management/clear-caches
 * }</pre>
 *
 * <h3>Пример успешного ответа</h3>
 * <pre>{@code
 * "Кеши успешно очищены"
 * }</pre>
 *
 * <h3>Особенности</h3>
 * <ul>
 *     <li>Метод не требует параметров и не выполняет дополнительной логики —
 *     очистка выполняется автоматически через аннотацию {@link CacheEvict}.</li>
 *     <li>Подходит для использования из Postman, Swagger UI или систем мониторинга.</li>
 * </ul>
 *
 * @see org.springframework.cache.annotation.CacheEvict
 */
@RestController
@RequestMapping("management")
public class ManagementController {

    /**
     * Очищает все внутренние кэши приложения.
     *
     * <p>Аннотация {@link CacheEvict} автоматически удаляет все записи из указанных кэшей.
     * Метод возвращает подтверждение об успешной очистке.</p>
     *
     * @return HTTP-ответ со статусом 200 OK и сообщением о результате операции
     */
    @CacheEvict(
            cacheNames = {
                    "recommendationsCache",
                    "ruleCache",
                    "ruleStatCache",
                    "ruleEvaluationCache"
            },
            allEntries = true
    )
    @PostMapping("/clear-caches")
    public ResponseEntity<String> clearCaches() {
        return ResponseEntity.ok("Кеши успешно очищены");
    }
}


