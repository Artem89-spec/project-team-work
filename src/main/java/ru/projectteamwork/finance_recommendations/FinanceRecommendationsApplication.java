package ru.projectteamwork.finance_recommendations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <h2>FinanceRecommendationsApplication — основной класс запуска Spring Boot приложения</h2>
 *
 * <p>Является точкой входа в систему <b>Finance Recommendations</b> — микросервиса,
 * предназначенного для формирования персонализированных финансовых рекомендаций пользователям
 * на основе их транзакционной активности и правил, задаваемых администраторами.</p>
 *
 * <h3>Основные функции приложения</h3>
 * <ul>
 *     <li>Анализ данных пользователей и их банковских продуктов.</li>
 *     <li>Формирование рекомендаций (инвестиции, накопления, кредиты и др.).</li>
 *     <li>Поддержка динамических правил, задаваемых через REST API.</li>
 *     <li>Интеграция с Telegram-ботом для получения рекомендаций по команде.</li>
 *     <li>Хранение и учёт статистики срабатываний правил.</li>
 * </ul>
 *
 * <h3>Основные компоненты приложения</h3>
 * <ul>
 *     <li><b>Контроллеры</b> — REST API для работы с правилами, рекомендациями и управлением кешем.</li>
 *     <li><b>Сервисы</b> — бизнес-логика формирования рекомендаций и обработки правил.</li>
 *     <li><b>Репозитории</b> — доступ к данным (H2 / PostgreSQL).</li>
 *     <li><b>Правила (rules)</b> — набор классов, описывающих статические сценарии рекомендаций.</li>
 *     <li><b>Telegram-интеграция</b> — модуль взаимодействия с пользователем через чат-бота.</li>
 * </ul>
 *
 * <h3>Механизм запуска</h3>
 * <p>При запуске создаётся контекст Spring, инициализируются все компоненты и
 * поднимается встроенный веб-сервер (по умолчанию Tomcat на порту 8080).</p>
 *
 * <h3>Пример запуска</h3>
 * <pre>
 * $ mvn spring-boot:run
 * </pre>
 *
 * <h3>Точка входа</h3>
 * <p>Метод {@link #main(String[])} вызывает {@link SpringApplication#run(Class, String...)}
 * для старта приложения в стандартном окружении Spring Boot.</p>
 *
 * @author Project Team
 * @version 1.0
 * @since 2025
 */
@SpringBootApplication
public class FinanceRecommendationsApplication {

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки (необязательны)
     */
    public static void main(String[] args) {
        SpringApplication.run(FinanceRecommendationsApplication.class, args);
    }
}

