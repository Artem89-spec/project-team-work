package ru.projectteamwork.finance_recommendations.domain.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.projectteamwork.finance_recommendations.domain.DynamicRule;

import java.util.UUID;

/**
 * Интерфейс {@code DynamicRuleRepository} представляет слой доступа к данным (DAO)
 * для сущности {@link ru.projectteamwork.finance_recommendations.domain.DynamicRule}.
 * <p>
 * Расширяет {@link JpaRepository}, что обеспечивает полный набор стандартных CRUD-операций:
 * сохранение, обновление, удаление и поиск динамических правил рекомендаций.
 * </p>
 *
 * <h3>Назначение:</h3>
 * <ul>
 *   <li>Используется для хранения и управления динамическими правилами в базе данных;</li>
 *   <li>Позволяет удалять правила, связанные с конкретным продуктом;</li>
 *   <li>Обеспечивает взаимодействие слоя бизнес-логики {@code RuleService}
 *       с системой хранения данных.</li>
 * </ul>
 *
 * <h3>Типы параметров:</h3>
 * <ul>
 *   <li>Тип сущности — {@link DynamicRule};</li>
 *   <li>Тип первичного ключа — {@link UUID}.</li>
 * </ul>
 *
 * <h3>Пользовательские методы:</h3>
 * <ul>
 *   <li>{@link #deleteByProductId(UUID)} — удаляет правило, связанное с указанным продуктом.</li>
 * </ul>
 *
 * <h3>Пример использования:</h3>
 * <pre>
 * UUID productId = UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925");
 * dynamicRuleRepository.deleteByProductId(productId);
 * </pre>
 *
 * @see ru.projectteamwork.finance_recommendations.domain.DynamicRule
 * @see ru.projectteamwork.finance_recommendations.domain.service.RuleService
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
public interface DynamicRuleRepository extends JpaRepository<DynamicRule, UUID> {

    /**
     * Удаляет динамическое правило по идентификатору продукта, с которым оно связано.
     *
     * @param productId идентификатор продукта, связанного с правилом
     */
    void deleteByProductId(UUID productId);
}
