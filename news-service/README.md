<h1 align="center">News Service</h1>

RESTful сервис для работы с новостями.

* [News API](#news-api)

## Используемые зависимости

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Data Redis
- Spring Web MVC
- Spring Cloud OpenFeign
- PostgreSQL
- Liquibase
- Redis
- JUnit
- Mockito
- Testcontainers
- WireMock
- MapStruct
- Lombok
- Gradle
- Docker
- Swagger (OpenAPI)

### Описание

При реализации сервиса была применена **гексагональная архитектура (hexagonal architecture)**.

Проект разделён на 2 модуля [news-service-api](/news-service-api) и [new-service-core](news-service-core).

Бизнес-логика инкапсулирована в модуле `news-service-core`.

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

Все настройки приложения [вынесены](/news-service-api/src/main/resources) в *.yml.

### Документация

Сервис задокументирован с помощью Swagger (Open API 3.0):

Swagger UI: http://localhost:8081/swagger-ui/index.html

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
  comment-service:
    url: # url for comment-service API, example: "http://comment-service:8082/api/v1"
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

Для смены профиля необходимо в [application.yml](/news-service-api/src/main/resources/application.yaml) поменять
активные профили

```yaml
spring:
  profiles:
    active: profiles # где profiles - необходимые профили
```

## News API

News API предоставляет возможности управления новостями, включая пагинацию, фильтрацию, создание, обновление, удаление
и получение комментариев.

### Описание

API позволяет:

- Получать список новостей с фильтрацией и пагинацией
- Создавать новые новости
- Обновлять существующие новости
- Удалять новости
- Получать новости с комментариями

### Endpoints

#### 1. Получение всех новостей

**GET** `/api/v1/news`

Возвращает список новостей с пагинацией и поддержкой фильтров.

**Параметры запроса:**

- `page` (integer): Номер страницы (по умолчанию `0`)
- `size` (integer): Количество элементов на странице (по умолчанию `20`)
- `sort` (string): Поле для сортировки, например `createdAt,desc`
- `createdAtGte` (date-time): Фильтрация по дате создания (позже указанной даты)
- `createdAtLte` (date-time): Фильтрация по дате создания (до указанной даты)
- `titleContains` (string): Подстрока, содержащаяся в заголовке
- `textContains` (string): Подстрока, содержащаяся в тексте

**Примечание:** Для реализации пагинации используется Scrolling API.

**Ответы:**

- `200`: Успешный ответ с данными новостей

  <details>
  <summary>Пример:</summary>

  ```json
  {
    "empty": false,
    "content": [
      {
        "id": "d9d4b354-65e0-4bcc-8c06-93080eb3cbf8",
        "createdAt": "2025-01-01T14:30:00Z",
        "lastModifiedAt": "2025-01-01T15:00:00Z",
        "title": "Заголовок новости",
        "text": "Текст новости"
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

#### 2. Создание новости

**POST** `/api/v1/news`

Создает новую новость.

**Тело запроса:**

<details>
<summary>Пример:</summary>

```json
{
  "title": "Заголовок новости",
  "text": "Текст новости"
}
```

</details>

**Ответы:**

- `201`: Новость успешно создана

  <details>
  <summary>Пример:</summary>

  ```json
  {
    "id": "d9d4b354-65e0-4bcc-8c06-93080eb3cbf8",
    "createdAt": "2025-01-01T14:30:00Z",
    "lastModifiedAt": "2025-01-01T14:30:00Z",
    "title": "Заголовок новости",
    "text": "Текст новости"
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
    "message": "News with id 'ccf130f4-e7eb-449c-abb9-66531f50af25' not found",
    "time": "2025-01-01T14:30:00Z"
  }
  ```
  </details>

---

#### 3. Получение новости по ID

**GET** `/api/v1/news/{newsId}`

Возвращает новость по уникальному идентификатору.

**Параметры пути:**

- `newsId` (uuid): Уникальный идентификатор новости

**Ответы:**

- `200`: Успешный ответ с данными новости

  <details>
  <summary>Пример:</summary>

  ```json
  {
    "id": "d9d4b354-65e0-4bcc-8c06-93080eb3cbf8",
    "createdAt": "2025-01-01T14:30:00Z",
    "lastModifiedAt": "2025-01-01T15:00:00Z",
    "title": "Заголовок новости",
    "text": "Текст новости"
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
    "message": "News with id 'ccf130f4-e7eb-449c-abb9-66531f50af25' not found",
    "time": "2025-01-01T14:30:00Z"
  }
  ```
  </details>

---

#### 4. Обновление новости

**PUT** `/api/v1/news/{newsId}`

Обновляет существующую новость.

**Параметры пути:**

- `newsId` (uuid): Уникальный идентификатор новости

**Тело запроса:**

<details>
<summary>Пример:</summary>

```json
{
  "title": "Обновленный заголовок",
  "text": "Обновленный текст"
}
```

</details>

**Ответы:**

- `200`: Новость успешно обновлена

  <details>
  <summary>Пример:</summary>

  ```json
  {
    "id": "d9d4b354-65e0-4bcc-8c06-93080eb3cbf8",
    "createdAt": "2025-01-01T14:30:00Z",
    "lastModifiedAt": "2025-01-01T16:00:00Z",
    "title": "Обновленный заголовок",
    "text": "Обновленный текст"
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
    "message": "News with id 'ccf130f4-e7eb-449c-abb9-66531f50af25' not found",
    "time": "2025-01-01T14:30:00Z"
  }
  ```
  </details>

---

#### 5. Удаление новости

**DELETE** `/api/v1/news/{newsId}`

Удаляет новость по уникальному идентификатору.

**Параметры пути:**

- `newsId` (uuid): Уникальный идентификатор новости

**Ответы:**

- `204`: Успешное удаление

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
    "message": "News with id 'ccf130f4-e7eb-449c-abb9-66531f50af25' not found",
    "time": "2025-01-01T14:30:00Z"
  }
  ```
  </details>

---

#### 6. Получение новости с комментариями

**GET** `/api/v1/news/{newsId}/with-comments`

Возвращает новость с комментариями, поддерживает пагинацию комментариев.

**Параметры пути:**

- `newsId` (uuid): Уникальный идентификатор новости

**Параметры запроса:**

- `page` (integer): Номер страницы (по умолчанию `0`)
- `size` (integer): Количество элементов на странице (по умолчанию `20`)
- `sort` (string): Поле для сортировки, например `createdAt,desc`

**Примечание:** Для реализации пагинации используется Scrolling API.

**Ответы:**

- `200`: Успешный ответ с данными новости и комментариев

  <details>
  <summary>Пример:</summary>

  ```json
  {
    "id": "d9d4b354-65e0-4bcc-8c06-93080eb3cbf8",
    "createdAt": "2025-01-01T14:30:00Z",
    "lastModifiedAt": "2025-01-01T15:00:00Z",
    "title": "Заголовок новости",
    "text": "Текст новости",
    "comments": {
      "empty": false,
      "content": [
        {
          "id": "ccf130f4-e7eb-449c-abb9-66531f50af26",
          "createdAt": "2025-01-01T14:30:00Z",
          "lastModifiedAt": "2025-01-01T15:00:00Z",
          "text": "Комментарий",
          "username": "user1"
        }
      ],
      "last": true
    }
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
    "message": "News with id 'ccf130f4-e7eb-449c-abb9-66531f50af25' not found",
    "time": "2025-01-01T14:30:00Z"
  }
  ```
  </details>