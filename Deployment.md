# 🚀 Развёртывание проекта

Этот документ описывает процесс сборки и запуска приложения **finance_recommendations**.

## 🧱 Требования
- Java 17+
- Maven 3.8+
- Git
- (опционально) Docker
- (опционально) Postman

## ⚙️ Конфигурация окружения

| Переменная среды | Назначение | Значение по умолчанию |
|------------------|------------|------------------------|
| SPRING_DATASOURCE_URL | URL базы данных для правил | jdbc:h2:mem:rules-db;MODE=PostgreSQL;DB_CLOSE_DELAY=-1 |
| SPRING_DATASOURCE_USERNAME | Логин | sa |
| SPRING_DATASOURCE_PASSWORD | Пароль |  |
| APPLICATION_RECOMMENDATIONS_DB_URL | URL внешней БД транзакций | jdbc:h2:file:./transaction |
| RECOMMENDATIONS_DB_URL | URL read-only БД транзакций | jdbc:h2:mem:reco-db;MODE=PostgreSQL;DB_CLOSE_DELAY=-1 |
| TELEGRAM_BOT_USERNAME | Имя Telegram-бота | demo_reco_bot |
| TELEGRAM_BOT_TOKEN | Токен бота | CHANGE_ME |

## 🏗️ Сборка проекта

```bash
mvn clean package -DskipTests
```

Результат: `target/finance_recommendations-1.0.0.jar`

## ▶️ Запуск проекта

```bash
java -jar target/finance_recommendations-1.0.0.jar
```

Пример с переменными среды:

```bash
APPLICATION_RECOMMENDATIONS_DB_URL=jdbc:h2:file:./transaction \
TELEGRAM_BOT_TOKEN=123456:ABCDE \
java -jar target/finance_recommendations-1.0.0.jar
```

Приложение запустится на `http://localhost:8080`

## 🧠 Основные эндпоинты

| Endpoint | Метод | Назначение |
|-----------|--------|------------|
| /api/recommendations/{userId} | GET | Получить рекомендации |
| /api/recommendations/dynamic/{userId} | GET | Динамические рекомендации |
| /rule | GET/POST/DELETE | CRUD для динамических правил |
| /rule/stats | GET | Статистика по правилам |
| /management/info | GET | Информация о сборке |
| /management/clear-caches | POST | Очистка кешей |
| /h2-console | GET | Консоль H2 |

## 🧩 Telegram-бот

1. Создайте бота через @BotFather.
2. Укажите токен в `TELEGRAM_BOT_TOKEN`.
3. Запустите приложение и используйте команду `/recommend Имя Фамилия`.

## 🧰 Docker (опционально)

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/finance_recommendations-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

```bash
docker build -t finance-reco .
docker run -p 8080:8080 finance-reco
```

## ✅ Проверка работы

```bash
curl http://localhost:8080/management/info
```

## 🧱 Очистка кешей

```bash
curl -X POST http://localhost:8080/management/clear-caches
```

## 💾 Завершение установки

- REST API и Telegram-бот работают.
- Правила доступны через `/rule`.
- Кеш можно сбросить через `/management/clear-caches`.
