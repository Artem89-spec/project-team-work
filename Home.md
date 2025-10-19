# 🏦 Finance Recommendations Service

## 📖 Описание проекта
**Finance Recommendations** — это микросервис, формирующий персональные финансовые рекомендации на основе данных пользователя и набора бизнес-правил.  
Сервис поддерживает статические правила (в коде) и динамические (через REST API), а также интеграцию с Telegram-ботом.

---

## ⚙️ Стек технологий
| Компонент | Технология |
|------------|-------------|
| Язык | Java 17 |
| Framework | Spring Boot 3.5 |
| База данных | H2 (режим PostgreSQL) |
| Кеширование | Spring Cache |
| Документация API | OpenAPI (Swagger UI) |
| Миграции | Liquibase |
| Telegram API | TelegramBots |
| Сборка | Maven |

---

## 🔗 Документация Wiki
| Раздел                            | Ссылка | Содержание |
|-----------------------------------|---------|------------|
| [Требования](./Требования.md)     | User Stories и NFR |
| [Traceability](./Traceability.md) | Таблица соответствий требований |
| [Архитектура](./Архитектура.md)   | Диаграммы и описание системы |
| [REST API](./REST-API.md)         | Эндпоинты и примеры запросов |
| [Развёртывание](./Deployment.md)  | Инструкция по запуску |

---

## 🧩 Быстрый старт
```bash
export TELEGRAM_BOT_USERNAME=demo_reco_bot
export TELEGRAM_BOT_TOKEN=YOUR_TOKEN
mvn spring-boot:run
```