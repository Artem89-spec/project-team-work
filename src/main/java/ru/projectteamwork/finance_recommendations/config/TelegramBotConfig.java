package ru.projectteamwork.finance_recommendations.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * <h2>TelegramBotConfig — конфигурация подключения базы данных для Telegram-бота</h2>
 *
 * <p>Данный класс конфигурирует отдельный источник данных и {@link JdbcTemplate},
 * которые используются Telegram-ботом для получения данных о рекомендациях
 * и взаимодействия с аналитической базой (H2/PostgreSQL).</p>
 *
 * <h3>Назначение</h3>
 * <ul>
 *     <li>Создаёт read-only источник данных, предназначенный исключительно для операций чтения.</li>
 *     <li>Обеспечивает Telegram-боту доступ к данным рекомендаций без риска модификации БД.</li>
 *     <li>Отделяет конфигурацию бота от основной конфигурации приложения.</li>
 * </ul>
 *
 * <h3>Использование</h3>
 * <ul>
 *     <li>Telegram-бот использует {@link JdbcTemplate} с квалификатором
 *         <b>"recommendationsJdbcTemplateBot"</b> для выполнения SQL-запросов.</li>
 *     <li>Источник данных подключается с использованием свойств:
 *         <ul>
 *             <li><b>recommendations.db.url</b> — JDBC URL базы данных;</li>
 *             <li><b>recommendations.db.username</b> — имя пользователя (по умолчанию <i>sa</i>);</li>
 *             <li><b>recommendations.db.password</b> — пароль (по умолчанию пустой).</li>
 *         </ul>
 *     </li>
 * </ul>
 *
 * <h3>Пример конфигурации в application.properties:</h3>
 * <pre>{@code
 * recommendations.db.url=jdbc:h2:mem:reco-db;MODE=PostgreSQL
 * recommendations.db.username=sa
 * recommendations.db.password=
 * }</pre>
 *
 * <h3>Особенности</h3>
 * <ul>
 *     <li>Источник данных создаётся с использованием пула HikariCP.</li>
 *     <li>Работает в режиме <b>read-only</b> для повышения безопасности.</li>
 *     <li>Поддерживает кэширование на уровне Spring благодаря аннотации {@link EnableCaching}.</li>
 * </ul>
 *
 * @see HikariDataSource
 * @see JdbcTemplate
 * @see EnableCaching
 */
@Configuration
@EnableCaching
public class TelegramBotConfig {

    /**
     * Создаёт и настраивает источник данных для Telegram-бота.
     *
     * <p>Источник данных работает в режиме "только для чтения" и используется
     * ботом для извлечения пользовательских рекомендаций из БД.</p>
     *
     * @param url  JDBC URL базы данных (например, jdbc:h2:mem:reco-db)
     * @param user имя пользователя базы данных
     * @param pass пароль пользователя базы данных
     * @return экземпляр {@link DataSource}, доступный под квалификатором "recommendationsDataSourceBot"
     */
    @Bean("recommendationsDataSourceBot")
    public DataSource recommendationsDataSource(
            @Value("${recommendations.db.url}") String url,
            @Value("${recommendations.db.username:sa}") String user,
            @Value("${recommendations.db.password:}") String pass
    ) {
        var ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(user);
        ds.setPassword(pass);
        ds.setDriverClassName("org.h2.Driver");
        ds.setReadOnly(true);
        return ds;
    }

    /**
     * Создаёт {@link JdbcTemplate} для выполнения SQL-запросов Telegram-ботом.
     *
     * <p>Использует источник данных, созданный методом {@link #recommendationsDataSource(String, String, String)}.</p>
     *
     * @param dataSource источник данных с квалификатором "recommendationsDataSource"
     * @return экземпляр {@link JdbcTemplate}, доступный под квалификатором "recommendationsJdbcTemplateBot"
     */
    @Bean("recommendationsJdbcTemplateBot")
    public JdbcTemplate recommendationsJdbcTemplate(
            @Qualifier("recommendationsDataSource") DataSource dataSource
    ) {
        return new JdbcTemplate(dataSource);
    }
}
