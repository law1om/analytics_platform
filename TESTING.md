# Руководство по тестированию

## Обзор

Проект использует следующие инструменты:

- **JUnit 5** - unit-тестирование
- **Mockito** - мокирование зависимостей


### Unit Tests

Расположение: `AnalyticsBank/src/test/java/com/bankanalytics/service/`

**Существующие тесты:**
- `UserServiceTest` - 10 тест-кейсов для UserService
- `TaskServiceTest` - 9 тест-кейсов для TaskService


Если тесты падают:
1. Проверьте логи: `target/surefire-reports/`
2. Убедитесь что БД запущена
3. Проверьте `application.yml`
4. Очистите кэш: `mvn clean`
