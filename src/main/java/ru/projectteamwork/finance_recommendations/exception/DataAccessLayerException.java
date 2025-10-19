package ru.projectteamwork.finance_recommendations.exception;

/**
 * Исключение уровня доступа к данным (Data Access Layer Exception)
 * <p>
 * Используется для инкапсуляции и проброса ошибок, возникающих при работе
 * с базой данных или другими источниками данных в слое репозиториев.
 * </p>
 *
 * <h3>Назначение:</h3>
 * <ul>
 *   <li>Упрощает обработку ошибок, возникающих в {@code Repository}-слое;</li>
 *   <li>Позволяет отделить детали реализации БД от бизнес-логики;</li>
 *   <li>Обеспечивает централизованное управление исключениями в приложении.</li>
 * </ul>
 *
 * <h3>Типичное использование:</h3>
 * <pre>
 * try {
 *     jdbcTemplate.queryForObject(sql, Integer.class, userId);
 * } catch (DataAccessException e) {
 *     throw new DataAccessLayerException("Ошибка при доступе к БД", e);
 * }
 * </pre>
 *
 * @see org.springframework.dao.DataAccessException
 * @see ru.projectteamwork.finance_recommendations.repository.impl.RecommendationsRepositoryImpl
 */
public class DataAccessLayerException extends RuntimeException {

    /**
     * Создаёт новое исключение уровня доступа к данным
     *
     * @param message   сообщение об ошибке, описывающее её контекст
     * @param throwable исходное исключение, вызвавшее ошибку
     */
    public DataAccessLayerException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

