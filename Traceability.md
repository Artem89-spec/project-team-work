# Requirement Traceability Matrix (v2)

Repository: https://github.com/Artem89-spec/project-team-work

## Legend
- **ReqID** — ID требования (User Story / NFR / API / OPS / BOT)
- **Artifact** — реализация (package/class, endpoint, config)
- **Link** — ссылка на commit/PR/wiki

---

## Functional Requirements (User Stories)

### US-01 — Получение рекомендаций по user_id (REST API)
- **Artifact**: `RecommendationsController#getRecommendations` (`GET /api/recommendations/{userId}`)
- **Commit**: b5643aa99c400817ab81b953613b97099f202ab6
  https://github.com/Artem89-spec/project-team-work/commit/b5643aa99c400817ab81b953613b97099f202ab6

### US-02 — Статические правила (3 продукта)
- **Artifact**: `rules.*` (`CreditRecommendationRule`, `InvestRecommendationRule`, `TopSavingRecommendationRule`)
- **Commit**: b5643aa99c400817ab81b953613b97099f202ab6
  https://github.com/Artem89-spec/project-team-work/commit/b5643aa99c400817ab81b953613b97099f202ab6

### US-03 — Telegram-бот: `/recommend Имя Фамилия`
- **Artifact**: `telegram.RecommendationTelegramBot`, `telegram.BotInitializer`
- **Commit**: 225b1511eff1011533162bbdab9c838f9e6f7917
  https://github.com/Artem89-spec/project-team-work/commit/225b1511eff1011533162bbdab9c838f9e6f7917

### US-04 — Динамические правила (CRUD)
- **Artifact**:
  - API: `RuleController` — `POST /rule`, `GET /rule`, `DELETE /rule/{productId}`
  - Сущности: `DynamicRule`, `DynamicRuleQuery`, `DynamicRuleStat`
  - Сервисы: `RuleService`, `RuleStatService`, `DynamicRuleEvaluator`
- **Commit**: a57fd0a4d8655bfe23c9355b0910837b2adc3d32
  https://github.com/Artem89-spec/project-team-work/commit/a57fd0a4d8655bfe23c9355b0910837b2adc3d32

---

## Non-Functional Requirements

### NFR-01 — Производительность: кэширование и JDBC
- **Artifact**: `RecommendationsRepositoryImpl` (Caffeine + Spring Cache), `JdbcTemplate` SQL
- **Commit**: a57fd0a4d8655bfe23c9355b0910837b2adc3d32
  https://github.com/Artem89-spec/project-team-work/commit/a57fd0a4d8655bfe23c9355b0910837b2adc3d32

### NFR-02 — Liquibase миграции и 2-я БД (R/W)
- **Artifact**: `DefaultDataSourceConfiguration`, `liquibase/changelog-master.yaml`
- **Commit**: a57fd0a4d8655bfe23c9355b0910837b2adc3d32
  https://github.com/Artem89-spec/project-team-work/commit/a57fd0a4d8655bfe23c9355b0910837b2adc3d32

---

## API

### API-01 — `POST /rule`
### API-02 — `GET /rule`
### API-03 — `DELETE /rule/{productId}`
- **Artifact**: `RuleController`, `RuleService`, `RuleMapper`
- **Commit**: a57fd0a4d8655bfe23c9355b0910837b2adc3d32

### API-04 — `GET /rule/stats`
- **Artifact**: `RuleStatsController`, `RuleStatService`
- **Commit**: a57fd0a4d8655bfe23c9355b0910837b2adc3d32

### API-05 — `POST /management/clear-caches`
- **Artifact**: `ManagementController`
- **Commit**: a57fd0a4d8655bfe23c9355b0910837b2adc3d32

### API-06 — `GET /management/info`
- **Artifact**: `InfoController` (Spring Boot build-info)
- **Commit**: 225b1511eff1011533162bbdab9c838f9e6f7917

---

## BOT

### BOT-01 — Приветствие/справка
### BOT-02 — Команда `/recommend {Имя Фамилия}`
- **Artifact**: `RecommendationTelegramBot`, `BotInitializer`
- **Commit**: 225b1511eff1011533162bbdab9c838f9e6f7917

---

## OPS

### OPS-01 — Документация деплоя и env vars
- **Artifact**: GitHub Wiki: `/Deployment`
- **Link**: https://github.com/Artem89-spec/project-team-work/wiki/Deployment

### OPS-02 — `/management/info`
- **Artifact**: `InfoController`
- **Commit**: 225b1511eff1011533162bbdab9c838f9e6f7917

### OPS-03 — `/management/clear-caches`
- **Artifact**: `ManagementController`
- **Commit**: a57fd0a4d8655bfe23c9355b0910837b2adc3d32

---

## Useful Wiki Links
- Home: https://github.com/Artem89-spec/project-team-work/wiki
- Requirements: https://github.com/Artem89-spec/project-team-work/wiki/Requirements
- Architecture: https://github.com/Artem89-spec/project-team-work/wiki/Architecture
- API: https://github.com/Artem89-spec/project-team-work/wiki/API
- Deployment: https://github.com/Artem89-spec/project-team-work/wiki/Deployment
