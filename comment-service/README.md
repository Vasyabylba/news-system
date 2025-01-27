<h1 align="center">Comment Service</h1>

RESTful сервис для работы с комментариями.

* [Comment API](#comment-api)

## Используемые зависимости

- Java 21
- Spring boot
- Gradle
- PostgreSQL
- Liquibase
- Redis
- Docker
- Swagger (OpenAPI 3.0)
- Spring Data JPA
- Spring Data Redis
- Spring Web MVC
- Spring Cloud OpenFeign
- MapStruct
- Lombok

### Описание

При реализации сервиса была применена **гексагональная архитектура (hexagonal architecture)**.

Проект разделён на 2 модуля [comment-service-api](/comment-service-api) и [comment-service-core](comment-service-core).

Бизнес-логика инкапсулирована в модуле `comment-service-core`.

### Подключение к базе данных

В сервисе реализованно подключение к базе данных на основе Spring @Profile (e.g. test & prod).

Параметры подключения к базе данных задаются в свойствах приложения соответствующего профиля:

```yaml
spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    username: ${POSTGRES_USER:postgres}
    password: ${POSTGRES_PASSWORD:postgres}
    url: jdbc:${POSTGRES_CONNECTION:postgresql}://${POSTGRES_HOST:db-news-system:5432}/${POSTGRES_DB:news_system} 
```

### Система управления миграции базы данных Liquibase

В сервис подключена система управления миграции базы данных Liquibase

- при запуске сервиса применяются скрипты на рабочую БД (генерируются необходимые таблицы из одного файла и
  наполняются таблицы данными из другого файла, 20 новостей и 10 комментариев, связанных с каждой новостью)
- при запуске тестов применяется скрипт по генерации необходимых таблиц и так же скрипт с тестовыми данными

### Логирование

В сервисе реализовано логирование запросов и ответов в аспектном стиле для слоя controller, а
также логирование по уровням в отдельных слоях приложения, используя logback, с помощью пользовательского
spring boot стартера.

[Подробнее о spring-boot-logging-starter](../spring-boot-logging-starter)

### Обработка исключений

В сервисе реализована обработка исключений и интерпретации их согласно REST с помощью пользовательского
spring boot стартера.

[Подробнее о spring-boot-exception-handler-starter](../spring-boot-exception-handler-starter)

### Найсройки приложения

Все настройки приложения [вынесены](/comment-service-api/src/main/resources) в *.yml.

### Документация

Сервис задокументирован с помощью Swagger (Open API 3.0):

Swagger UI: http://localhost:8082/swagger-ui/index.html

Код сервиса задокументирован с помощью @JavaDoc.

### Встроенный кэш

В сервисе с помощью Spring AOP реализован встроенный кеша, для хранения сущностей. Доступные алгоритмы кэша:
LRU (Least Recently Used) и LFU (Least Frequently Used).
Алгоритм и максимальный размер коллекции читается из файла application.yml.

Общий алгоритм работы с кешем:

- GET - поиск в кеше данных и если там данных нет, то получить объект из repository, сохранить в кеш и вернуть результат
- POST - сохранение в repository и потом сохранение в кеше
- DELETE - удаление из repository и потом удаление из кеша
- PUT - обновление/вставка в repository и потом обновление/вставка в кеше.

#### Настройки кеша в application.yml

Корневой узел `in-memory-cache`.

| Параметр  | Допустимые значения | Значение по умолчанию | Описание                   |
|:---------:|:-------------------:|:---------------------:|----------------------------|
|  enable   |     true, false     |         false         | Включение и выключение кеш |
| algorithm |      LRU, LFU       |          LRU          | Алгоритм встроенного кеша  |
| capacity  |  0 ... 2147483647   |           3           | Размер коллекции кеша      |

```yaml
in-memory-cache:
  enable: true
  algorithm: LRU
  capacity: 3
```

### Тесты

- Весь код покрыт unit-тестами более 80%, а сервисный слой – 100%.
- Для тестирования взаимодействия с базами данных использовались **testcontainers** (persistence layer)
- Использованы интеграционные тесты
- Реализованы E2E тесты
- Для тестирования интеграций с другими микросервисами использовался WireMock (clients layer)

### Взаимодействие с другими сервисами

Взаимодействие с сервисом [comment](../comment-service) происходит по REST API с помощью Spring Cloud OpenFeign.

Url для взаимодействия указывается в параметрах приложения:

```yaml
external-api:
  news-service:
    url: # url for news-service API, example: "http://news-service:8081/api/v1"
```

### Redis кеш

Сервис настроен для использования базы данных Redis в качестве кеш провайдера, при активном профиле приложения `redis`.

### Профили работы приложения

В приложении настроены профили для разных сценариев использования.

- Профиль demo применяется демонстрации работы сервиса
- Профиль dev применяется для разработки сервиса
- Профиль redis применяется для использования базы данных Redis в качестве кеш провайдера. Данный профиль можно
  совмещать с другими профилями, например: `demo, redis`.
- Профиль prod для production

Для смены профиля необходимо в [application.yml](/comment-service-api/src/main/resources/application.yaml) поменять
активные профили

```yaml
spring:
  profiles:
    active: profiles # где profiles - необходимые профили
```

## Comment API

Comment API предоставляет возможности управления комментариями для новостей, включая создание, обновление, удаление и
фильтрацию с поддержкой пагинации.

### Описание

API позволяет:

- Получать список комментариев с фильтрацией и пагинацией
- Создавать новые комментарии для новостей
- Обновлять существующие комментарии
- Удалять комментарии

### Endpoints

#### 1. Получение всех комментариев к новости

**GET** `/api/v1/news/{newsId}/comments`

Возвращает список комментариев для указанной новости с пагинацией и поддержкой фильтров.

**Параметры пути:**

- `newsId` (uuid): Уникальный идентификатор новости

**Параметры запроса:**

- `page` (integer): Номер страницы (по умолчанию `0`)
- `size` (integer): Количество элементов на странице (по умолчанию `20`)
- `sort` (string): Поле для сортировки, например `createdAt,desc`
- `createdAtGte` (date-time): Фильтрация по дате создания (позже указанной даты)
- `createdAtLte` (date-time): Фильтрация по дате создания (до указанной даты)
- `textContains` (string): Подстрока, содержащаяся в тексте комментария
- `username` (string): Фильтрация по имени пользователя
- `usernameStarts` (string): Фильтрация по началу имени пользователя

**Примечание:** Для реализации пагинации используется Scrolling API.

**Ответы:**

- `200`: Успешный ответ с данными комментариев

  <details>
  <summary>Пример:</summary>

  ```json
  {
    "empty": false,
    "content": [
      {
        "id": "ccf130f4-e7eb-449c-abb9-66531f50af26",
        "createdAt": "2025-01-01T14:30:00Z",
        "lastModifiedAt": "2025-01-01T15:00:00Z",
        "text": "Текст комментария",
        "username": "user1"
      }
    ],
    "last": true
  }
  ```
  </details>

- `400`: Неверный запрос

  <details>
  <summary>Пример:</summary>

  ```json
  {
    "code": 400,
    "status": "BAD_REQUEST",
    "message": "Некорректный запрос",
    "time": "2025-01-01T14:30:00Z"
  }
  ```
  </details>

---

#### 2. Создание комментария

**POST** `/api/v1/news/{newsId}/comments`

Создает новый комментарий для указанной новости.

**Параметры пути:**

- `newsId` (uuid): Уникальный идентификатор новости

**Тело запроса:**

<details>
<summary>Пример:</summary>

```json
{
  "text": "Текст комментария",
  "username": "user1"
}
```

</details>

**Ответы:**

- `201`: Комментарий успешно создан

  <details>
  <summary>Пример:</summary>

  ```json
  {
    "id": "ccf130f4-e7eb-449c-abb9-66531f50af26",
    "createdAt": "2025-01-01T14:30:00Z",
    "lastModifiedAt": "2025-01-01T14:30:00Z",
    "text": "Текст комментария",
    "username": "user1"
  }
  ```
  </details>

- `400`: Неверный запрос

  <details>
  <summary>Пример:</summary>

  ```json
  {
    "code": 400,
    "status": "BAD_REQUEST",
    "message": "Некорректный запрос",
    "time": "2025-01-01T14:30:00Z"
  }
  ```
  </details>

- `422`: Ресурс не найден

  <details>
  <summary>Пример:</summary>

  ```json
  {
    "code": 422,
    "status": "UNPROCESSABLE_ENTITY",
    "message": "Comment with id 'ccf130f4-e7eb-449c-abb9-66531f50af25' not found",
    "time": "2025-01-01T14:30:00Z"
  }
  ```
  </details>

---

#### 3. Получение комментария по ID

**GET** `/api/v1/news/{newsId}/comments/{commentId}`

Возвращает комментарий по уникальному идентификатору.

**Параметры пути:**

- `newsId` (uuid): Уникальный идентификатор новости
- `commentId` (uuid): Уникальный идентификатор комментария

**Ответы:**

- `200`: Успешный ответ с данными комментария

  <details>
  <summary>Пример:</summary>

  ```json
  {
    "id": "ccf130f4-e7eb-449c-abb9-66531f50af26",
    "createdAt": "2025-01-01T14:30:00Z",
    "lastModifiedAt": "2025-01-01T15:00:00Z",
    "text": "Текст комментария",
    "username": "user1"
  }
  ```
  </details>

- `400`: Неверный запрос

  <details>
  <summary>Пример:</summary>

  ```json
  {
    "code": 400,
    "status": "BAD_REQUEST",
    "message": "Некорректный запрос",
    "time": "2025-01-01T14:30:00Z"
  }
  ```
  </details>

- `422`: Ресурс не найден

  <details>
  <summary>Пример:</summary>

  ```json
  {
    "code": 422,
    "status": "UNPROCESSABLE_ENTITY",
    "message": "Comment with id 'ccf130f4-e7eb-449c-abb9-66531f50af25' not found",
    "time": "2025-01-01T14:30:00Z"
  }
  ```
  </details>

---

#### 4. Обновление комментария

**PUT** `/api/v1/news/{newsId}/comments/{commentId}`

Обновляет существующий комментарий.

**Параметры пути:**

- `newsId` (uuid): Уникальный идентификатор новости
- `commentId` (uuid): Уникальный идентификатор комментария

**Тело запроса:**

<details>
<summary>Пример:</summary>

```json
{
  "text": "Обновленный текст комментария",
  "username": "user1"
}
```

</details>

**Ответы:**

- `200`: Комментарий успешно обновлён

  <details>
  <summary>Пример:</summary>

  ```json
  {
    "id": "ccf130f4-e7eb-449c-abb9-66531f50af26",
    "createdAt": "2025-01-01T14:30:00Z",
    "lastModifiedAt": "2025-01-01T16:00:00Z",
    "text": "Обновленный текст комментария",
    "username": "user1"
  }
  ```
  </details>

- `400`: Неверный запрос

  <details>
  <summary>Пример:</summary>

  ```json
  {
    "code": 400,
    "status": "BAD_REQUEST",
    "message": "Некорректный запрос",
    "time": "2025-01-01T14:30:00Z"
  }
  ```
  </details>

- `422`: Ресурс не найден

  <details>
  <summary>Пример:</summary>

  ```json
  {
    "code": 422,
    "status": "UNPROCESSABLE_ENTITY",
    "message": "Comment with id 'ccf130f4-e7eb-449c-abb9-66531f50af25' not found",
    "time": "2025-01-01T14:30:00Z"
  }
  ```
  </details>

---

#### 5. Удаление комментария

**DELETE** `/api/v1/news/{newsId}/comments/{commentId}`

Удаляет комментарий по уникальному идентификатору.

**Параметры пути:**

- `newsId` (uuid): Уникальный идентификатор новости
- `commentId` (uuid): Уникальный идентификатор комментария

**Ответы:**

- `204`: Комментарий успешно удалён

- `400`: Неверный запрос

  <details>
  <summary>Пример:</summary>

  ```json
  {
    "code": 400,
    "status": "BAD_REQUEST",
    "message": "Некорректный запрос",
    "time": "2025-01-01T14:30:00Z"
  }
  ```
  </details>

- `422`: Ресурс не найден

  <details>
  <summary>Пример:</summary>

  ```json
  {
    "code": 422,
    "status": "UNPROCESSABLE_ENTITY",
    "message": "Comment with id 'ccf130f4-e7eb-449c-abb9-66531f50af25' not found",
    "time": "2025-01-01T14:30:00Z"
  }
  ```
  </details>