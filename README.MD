# AuthService

Сервис авторизации с регистрацией, подтверждением через email, управлением ролями и безопасной аутентификацией.

## Возможности

- Регистрация с подтверждением кода по email
- Аутентификация по логину и паролю
- Роли: `ADMIN`, `PREMIUM_USER`, `GUEST`
- Access и Refresh токены (срок действия: 15 мин / 7 дней)
- Обновление и отзыв токенов
- Управление ролями (только админ)

## Быстрый старт

### 1. Запуск PostgreSQL через Docker

```bash
docker-compose up -d
```

БД будет доступна на `localhost:5433`  
Данные:
- БД: `userdb`
- Пользователь: `useruser`
- Пароль: `userpass`

### 2. Переменные окружения для SMTP

Создайте `.env` файл в корне проекта:

```
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password
```

> ⚠️ `.env` добавлен в `.gitignore` — он не попадёт в репозиторий.  
> Для Gmail рекомендуется использовать [App Password](https://myaccount.google.com/apppasswords).
`

### 3. Запуск приложения

```bash
mvn spring-boot:run
```

---

##  API

### Регистрация
`POST /auth/register`

```json
{
  "login": "user1",
  "email": "your_email_fore_code",
  "password": "pass"
}
```

###  Подтверждение
`POST /auth/confirm`

```json
{
  "request": {
    "login": "user1",
    "email": "your_email_fore_code",
    "password": "pass"
  },
  "code": "code_from_email"
}
```

### Вход
`POST /auth/login`

```json
{
  "login": "user1",
  "password": "pass"
}
```

➡ Ответ:

```json
{
  "accessToken": "...",
  "refreshToken": "..."
}
```

### ️ Обновление токена
`POST /auth/refresh`

```json
{
  "refreshToken": "..." 
}
```

###  Выход
`POST /auth/logout`

`Authorization: Bearer <access_token>`

---

###  Смена роли (только для ADMIN)
`POST /auth/change-role`

```json
{
  "login": "user1",
  "newRole": "PREMIUM_USER"
}
```

>  Доступно только для пользователей с ролью `ADMIN`.
> Для тестирования можно временно закомментировать проверку роли в `UserServiceImpl#changeRole`.

---

##  Проверка роли

- **Админ** может менять любую роль, включая себе.
- **Обычный пользователь** — не может изменить свою роль.
- Эндпоинт `/auth/change-role` проверяет, имеет ли пользователь роль `ADMIN`.

---

## Зависимости

- Spring Boot 3.5.3
- Spring Security
- Spring Data JPA (Hibernate)
- Spring Web
- Spring Mail
- PostgreSQL Driver
- Dotenv Java (переменные окружения из `.env`)
- Lombok
