package ru.projectteamwork.finance_recommendations.controller;

import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <h2>InfoController — контроллер для предоставления информации о сборке приложения</h2>
 *
 * <p>Данный REST-контроллер используется для получения основной информации
 * о текущей сборке приложения — его имени и версии. Это упрощает мониторинг,
 * проверку состояния и идентификацию развернутой версии сервиса.</p>
 *
 * <h3>Назначение</h3>
 * <p>Контроллер извлекает данные из объекта {@link BuildProperties},
 * который автоматически заполняется Spring Boot на основе файла
 * <b>build-info.properties</b>, создаваемого при сборке проекта Maven или Gradle.</p>
 *
 * <h3>Маршрут</h3>
 * <ul>
 *     <li><b>GET /management/info</b> — возвращает JSON с названием и версией приложения.</li>
 * </ul>
 *
 * <h3>Пример успешного ответа</h3>
 * <pre>{@code
 * {
 *   "name": "finance_recommendations",
 *   "version": "0.0.1-SNAPSHOT"
 * }
 * }</pre>
 *
 * <h3>Использование</h3>
 * <p>Данный эндпоинт удобно использовать в системах мониторинга, CI/CD конвейерах
 * или при диагностике текущего состояния приложения в продакшене.</p>
 *
 * @see BuildProperties
 */
@RestController
@RequestMapping("/management/info")
public class InfoController {

    /**
     * Свойства сборки, автоматически предоставляемые Spring Boot.
     * <p>Содержат метаданные проекта: имя, версию, время сборки и т.д.</p>
     */
    private final BuildProperties buildProperties;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param buildProperties объект, содержащий информацию о сборке приложения
     */
    public InfoController(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    /**
     * Возвращает информацию о текущей сборке приложения.
     *
     * <p>Метод формирует JSON с двумя ключами:</p>
     * <ul>
     *   <li><b>name</b> — имя приложения;</li>
     *   <li><b>version</b> — версия сборки.</li>
     * </ul>
     *
     * @return {@link Map} с информацией о приложении (имя и версия)
     */
    @GetMapping
    public Map<String, String> getInfo() {
        return Map.of(
                "name", buildProperties.getName(),
                "version", buildProperties.getVersion()
        );
    }
}

