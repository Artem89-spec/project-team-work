package ru.projectteamwork.finance_recommendations.repository.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.projectteamwork.finance_recommendations.repository.UserLookupRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Реализация репозитория {@link UserLookupRepository} для поиска пользователя по имени и фамилии
 * <p>
 * Использует {@link JdbcTemplate} для выполнения SQL-запросов к базе данных
 * и аннотацию {@link Cacheable} для кеширования результатов запросов
 * </p>
 *
 * <h3>Основное назначение:</h3>
 * <ul>
 *   <li>Находит идентификатор пользователя ({@link UUID}) по его полному имени (ФИО);</li>
 *   <li>Сопоставляет имя и фамилию с соответствующими полями таблицы <code>users</code>;</li>
 *   <li>Используется при обработке запросов от Telegram-бота для связывания имени пользователя с его данными в системе.</li>
 * </ul>
 *
 * <h3>Особенности реализации:</h3>
 * <ul>
 *   <li>Регистр символов не учитывается при поиске (сравнение через <code>UPPER()</code>);</li>
 *   <li>Имя и фамилия разделяются пробелом и извлекаются с помощью функции <code>SPLIT_PART</code> (для PostgreSQL/H2 в режиме совместимости);</li>
 *   <li>Результаты поиска кешируются в кеше <b>userIdCache</b> для ускорения повторных обращений.</li>
 * </ul>
 *
 * <h3>Пример SQL-запроса:</h3>
 * <pre>
 * SELECT u.id
 * FROM users u
 * WHERE UPPER(u.first_name) = UPPER(SPLIT_PART('Иван Иванов', ' ', 1))
 *   AND UPPER(u.last_name)  = UPPER(SPLIT_PART('Иван Иванов', ' ', 2));
 * </pre>
 *
 * @see ru.projectteamwork.finance_recommendations.repository.UserLookupRepository
 * @see org.springframework.jdbc.core.JdbcTemplate
 * @see org.springframework.cache.annotation.Cacheable
 */
@Repository
public class UserLookupRepositoryImpl implements UserLookupRepository {

    private final JdbcTemplate jdbc;

    /**
     * Создаёт экземпляр репозитория с указанным {@link JdbcTemplate}.
     *
     * @param jdbc экземпляр {@link JdbcTemplate}, используемый для выполнения SQL-запросов
     */
    public UserLookupRepositoryImpl(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Выполняет поиск пользователя по его имени и фамилии.
     * <p>
     * Результат кешируется для ускорения повторных запросов.
     * </p>
     *
     * @param fullName полное имя пользователя (например, «Иван Иванов»)
     * @return {@link Optional}, содержащий {@link UUID}, если пользователь найден;
     *         {@link Optional#empty()} — если пользователь не найден или найдено более одного совпадения
     */
    @Override
    @Cacheable(cacheNames = "userIdCache", key = "#fullName")
    public Optional<UUID> findSingleUserIdByFullName(String fullName) {
        String sql = """
            SELECT u.id
            FROM users u
            WHERE UPPER(u.first_name) = UPPER(SPLIT_PART(?, ' ', 1))
              AND UPPER(u.last_name)  = UPPER(SPLIT_PART(?, ' ', 2))
        """;
        List<UUID> ids = jdbc.query(sql, (rs, i) -> UUID.fromString(rs.getString(1)), fullName.trim());
        return ids.size() == 1 ? Optional.of(ids.get(0)) : Optional.empty();
    }
}