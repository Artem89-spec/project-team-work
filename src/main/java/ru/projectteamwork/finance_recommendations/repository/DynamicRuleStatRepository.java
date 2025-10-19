package ru.projectteamwork.finance_recommendations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.projectteamwork.finance_recommendations.domain.DynamicRuleStat;

import java.util.UUID;

/**
 * Репозиторий для доступа к статистике срабатываний динамических правил.
 * <p>
 * Использует возможности Spring Data JPA для выполнения стандартных операций CRUD
 * над сущностью {@link DynamicRuleStat}, которая хранит количество активаций каждого динамического правила.
 * </p>
 *
 * <h3>Назначение:</h3>
 * <ul>
 *   <li>Хранение статистики срабатываний динамических правил рекомендаций;</li>
 *   <li>Используется сервисом {@code RuleStatService} для инкремента и чтения статистики;</li>
 *   <li>Позволяет извлекать, сохранять и обновлять записи без написания SQL-кода.</li>
 * </ul>
 *
 * <h3>Основные методы:</h3>
 * <ul>
 *   <li>{@link org.springframework.data.jpa.repository.JpaRepository#findAll()} — получить все записи статистики;</li>
 *   <li>{@link org.springframework.data.jpa.repository.JpaRepository#save(Object)} — сохранить или обновить запись;</li>
 *   <li>{@link org.springframework.data.jpa.repository.JpaRepository#findById(Object)} — получить статистику по ID правила.</li>
 * </ul>
 *
 * @see ru.projectteamwork.finance_recommendations.domain.DynamicRuleStat
 */
public interface DynamicRuleStatRepository extends JpaRepository<DynamicRuleStat, UUID> {
}
