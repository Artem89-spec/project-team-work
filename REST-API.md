# 📘 REST API документация

Документация API приложения **finance_recommendations**.

## 🌍 Базовый URL
```
http://localhost:8080
```

## 🔹 Рекомендации

### Получить все рекомендации
**GET** `/api/recommendations/{userId}`  
Возвращает список статических и динамических рекомендаций.

Пример ответа:
```json
{
  "user_id": "8a6f0d12-0b8a-49fb-a0ff-1a5b7b83945b",
  "recommendations": [
    {"name": "Invest 500", "id": "147f6a0f-3b91-413b-ab99-87f081d60d5a", "text": "..."}
  ]
}
```

### Получить только динамические рекомендации
**GET** `/api/recommendations/dynamic/{userId}`

---

## 🔹 Управление правилами

### Список правил
**GET** `/rule`

### Создать правило
**POST** `/rule`
```json
{
  "product_name": "Invest 500",
  "product_id": "147f6a0f-3b91-413b-ab99-87f081d60d5a",
  "product_text": "Текст рекомендации...",
  "rule": [
    {"query": "USER_OF", "arguments": ["DEBIT"], "negate": false}
  ]
}
```

### Удалить правило по product_id
**DELETE** `/rule/{productId}`

---

## 🔹 Статистика правил
**GET** `/rule/stats`  
Возвращает количество срабатываний каждого правила.

---

## 🔹 Управление кешами
**POST** `/management/clear-caches`  
Очищает все кеши приложения.

---

## 🔹 Информация о сборке
**GET** `/management/info`  
Возвращает:
```json
{"name": "finance_recommendations", "version": "1.0.0"}
```

---

## 🔹 Консоль H2
**GET** `/h2-console`  
Веб-интерфейс встроенной базы данных H2.
