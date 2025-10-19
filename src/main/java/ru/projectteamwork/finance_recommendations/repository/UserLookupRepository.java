package ru.projectteamwork.finance_recommendations.repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для поиска пользователей по их полному имени.
 * <p>
 * Интерфейс определяет метод для получения уникального идентификатора пользователя
 * ({@link UUID}) на основе переданного полного имени (например, «Иван Иванов»).
 * </p>
 *
 * <h3>Назначение:</h3>
 * <ul>
 *   <li>Обеспечить доступ к данным пользователей по человеко-читаемому идентификатору (ФИО);</li>
 *   <li>Используется сервисами и ботом Telegram для сопоставления имени пользователя с его UUID;</li>
 *   <li>Инкапсулирует логику поиска в базе данных или других источниках данных.</li>
 * </ul>
 *
 * @see java.util.UUID
 * @see java.util.Optional
 */
public interface UserLookupRepository {

    /**
     * Находит идентификатор пользователя по полному имени.
     *
     * @param fullName полное имя пользователя (например, «Иван Иванов»)
     * @return {@link Optional}, содержащий {@link UUID} найденного пользователя,
     *         либо {@link Optional#empty()}, если пользователь не найден
     */
    Optional<UUID> findSingleUserIdByFullName(String fullName);
}

