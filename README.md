# Task Management System

## Запуск проекта

### Требования
- Java 21
- Docker
- Docker Compose

### Шаги
1. С клонируйте репозиторий
2. Запустите команду:
    ```shell
    docker-compose up -d
    ```
3. Сборка и запуск приложения с использованием Gradle:
    - Сборка: `./gradlew build`
    - Запуск: `./gradlew bootRun`
4. API будет доступно по адресу `http://localhost:8080`

## Документация API
Документация доступна по адресу `http://localhost:8080/swagger-ui/index.html#/`
