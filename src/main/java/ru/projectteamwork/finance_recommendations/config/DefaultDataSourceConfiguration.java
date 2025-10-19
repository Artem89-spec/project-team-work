package ru.projectteamwork.finance_recommendations.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * <h2>DefaultDataSourceConfiguration — конфигурация основного источника данных приложения</h2>
 *
 * <p>Этот класс отвечает за создание и настройку основного (primary) источника данных,
 * который используется всеми модулями приложения по умолчанию, если не указан другой {@link javax.sql.DataSource}.</p>
 *
 * <h3>Назначение</h3>
 * <ul>
 *     <li>Создаёт основной источник данных на основе параметров из {@code application.properties} или {@code application.yml}.</li>
 *     <li>Обеспечивает централизованное подключение к основной базе данных приложения.</li>
 *     <li>Поддерживает кэширование (через {@link EnableCaching}) для оптимизации производительности.</li>
 * </ul>
 *
 * <h3>Пример конфигурации в application.properties:</h3>
 * <pre>{@code
 * spring.datasource.url=jdbc:postgresql://localhost:5432/finance_db
 * spring.datasource.username=finance_user
 * spring.datasource.password=secret
 * spring.datasource.driver-class-name=org.postgresql.Driver
 * }</pre>
 *
 * <h3>Особенности</h3>
 * <ul>
 *     <li>Аннотация {@link Primary} делает этот источник данных приоритетным
 *         при внедрении зависимостей в компоненты Spring.</li>
 *     <li>Конфигурация использует {@link DataSourceProperties}, что упрощает интеграцию с Spring Boot.</li>
 *     <li>Совместима с HikariCP, PostgreSQL, H2 и другими драйверами JDBC.</li>
 * </ul>
 *
 * <h3>Использование</h3>
 * <ul>
 *     <li>Источники данных, не имеющие собственного {@code @Qualifier}, будут автоматически получать этот DataSource.</li>
 *     <li>Для специфических подключений (например, модуль рекомендаций или Telegram-бот)
 *         следует использовать отдельные конфигурации, такие как
 *         {@link ru.projectteamwork.finance_recommendations.configuration.RecommendationsDataSourceConfiguration}
 *         или {@link ru.projectteamwork.finance_recommendations.config.TelegramBotConfig}.</li>
 * </ul>
 *
 * @see DataSource
 * @see DataSourceProperties
 * @see EnableCaching
 */
@Configuration
@EnableCaching
public class DefaultDataSourceConfiguration {

    /**
     * Создаёт основной источник данных для приложения.
     *
     * <p>Источник данных инициализируется с помощью {@link DataSourceProperties},
     * которые автоматически читаются из стандартных параметров Spring Boot:
     * {@code spring.datasource.url}, {@code spring.datasource.username}, {@code spring.datasource.password} и т.д.</p>
     *
     * @param properties свойства конфигурации источника данных, предоставляемые Spring Boot
     * @return основной экземпляр {@link DataSource}, доступный под именем "defaultDataSource"
     */
    @Primary
    @Bean(name = "defaultDataSource")
    public DataSource defaultDataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }
}
