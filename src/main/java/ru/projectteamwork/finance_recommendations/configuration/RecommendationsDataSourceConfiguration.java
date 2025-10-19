package ru.projectteamwork.finance_recommendations.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * <h2>RecommendationsDataSourceConfiguration — конфигурация источника данных для модуля рекомендаций</h2>
 *
 * <p>Этот класс конфигурирует отдельный источник данных и {@link JdbcTemplate}
 * для работы с внешней (или вспомогательной) базой данных, содержащей
 * информацию о пользователях, продуктах и транзакциях, используемую
 * в механизме формирования финансовых рекомендаций.</p>
 *
 * <h3>Назначение</h3>
 * <ul>
 *     <li>Создаёт read-only источник данных для аналитической БД рекомендаций.</li>
 *     <li>Предоставляет отдельный {@link JdbcTemplate}, изолированный от основной базы приложения.</li>
 *     <li>Включает механизм кэширования через {@link EnableCaching}.</li>
 * </ul>
 *
 * <h3>Основные компоненты</h3>
 * <ul>
 *     <li><b>recommendationsDataSource</b> — пул соединений HikariCP для чтения данных рекомендаций;</li>
 *     <li><b>recommendationsJdbcTemplate</b> — шаблон JDBC для выполнения SQL-запросов к этой БД.</li>
 * </ul>
 *
 * <h3>Пример настройки в application.properties:</h3>
 * <pre>{@code
 * application.recommendations-db.url=jdbc:h2:mem:reco-db;MODE=PostgreSQL
 * }</pre>
 *
 * <h3>Особенности</h3>
 * <ul>
 *     <li>Использует драйвер H2 (можно заменить на PostgreSQL или иной при необходимости).</li>
 *     <li>Источник данных создаётся в режиме только для чтения для повышения безопасности и стабильности.</li>
 *     <li>Может использоваться сервисами и репозиториями, помеченными {@code @Qualifier("recommendationsJdbcTemplate")}.</li>
 * </ul>
 *
 * @see JdbcTemplate
 * @see HikariDataSource
 * @see EnableCaching
 */
@Configuration
@EnableCaching
public class RecommendationsDataSourceConfiguration {

    /**
     * Создаёт и настраивает источник данных для базы рекомендаций
     *
     * <p>Источник данных основан на {@link HikariDataSource} и работает в режиме "только для чтения".
     * Используется для выполнения аналитических запросов без изменения данных.</p>
     *
     * @param recommendationsUrl JDBC-URL для подключения к базе данных рекомендаций
     * @return настроенный экземпляр {@link DataSource}
     */
    @Bean("recommendationsDataSource")
    public DataSource recommendationsDataSource(
            @Value("${application.recommendations-db.url}") String recommendationsUrl) {
        var dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(recommendationsUrl);
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setReadOnly(true);
        return dataSource;
    }

    /**
     * Создаёт {@link JdbcTemplate}, использующий источник данных для рекомендаций.
     *
     * <p>Этот шаблон используется в репозиториях, работающих с аналитической базой данных.</p>
     *
     * @param dataSource источник данных, созданный методом {@link #recommendationsDataSource(String)}
     * @return экземпляр {@link JdbcTemplate} для запросов к БД рекомендаций
     */
    @Bean("recommendationsJdbcTemplate")
    public JdbcTemplate recommendationsJdbcTemplate(
            @Qualifier("recommendationsDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}


