# tg-library

![Java CI with Maven](https://github.com/castroy10/tg-library/actions/workflows/maven.yml/badge.svg)
[![JitPack](https://jitpack.io/v/castroy10/tg-library.svg)](https://jitpack.io/#castroy10/tg-library)
![License](https://img.shields.io/github/license/castroy10/tg-library)
![Java](https://img.shields.io/badge/Java-21-orange)
![GitHub issues](https://img.shields.io/github/issues/castroy10/tg-library)
![GitHub stars](https://img.shields.io/github/stars/castroy10/tg-library?style=social)

[🇺🇸 English version](README.md)

Tg-library — это легковесная библиотека-обертка для Telegram Bots API на Java. Она упрощает создание Telegram ботов, использующих Long Polling, предоставляя удобный паттерн Builder и систему маршрутизации логики, основанную на ID чата.

## Возможности

- **Fluent Builder API**: Простая конфигурация и запуск бота с помощью `TgBotBuilder`
- **Маршрутизация логики**: Направление обновлений в конкретные обработчики на основе `Chat ID`
- **Абстракция логики**: Базовый класс для фокусировки на бизнес-логике
- **Минимальные зависимости**: Работает поверх `telegrambots`

## Использованные технологии

- Java 21
- Maven
- Telegram Bots API

## Установка

### Сборка и установка в локальный репозиторий

```bash
git clone https://github.com/castroy10/tg-library.git
cd tg-library
mvn clean install
```

### Добавление зависимости

#### Вариант 1: Через JitPack (рекомендуется)

1. Добавьте репозиторий JitPack в ваш `pom.xml`:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

2. Добавьте зависимость:
```xml
<dependency>
    <groupId>com.github.castroy10</groupId>
    <artifactId>tg-library</artifactId>
    <version>1.0.1</version>
</dependency>
```

#### Вариант 2: Локальная установка

Если вы собрали библиотеку локально через `mvn clean install`:
```xml
<dependency>
    <groupId>ru.castroy10</groupId>
    <artifactId>tg-library</artifactId>
    <version>1.0.1</version>
</dependency>
```

## Использование

### Реализация логики бота

Создайте класс, наследующий `AbstractBotLogic`. Необходимо реализовать методы для возвращения ID нужного чата и обработки обновлений. В методе `processUpdate` реализуйте свою логику обработки сообщений из чата.

```java
import ru.castroy10.bot.AbstractBotLogic;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MyCustomLogic extends AbstractBotLogic {

    private final Long targetChatId;

    public MyCustomLogic(Long targetChatId) {
        this.targetChatId = targetChatId;
    }

    @Override
    public Long getChatId() {
        return this.targetChatId;
    }

    @Override
    public void processUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            sendMessage("Ответ от бота: " + update.getMessage().getText());
        }
    }
}
```

### Конфигурация и запуск

Используйте `TgBotBuilder` для инициализации и запуска бота:

```java
import ru.castroy10.TgBotBuilder;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            TgBotBuilder.create()
                .botName("MyBot")
                .token("YOUR_TOKEN")
                .registerLogic(List.of(
                    new MyCustomLogic(123456789L)
                ))
                .start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### Обработка неизвестных пользователей (Опционально)

Вы можете обрабатывать обновления от пользователей, которые не зарегистрированы явно в основном списке логики, унаследовав класс `DefaultBotLogic`.

1. Создайте класс, наследующий `DefaultBotLogic`:
```java
import ru.castroy10.bot.DefaultBotLogic;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MyUnknownUserLogic extends DefaultBotLogic {
    @Override
    public void processUpdate(Update update) {
        // chatId устанавливается автоматически для текущего обновления
        sendMessage("Привет, неизвестный пользователь!");
    }
}
```

2. Зарегистрируйте его с помощью `registerDefaultLogic`:
```java
TgBotBuilder.create()
    // ... конфигурация токена и имени
    .registerDefaultLogic(new MyUnknownUserLogic()) 
    .start();
```
Теперь все сообщения, `chatId` которых отсутствует в классах логики (или этих классов вообще нет) будут обрабатываться здесь.
## Архитектура

### RouterBot
Класс `RouterBot` перебирает список зарегистрированных реализаций `AbstractBotLogic` и направляет обновления в соответствующий обработчик, если `Chat ID` сообщения совпадает с `Chat ID` логики.

### AbstractBotLogic
Предоставляет базовый функционал для бизнес-логики. Все методы автоматически используют `chatId`, возвращаемый вашим методом `getChatId()`.

#### Отправка текстовых сообщений
* **`sendMessage(String text)`** — отправляет простое текстовое сообщение.
* **`sendMessage(String text, ReplyKeyboard keyboard)`** — отправляет текст с клавиатурой (`InlineKeyboardMarkup` или `ReplyKeyboardMarkup`).

#### Отправка медиафайлов
Все методы принимают `InputFile` (файл, URL или stream) и необязательную подпись `caption`.
* **`sendPhoto(InputFile photo, String caption)`** — отправка фотографий.
* **`sendVideo(InputFile video, String caption)`** — отправка видеофайлов.
* **`sendAudio(InputFile audio, String caption)`** — отправка аудиофайлов (MP3).
* **`sendVoice(InputFile voice, String caption)`** — отправка голосовых сообщений (OGG).
* **`sendAnimation(InputFile animation, String caption)`** — отправка GIF или бесшумных видео.
* **`sendDocument(InputFile document, String caption)`** — отправка любых файлов как документов.
* **`sendSticker(InputFile sticker)`** — отправка стикеров.

#### Локация, контакты и опросы
* **`sendLocation(Double latitude, Double longitude)`** — отправка точки на карте по координатам.
* **`sendContact(String phoneNumber, String firstName, String lastName)`** — отправка карточки контакта.
* **`sendPoll(String question, List<String> options)`** — отправка опроса с вариантами ответа.

#### Управление сообщениями и состоянием
* **`sendChatAction(ActionType action)`** — отображение статуса бота (например, `ActionType.TYPING`, `ActionType.UPLOAD_PHOTO`).
* **`deleteMessage(Integer messageId)`** — удаление сообщения по его ID.
* **`editMessageText(Integer messageId, String text, ReplyKeyboard keyboard)`** — изменение текста и клавиатуры существующего текстового сообщения.
* **`editMessageCaption(Integer messageId, String caption, ReplyKeyboard keyboard)`** — изменение подписи и клавиатуры под медиафайлом.

