package ru.projectteamwork.finance_recommendations.dto;

/**
 * Класс {@code RecommendationDTO} представляет собой модель данных (Data Transfer Object),
 * описывающую одну финансовую рекомендацию для пользователя
 * <p>
 * Используется для передачи информации о конкретной рекомендации между слоями приложения:
 * сервисом, REST-контроллером и Telegram-ботом.
 * </p>
 *
 * <h3>Состав полей:</h3>
 * <ul>
 *   <li>{@code name} — название финансового продукта или предложения;</li>
 *   <li>{@code id} — уникальный идентификатор рекомендации (например, UUID продукта);</li>
 *   <li>{@code text} — описание или рекламное сообщение, содержащее подробности рекомендации.</li>
 * </ul>
 *
 * <h3>Особенности:</h3>
 * <ul>
 *   <li>Все поля являются неизменяемыми (final);</li>
 *   <li>Класс не содержит бизнес-логики и служит исключительно для передачи данных;</li>
 *   <li>Используется при формировании ответов API и в логике Telegram-бота.</li>
 * </ul>
 *
 * <h3>Пример использования:</h3>
 * <pre>
 * RecommendationDTO recommendation = new RecommendationDTO(
 *     "Top Saving",
 *     "59efc529-2fff-41af-baff-90ccd7402925",
 *     "Откройте свою собственную копилку с выгодными условиями!"
 * );
 * System.out.println(recommendation.getName()); // Top Saving
 * </pre>
 *
 * @see ru.projectteamwork.finance_recommendations.dto.RecommendationsResponse
 * @see ru.projectteamwork.finance_recommendations.service.RecommendationsService
 */
public class RecommendationDTO {

    /** Название рекомендации или продукта. */
    private final String name;

    /** Уникальный идентификатор рекомендации (например, UUID продукта). */
    private final String id;

    /** Подробное описание рекомендации. */
    private final String text;

    /**
     * Создаёт новый объект {@code RecommendationDTO}
     *
     * @param name название рекомендации
     * @param id уникальный идентификатор рекомендации
     * @param text описание или текст рекомендации
     */
    public RecommendationDTO(String name, String id, String text) {
        this.name = name;
        this.id = id;
        this.text = text;
    }

    /** @return название рекомендации */
    public String getName() {
        return name;
    }

    /** @return уникальный идентификатор рекомендации */
    public String getId() {
        return id;
    }

    /** @return описание или рекламный текст рекомендации */
    public String getText() {
        return text;
    }
}
