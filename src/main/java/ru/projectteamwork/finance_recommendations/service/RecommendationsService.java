package ru.projectteamwork.finance_recommendations.service;

import ru.projectteamwork.finance_recommendations.dto.RecommendationDTO;

import java.util.List;

/**
 * Сервисный интерфейс для работы с рекомендациями пользователей
 * <p>
 * Определяет контракт для получения списка персональных рекомендаций
 * по заданному идентификатору или имени пользователя.
 * </p>
 *
 * <h3>Основные задачи интерфейса:</h3>
 * <ul>
 *   <li>Предоставление рекомендаций пользователю по его ID или имени;</li>
 *   <li>Инкапсуляция бизнес-логики рекомендаций от уровня контроллеров;</li>
 *   <li>Обеспечение гибкости и возможности подмены реализации.</li>
 * </ul>
 *
 * <h3>Использование:</h3>
 * <pre>
 * RecommendationsService service = ...;
 * List&lt;RecommendationDTO&gt; recos = service.getRecommendationsForUser("Иван Иванов");
 * </pre>
 *
 * @see ru.projectteamwork.finance_recommendations.dto.RecommendationDTO
 */
public interface RecommendationsService {

    /**
     * Возвращает список персональных рекомендаций для указанного пользователя
     *
     * @param userId идентификатор или имя пользователя
     * @return список объектов {@link RecommendationDTO}, содержащих рекомендации;
     *         если пользователь не найден — возвращается пустой список
     */
    List<RecommendationDTO> getRecommendationsForUser(String userId);
}
