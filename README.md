# 💡 Finance Recommendations

**Finance Recommendations** — это Spring Boot микросервис, который формирует **персональные финансовые рекомендации** пользователям на основе их транзакций и динамических правил.  
Сервис также интегрируется с Telegram-ботом и предоставляет REST API для управления рекомендациями и правилами.

---

## ⚙️ Основные возможности

- Анализ транзакций пользователя из внешней H2-базы (`transaction`)
- Генерация кредитных, инвестиционных и накопительных рекомендаций
- Гибкая система **динамических правил** (создаются через API)
- Telegram-бот с командой `/recommend Имя Фамилия`
- Кеширование результатов и управление кешами через API
- Встроенная H2-консоль и Liquibase для миграций схемы

---

## 🧰 Технологический стек

- **Java 17+**
- **Spring Boot 3**
- **Spring Data JPA**
- **Liquibase**
- **HikariCP**
- **H2 Database (в памяти и на диске)**
- **Caffeine / ConcurrentMap Cache**
- **TelegramBots API**
- **Springdoc OpenAPI (Swagger UI)**

---

## 🚀 Запуск проекта

### 1️⃣ Клонировать репозиторий

```bash
git clone https://github.com/your-org/finance-recommendations.git
cd finance-recommendations
```

---

### 2️⃣ Проверьте настройки в `application.properties`

```properties
spring.application.name=finance_recommendations

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Основная база (правила + Liquibase + JPA)
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:h2:mem:rules-db;DB_CLOSE_DELAY=-1;MODE=PostgreSQL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:sa}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:}
spring.jpa.hibernate.ddl-auto=none
spring.jpa.open-in-view=false

logging.level.ru.projectteamwork.finance_recommendations=TRACE

spring.liquibase.change-log=classpath:/liquibase/changelog-master.yaml
spring.liquibase.enabled=true

# Вторая база (транзакции, используется RecommendationRepository)
application.recommendations-db.url=jdbc:h2:file:./transaction;MODE=PostgreSQL;AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE

# Telegram-бот
telegram.bot.username=${TELEGRAM_BOT_USERNAME:demo_reco_bot}
telegram.bot.token=${TELEGRAM_BOT_TOKEN:CHANGE_ME}
```

---

### 3️⃣ Установите переменные окружения

```bash
export TELEGRAM_BOT_USERNAME=your_bot_username
export TELEGRAM_BOT_TOKEN=your_bot_token
```

---

### 4️⃣ Запустите приложение

```bash
mvn clean spring-boot:run
```

После запуска:
- Приложение: [http://localhost:8080](http://localhost:8080)
- H2 Console: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 🤖 Telegram-бот

Бот автоматически регистрируется при старте через `BotInitializer`.

**Команда:**
```
/recommend Имя Фамилия
```

**Пример:**
```
/recommend Иван Иванов
```

Бот обращается к API и возвращает рекомендации для указанного пользователя.

---

## 📜 REST API

| Метод | Endpoint | Описание |
|--------|-----------|----------|
| `GET` | `/api/recommendations/{userId}` | Получить все рекомендации (статические и динамические) |
| `GET` | `/api/recommendations/dynamic/{userId}` | Получить только динамические рекомендации |
| `POST` | `/rule` | Создать новое правило |
| `GET` | `/rule` | Получить список всех правил |
| `DELETE` | `/rule/{productId}` | Удалить правило по `productId` |
| `GET` | `/rule/stats` | Получить статистику срабатываний правил |
| `POST` | `/management/clear-caches` | Очистить все кеши |
| `GET` | `/management/info` | Получить информацию о сборке |

---

## 📘 Примеры динамических правил

Файл `dynamic rules.txt` содержит готовые правила для загрузки через API.

### 🟢 Invest 500
```json
{
  "product_name": "Invest 500",
  "product_id": "147f6a0f-3b91-413b-ab99-87f081d60d5a",
  "product_text": "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС)...",
  "rule": [
    {"query": "USER_OF", "arguments": ["DEBIT"], "negate": false},
    {"query": "USER_OF", "arguments": ["INVEST"], "negate": true},
    {"query": "TRANSACTION_SUM_COMPARE", "arguments": ["SAVING", "DEPOSIT", ">", "1000"], "negate": false}
  ]
}
```

### 🟢 Top Saving (вариант 1)
```json
{
  "product_name": "Top Saving",
  "product_id": "59efc529-2fff-41af-baff-90ccd7402925",
  "product_text": "Откройте свою собственную «Копилку» с нашим банком!...",
  "rule": [
    {"query": "USER_OF", "arguments": ["DEBIT"], "negate": false},
    {"query": "TRANSACTION_SUM_COMPARE", "arguments": ["DEBIT", "DEPOSIT", ">=", "50000"], "negate": false},
    {"query": "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW", "arguments": ["DEBIT", "DEPOSIT", ">", "DEBIT", "WITHDRAW"], "negate": false}
  ]
}
```

### 🟢 Простой кредит
```json
{
  "product_name": "Простой кредит",
  "product_id": "ab138afb-f3ba-4a93-b74f-0fcee86d447f",
  "product_text": "Откройте мир выгодных кредитов с нами!...",
  "rule": [
    {"query": "USER_OF", "arguments": ["CREDIT"], "negate": true},
    {"query": "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW", "arguments": ["DEBIT", "DEPOSIT", ">", "DEBIT", "WITHDRAW"], "negate": false},
    {"query": "TRANSACTION_SUM_COMPARE", "arguments": ["DEBIT", "WITHDRAW", ">", "100000"], "negate": false}
  ]
}
```

---

## 📦 Импорт правил из файла

Можно загрузить правила в систему вручную:

```bash
curl -X POST http://localhost:8080/rule   -H "Content-Type: application/json"   -d @dynamic_rules.txt
```

или по одному правилу в отдельном JSON-файле:
```bash
curl -X POST http://localhost:8080/rule   -H "Content-Type: application/json"   -d @rules/invest_500.json
```

---

## 💾 Кеширование

| Кеш | Назначение |
|------|-------------|
| `recommendationsCache` | Кеш рекомендаций по userId |
| `ruleCache` | Кеш динамических правил |
| `ruleStatCache` | Кеш статистики срабатываний |
| `ruleEvaluationCache` | Кеш результатов вычислений |
| `userIdCache` | Кеш соответствия ФИО → userId |

Очистка всех кешей:
```bash
curl -X POST http://localhost:8080/management/clear-caches
```

---

## 🧱 Архитектура

- **Контроллеры** — REST API (`controller`)
- **Сервисы** — бизнес-логика (`service`)
- **Правила** — логика рекомендаций (`rules`)
- **Репозитории** — доступ к БД (`repository`)
- **Evaluator** — вычисление правил (`DynamicRuleEvaluator`)
- **Telegram** — бот (`telegram`)

---

## 📈 Статистика правил

Срабатывания правил сохраняются в таблицу `dynamic_rule_stat`.

Для просмотра статистики:
```bash
GET /rule/stats
```

---

## 🧾 Лицензия

MIT License © 2025 Project TeamWork  
Можно свободно использовать и развивать проект.