# Bank Analytics Platform

Платформа для автоматизации мониторинга и анализа исполнения стратегических целей подразделений Банка.

## Описание

Данная платформа предоставляет интерактивный дашборд для:
- Мониторинга стратегических целей банка
- Анализа исполнения целей по подразделениям
- Отслеживания прогресса выполнения задач
- Импорт Excel files (Actual plans)
- Управления пользователями и подразделениями
- Генерации аналитических отчетов

## Технологический стек

### Backend
- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Security**
- **PostgreSQL**
- **Apache POI 5.2.5** (работа с Excel)
- **Maven**
- **Lombok**
- **JUnit 5 & Mockito** (тестирование)

### Frontend
- **React 18.2.0**
- **React Router DOM 6.22.0**
- **Recharts 2.12.1** (графики)
- **Vite** 
- **CSS**

## API Endpoints

### Пользователи (Users)
- `GET /users` - Получить всех пользователей
- `GET /users/{id}` - Получить пользователя по ID
- `GET /users/email/{email}` - Получить пользователя по email
- `GET /users/role/{role}` - Получить пользователей по роли
- `GET /users/division/{divisionId}` - Получить пользователей подразделения
- `POST /users` - Создать нового пользователя
- `PUT /users/{id}` - Обновить пользователя
- `DELETE /users/{id}` - Удалить пользователя

### Подразделения (Divisions)
- `GET /divisions` - Получить все подразделения
- `GET /divisions/{id}` - Получить подразделение по ID
- `POST /divisions` - Создать новое подразделение
- `PUT /divisions/{id}` - Обновить подразделение
- `DELETE /divisions/{id}` - Удалить подразделение

### Цели (Goals)
- `GET /goals` - Получить все цели
- `GET /goals/{id}` - Получить цель по ID
- `GET /goals/division/{divisionId}` - Получить цели подразделения
- `POST /goals` - Создать новую цель
- `PUT /goals/{id}` - Обновить цель
- `DELETE /goals/{id}` - Удалить цель

### Задачи (Tasks)
- `GET /tasks` - Получить все задачи
- `GET /tasks/{id}` - Получить задачу по ID
- `GET /tasks/goal/{goalId}` - Получить задачи цели
- `GET /tasks/user/{userId}` - Получить задачи пользователя
- `GET /tasks/status/{status}` - Получить задачи по статусу
- `GET /tasks/overdue` - Получить просроченные задачи
- `POST /tasks` - Создать новую задачу
- `POST /tasks/import` - Импортировать задачи из Excel
- `PUT /tasks/{id}` - Обновить задачу
- `DELETE /tasks/{id}` - Удалить задачу


## Установка и запуск

### Требования

- Java 17+
- Maven 3.8+
- PostgreSQL 14+
- Node.js 18+ & npm

### 1. Настройка базы данных

```sql
CREATE DATABASE bank_analytics;
```

### 3. Настройка Frontend

1. Перейдите в папку frontend:
```bash
cd frontbank
```

2. Установите зависимости:
```bash
npm install
```

3. Запустите dev server:
```bash
npm run dev
```

Frontend будет доступен на `http://localhost:5173`

## Тестирование

Проект включает Unit тесты с использованием JUnit 5 и Mockito.
Подробная информация о тестировании доступна в файле [TESTING.md](TESTING.md).


### Роли пользователей
- **ADMIN** - полный доступ ко всем функциям
- **EMPLOYEE** - ограниченный доступ


1. Использовать IDE с поддержкой Spring Boot (IntelliJ IDEA)

