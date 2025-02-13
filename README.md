<h1 align="center">News Management System</h1>

RESTful платформа с микросервисной архитектурой для управления новостями и комментариями. Предоставляет функционал для 
просмотра, публикации и редактирования новостей, а также для работы с комментариями пользователей. Система поддерживает 
возможности поиска и фильтрации контента, обеспечивая удобный интерфейс для работы с информацией.

## Технический стек

- Java
- Spring Boot
- Hibernate
- PostgreSQL
- Liquibase
- Redis
- JUnit
- Mockito
- Testcontainers
- WireMock
- Gradle
- Docker
- Swagger (OpenAPI)

## Структура проекта

- [news-service](/news-service)
- [comment-service](/comment-service)
- [spring-boot-exception-handler-starter](/spring-boot-exception-handler-starter)
- [spring-boot-logging-starter](/spring-boot-logging-starter)

## Инструкция по запуску

### С использованием Docker

1. Клонировать Git репозиторий:

    ```bash
    git clone https://github.com/Vasyabylba/news-system.git
    ```

2. Перейти в корневую директорию проекта и запустить docker compose файл для демонстрации работы приложения:

    ```bash
    docker compose -f compose-demo.yaml -p news-system up -d
    ``` 

### Ручной метод

1. Клонировать Git репозиторий:

    ```bash
    git clone https://github.com/Vasyabylba/news-system.git
    ```

2. Перейти в корневую директорию проекта и собрать необходимые зависимости:

   ```bash
   cd news-system
   
   cd spring-boot-exception-handling-starter ./gradlew clean -x test build cd ..
     
   cd spring-boot-logging-starter ./gradlew clean -x test build cd ..
   ```  

3. Для демонстрации работы проекта все необходимые параметры уже настроены в файлах свойств приложений.
   По умолчанию используется профиль `demo` совместно с профилем `redis`.
   При необходимости скорректируйте данные для подключения к базе данных в файле свойств приложения
   `application-demo.yaml` (src/main/resources) для необходимых сервисов.

   ```yaml
   spring:
      datasource:
         driver-class-name: org.postgresql.Driver
         username: ${POSTGRES_USER:postgres}
         password: ${POSTGRES_PASSWORD:postgres}
         url: jdbc:${POSTGRES_CONNECTION:postgresql}://${POSTGRES_HOST:db-news-system:5432}/${POSTGRES_DB:news_system}   
   ```

4. Перейти в корневую директорию проекта и запустить необходимые микросервисы:

   ```bash
   cd news-service ./gradlew clean -x test bootRun
   
   cd comment-service ./gradlew clean -x test bootRun
   ```

## Демонстрация

Для демонстрации работы проект настроен на `demo` режим. Структура таблиц базы данных и данные для неё загружаются
автоматически с помощью скриптов миграции liquibase. В базе данных будут находиться 20 новостей и 10 комментариев,
связанных с каждой новостью.

## Использование Swagger (Open API 3.0)

- `news-service`: http://localhost:8081/swagger-ui/index.html
- `comment-service`: http://localhost:8082/swagger-ui/index.html

## Описание проекта

### News service

News-service - сервис для работы с новостями.

[Подробнее о news-service](/news-service)

### News service

Comment-service - сервис для работы с комментариями к новостям.

[Подробнее о comment-service](/comment-service)

### Spring boot exception handler starter

Spring boot стартер для обработки исключений и интерпретации их согласно REST.

[Подробнее о spring-boot-exception-handler-starter](/spring-boot-exception-handler-starter)

### Logging starter

Spring boot стартер для логирования запросов и ответов в аспектном стиле для слоя controller, а
также логирование по уровням в отдельных слоях приложения, используя logback.

[Подробнее о spring-boot-logging-starter](/spring-boot-logging-starter)

### Используемые зависимости

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