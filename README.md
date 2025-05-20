# PioneerPixel Banking API Техническое задание

## Основные функции

- **Аутентификация пользователей**: Вход по email и паролю с генерацией JWT-токена.
- **Управление пользователями**: Получение информации о пользователях, включая их контакты (email, телефоны) и счета.
- **Поиск пользователей**: Поиск по имени, телефону, email и дате рождения с поддержкой пагинации.
- **Управление контактами**: Добавление, обновление и удаление email и телефонных номеров пользователей.
- **Переводы денег**: Перевод средств между счетами пользователей с проверкой баланса.
- **Периодическое обновление балансов**: Автоматическое увеличение балансов счетов на 10% каждые 30 секунд (до 2.07x от начального баланса).
- **Интеграция с Kafka**: Отправка событий о переводах в Kafka для асинхронной обработки.
- **Кэширование**: Использование Spring Cache для оптимизации запросов к данным пользователей и контактов.

## Технологии

- **Java 17+**
- **Spring Boot**: Основной фреймворк для создания REST API.
- **Spring Data JPA**: Для работы с базой данных.
- **Spring Transaction Management**: Управление транзакциями с уровнями изоляции (например, `REPEATABLE_READ`).
- **Spring Cache**: Кэширование данных пользователей и контактов.
- **Kafka**: Для асинхронной отправки событий о переводах.
- **Lombok**: Для уменьшения шаблонного кода.
- **SLF4J**: Для логирования.
- **Maven**: Для управления зависимостями и сборки проекта.



## Эндпоинты API

### 1. **Аутентификация**
- **POST /api/users/login**
  - **Описание**: Аутентификация пользователя по email и паролю, возвращает JWT-токен.
  - **Тело запроса**: `UserLoginRequestDto` (email, password)
  - **Ответ**: `UserAuthResponseDto` (JWT-токен) или HTTP 400 при неверных данных.
  - **Пример**:
    ```bash
    curl -X POST http://localhost:8080/api/users/login \
    -H "Content-Type: application/json" \
    -d '{"email":"user@example.com","password":"password123"}'
    ```

### 2. **Получение данных пользователя**
- **GET /api/users/{id}**
  - **Описание**: Получение информации о пользователе по ID.
  - **Параметры**: `id` (Long) — ID пользователя.
  - **Ответ**: `UserDtoResponse` или HTTP 404, если пользователь не найден.
  - **Пример**:
    ```bash
    curl -X GET http://localhost:8080/api/users/1
    ```

- **GET /api/users/{id}/contact**
  - **Описание**: Получение данных пользователя с контактами (email, телефоны, счет).
  - **Параметры**: `id` (Long) — ID пользователя.
  - **Ответ**: `UserContactDtoResponse` или HTTP 404, если пользователь не найден.
  - **Пример**:
    ```bash
    curl -X GET http://localhost:8080/api/users/1/contact
    ```

### 3. **Поиск пользователей**
- **POST /api/users/search**
  - **Описание**: Поиск пользователей по имени, телефону, email и/или дате рождения с пагинацией.
  - **Тело запроса**: `SearchUserDto` (name, phone, email, dateOfBirth, page, size)
  - **Ответ**: `Page<SearchUserDtoResponse>` — список пользователей с пагинацией.
  - **Пример**:
    ```bash
    curl -X POST http://localhost:8080/api/users/search \
    -H "Content-Type: application/json" \
    -d '{"name":"John","phone":"1234567890","email":"john@example.com","dateOfBirth":"1990-01-01","page":0,"size":10}'
    ```

### 4. **Управление email**
- **POST /api/users/email/{id}**
  - **Описание**: Добавление или обновление email для пользователя.
  - **Параметры**: `id` (Long) — ID пользователя.
  - **Тело запроса**: `UserEmailDtoRequest` (email)
  - **Ответ**: `UserEmailDtoResponse` или HTTP 400 при некорректных данных.
  - **Пример**:
    ```bash
    curl -X POST http://localhost:8080/api/users/email/1 \
    -H "Content-Type: application/json" \
    -d '{"email":"newemail@example.com"}'
    ```

- **DELETE /api/users/email/{id}**
  - **Описание**: Удаление email пользователя (не допускается удаление последнего email).
  - **Параметры**: `id` (Long) — ID пользователя.
  - **Тело запроса**: `UserEmailDtoRequest` (email)
  - **Ответ**: HTTP 200 или HTTP 400, если email не найден или это последний email.
  - **Пример**:
    ```bash
    curl -X DELETE http://localhost:8080/api/users/email/1 \
    -H "Content-Type: application/json" \
    -d '{"email":"oldemail@example.com"}'
    ```

### 5. **Управление телефонами**
- **POST /api/users/phone/{id}**
  - **Описание**: Добавление или обновление телефона для пользователя.
  - **Параметры**: `id` (Long) — ID пользователя.
  - **Тело запроса**: `UserPhoneDtoRequest` (phone)
  - **Ответ**: `UserPhoneDtoResponse` или HTTP 400 при некорректных данных.
  - **Пример**:
    ```bash
    curl -X POST http://localhost:8080/api/users/phone/1 \
    -H "Content-Type: application/json" \
    -d '{"phone":"1234567890"}'
    ```

- **DELETE /api/users/phone/{id}**
  - **Описание**: Удаление телефона пользователя (не допускается удаление последнего телефона).
  - **Параметры**: `id` (Long) — ID пользователя.
  - **Тело запроса**: `UserPhoneDtoRequest` (phone)
  - **Ответ**: HTTP 200 или HTTP 400, если телефон не найден или это последний телефон.
  - **Пример**:
    ```bash
    curl -X DELETE http://localhost:8080/api/users/phone/1 \
    -H "Content-Type: application/json" \
    -d '{"phone":"1234567890"}'
    ```

### 6. **Перевод денег**
- **POST /api/users/transfer**
  - **Описание**: Перевод денег между счетами пользователей.
  - **Тело запроса**: `TransferMoneyRequestDto` (fromUserId, toUserId, amount)
  - **Ответ**: HTTP 200 с сообщением об успехе или HTTP 400/500 при ошибке.
  - **Пример**:
    ```bash
    curl -X POST http://localhost:8080/api/users/transfer \
    -H "Content-Type: application/json" \
    -d '{"fromUserId":1,"toUserId":2,"amount":100.00}'
    ```

## Предварительные требования

- **Java 17+**
- **Maven 3.8+**
- **База данных**: PostgreSQL (или другая, поддерживаемая JPA)
- **Kafka**: Для обработки событий переводов
- **Redis** (опционально): Для кэширования, если используется Spring Cache с Redis

## Установка и запуск

1. **Клонирование репозитория**:
   ```bash
   git clone <repository-url>
   cd pioneerpixel
   ```

2. **Настройка базы данных**:
   - Создайте базу данных в PostgreSQL:
     ```sql
     CREATE DATABASE pioneerpixel;
     ```
   - Настройте параметры подключения в `application.properties`:
     ```properties
     spring.datasource.url=jdbc:postgresql://localhost:5432/pioneerpixel
     spring.datasource.username=your_username
     spring.datasource.password=your_password
     spring.jpa.hibernate.ddl-auto=update
     ```

3. **Настройка Kafka**:
   - Убедитесь, что Kafka-брокер запущен (например, на `localhost:9092`).
   - Настройте параметры в `application.properties`:
     ```properties
     spring.kafka.bootstrap-servers=localhost:9092
     spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
     spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
     ```

4. **Сборка и запуск**:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

5. **Проверка API**:
   - API будет доступно по адресу `http://localhost:8080/api/users`.

## Транзакции и изоляция

- **Транзакции**: Все операции с базой данных в сервисах (`UserServiceImpl`, `EmailDataServiceImpl`, `PhoneDataServiceImpl`, `TransferServiceImpl`) используют аннотацию `@Transactional` для обеспечения атомарности.
- **Изоляция**:
  - Метод `searchUser` в `UserServiceImpl` использует `Isolation.REPEATABLE_READ` для предотвращения неконсистентного чтения.
  - Метод `transferMoney` в `TransferServiceImpl` использует транзакции для обеспечения атомарности переводов.
  - Для методов `addEmailOrUpdate`, `deleteEmail`, `addPhoneOrUpdate`, `deletePhone` рекомендуется добавить `Isolation.REPEATABLE_READ` или `SERIALIZABLE` для защиты от конкурентных изменений.

## Замечания по производительности

- **Кэширование**: Методы `getUserById` и `getUserWithContact` используют `@Cacheable` для минимизации обращений к базе. Убедитесь, что кэш инвалидируется при изменении данных (`@CacheEvict` в `EmailDataServiceImpl`, `PhoneDataServiceImpl`).
- **Оптимизация запросов**: В `UserServiceImpl` метод `getUserWithContact` выполняет несколько запросов. Рассмотрите объединение запросов с использованием JPA JOIN для повышения производительности.
- **Блокировки**: Для переводов (`TransferServiceImpl`) рекомендуется добавить пессимистическую блокировку (`SELECT ... FOR UPDATE`) для предотвращения гонок данных.

## Тестирование

- **Unit-тесты**: Используйте JUnit и Mockito для тестирования сервисов и контроллеров.
- **Интеграционные тесты**: Настройте Testcontainers для тестирования с реальной базой данных и Kafka.
- **Конкурентность**: Проведите стресс-тестирование эндпоинтов `/api/users/transfer` и `/api/users/search` для проверки поведения при параллельных запросах.

## Ограничения и рекомендации

- **Ограничения**:
  - Пользователь не может удалить последний email или телефон.
  - Переводы денег проверяют достаточность средств, но не используют явных блокировок в текущей реализации.
- **Рекомендации**:
  - Добавить пессимистическую блокировку в `TransferServiceImpl` для счетов.
  - Использовать `Isolation.SERIALIZABLE` для критических операций, если требуется строгая консистентность.
  - Настроить уникальные индексы для полей `email` и `phone` в таблицах `EmailData` и `PhoneData`.
