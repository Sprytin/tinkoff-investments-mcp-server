# Tinkoff Invest MCP Server

MCP сервер для работы с API Тинькофф Инвестиций через Model Context Protocol. Предоставляет инструменты для чтения рыночных данных, выполнения торговых операций и тестирования в песочнице.

## Особенности

- **Полная поддержка Tinkoff Invest API** - доступ ко всем основным сервисам
- **MCP совместимость** - работает с любыми MCP клиентами
- **Модульная архитектура** - разделение на профили для разных типов операций
- **Sandbox режим** - безопасное тестирование с виртуальными деньгами
- **Spring Boot** - надежная и масштабируемая основа

## Возможности

### 📊 Чтение данных (read-mcp)
- **Инструменты**: информация о брендах, фундаментальные метрики, отчеты эмитентов, консенсус прогнозы
- **Рыночные данные**: свечи, стаканы, статусы торгов, последние сделки
- **Пользовательские данные**: аккаунты, тарифы и лимиты

### 💼 Торговля (trade-mcp)
- **Обычные ордеры**: рыночные, лимитные и лучших цен
- **Стоп-ордеры**: с различными типами триггеров и сроками действия

### 🧪 Sandbox (sandbox-mcp)
- **Полный набор инструментов** для тестирования с виртуальными деньгами
- **Управление счетами**: открытие, закрытие, пополнение
- **Тестирование стратегий** без риска реальных денег

## Быстрый старт

### Предварительные требования

- **Java 21** или выше
- **Токен Tinkoff Invest API** ([получить в личном кабинете](https://www.tinkoff.ru/invest/settings/api/))

### Установка

1. **Клонируйте репозиторий:**
```bash
git clone https://github.com/Sprytin/tinkoff-investments-mcp-server.git
cd tinkoff-investments-mcp-server
```

2. **Настройте токен:**
Отредактируйте `src/main/resources/application.yml`:
```yaml
tinkoff:
  token: "ВСТАВИТЬ_ВАШ_ТОКЕН"
```

3. **Соберите проект:**
```bash
./gradlew build
```

## Использование с MCP клиентами

### Claude Desktop

Добавьте в конфигурацию `claude_desktop_config.json`:

```json
{
  "mcpServers": {
    "tinkoff-invest": {
      "command": "java",
      "args": ["-jar", "/path/to/rich-0.0.1-SNAPSHOT.jar"],
      "env": {
        "SPRING_PROFILES_ACTIVE": "read-mcp,sandbox-mcp"
      }
    }
  }
}
```

### Другие MCP клиенты

Сервер использует STDIO транспорт, поэтому совместим с любыми MCP клиентами, поддерживающими этот протокол.

## Конфигурация

### Профили Spring

Сервер поддерживает следующие профили:

- **`read-mcp`** - инструменты чтения данных
- **`trade-mcp`** - торговые инструменты (требует торговый токен)
- **`sandbox-mcp`** - sandbox инструменты

По умолчанию активированы все профили: `read-mcp,trade-mcp,sandbox-mcp`

### Параметры конфигурации

```yaml
spring:
  application:
    name: rich
  ai:
    mcp:
      server:
        name: rich-mcp-server        # Имя сервера
        version: 1.0.0               # Версия
        type: SYNC                   # Тип сервера
        transport: STDIO             # Транспорт STDIO
  profiles:
    active: read-mcp,trade-mcp,sandbox-mcp  # Активные профили

tinkoff:
  token: "ВАШ_ТОКЕН"                 # Токен API
  app-name: rich-mcp-server          # Имя приложения
  target: invest-public-api.tinkoff.ru:443  # Адрес API
```
## API Инструменты

### Чтение данных

#### Инструменты (`InstrumentsTools`)
- `getBrandBy(uid)` - информация о бренде
- `getBrands()` - список брендов
- `getAssetFundamentals(assetUids)` - фундаментальные метрики
- `getAssetReports(instrumentId, from, to)` - отчеты эмитентов
- `getConsensusForecasts(limit, pageNumber)` - консенсус прогнозы
- `getForecastBy(instrumentId)` - прогнозы по инструменту
- `getRiskRates()` - параметры риска

#### Рыночные данные (`MarketDataTools`)
- `getCandles(instrumentId, from, to, interval)` - исторические свечи
- `getOrderBook(instrumentId, depth)` - стакан котировок
- `getTradingStatuses(instrumentIds)` - статусы торгов
- `getLastTrades(instrumentId, from, to)` - последние сделки
- `getClosePrices(instrumentId, from, to)` - цены закрытия
- `getTradingStatus(instrumentId)` - статус торгов по инструменту

#### Пользовательские данные (`UsersTools`)
- `getUserTariff()` - тариф и лимиты
- `getAccounts()` - список аккаунтов

### Торговля

#### Обычные ордеры (`OrdersTools`)
- `postOrder(accountId, instrumentId, quantity, direction, orderType, limitPriceUnits?, limitPriceNano?)` - создать ордер
- `postOrderAsync(...)` - создать ордер асинхронно

#### Стоп-ордеры (`StopOrdersTools`)
- `postStopOrder(accountId, instrumentId, direction, stopOrderType, expirationType, priceUnits, priceNano, stopPriceUnits, stopPriceNano, quantity)` - создать стоп-ордер
- `getStopOrders(accountId)` - список стоп-ордеров
- `cancelStopOrder(accountId, stopOrderId)` - отменить стоп-ордер

### Sandbox

Полный набор инструментов для тестирования:
- Управление счетами: `openSandboxAccount`, `closeSandboxAccount`, `getSandboxAccounts`
- Финансы: `sandboxPayIn`, `getSandboxWithdrawLimits`
- Ордеры: `postSandboxOrder`, `replaceSandboxOrder`, `cancelSandboxOrder`, `getSandboxOrders`, `getSandboxOrderState`
- Портфель: `getSandboxPortfolio`, `getSandboxPositions`
- Операции: `getSandboxOperations`, `getSandboxOperationsByCursor`
- Анализ: `getSandboxMaxLots`

## Примеры использования

### Получение рыночных данных
```kotlin
// Исторические свечи за последний час
getCandles("BBG004730N88", 1704067200, 1704070800, CANDLE_INTERVAL_1_MIN)

// Стакан котировок
getOrderBook("BBG004730N88", 20)

// Статус торгов
getTradingStatus("BBG004730N88")
```

### Создание ордера
```kotlin
// Лимитный ордер на покупку
postOrder(
    accountId = "1234567890",
    instrumentId = "BBG004730N88",
    quantity = 10,
    direction = ORDER_DIRECTION_BUY,
    orderType = ORDER_TYPE_LIMIT,
    limitPriceUnits = 150,
    limitPriceNano = 500000000
)
```

### Работа с sandbox
```kotlin
// Открыть sandbox счет
val account = openSandboxAccount()

// Пополнить счет
sandboxPayIn(account.accountId, 100000, 0, "rub")

// Создать тестовый ордер
postSandboxOrder(
    accountId = account.accountId,
    instrumentId = "BBG004730N88",
    quantity = 1,
    direction = ORDER_DIRECTION_BUY,
    orderType = ORDER_TYPE_MARKET
)
```

## Разработка

### Структура проекта

```
src/main/kotlin/tech/sprytin/rich/
├── api/           # Интерфейсы
├── config/        # Конфигурация
├── read/          # Инструменты чтения
├── trade/         # Торговые инструменты
└── sandbox/       # Sandbox инструменты
```

### Сборка и тестирование

```bash
# Сборка
./gradlew build

# Тестирование
./gradlew test

# Запуск в режиме разработки
./gradlew bootRun
```

## Безопасность

- **Никогда не храните токены в коде** - используйте переменные окружения или конфигурационные файлы
- **Используйте sandbox для тестирования** - перед реальной торговлей проверяйте стратегии
- **Следуйте лимитам API** - проверяйте тариф с помощью `getUserTariff()`

## Лицензия

MIT License - см. [LICENSE](LICENSE) файл.

## Changelog

История изменений доступна в [CHANGELOG.md](CHANGELOG.md).

## Поддержка

- 📖 [Документация Tinkoff Invest API](https://developer.tinkoff.ru/docs/intro)
- 📋 [Contributing Guidelines](CONTRIBUTING.md)
- 🐛 [Issues](https://github.com/Sprytin/tinkoff-investments-mcp-server/issues)
- 💬 [Discussions](https://github.com/Sprytin/tinkoff-investments-mcp-server/discussions)

## Contributing

Приветствуются contributions! Пожалуйста, читайте [CONTRIBUTING.md](CONTRIBUTING.md) перед созданием PR.
