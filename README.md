# tg-library

![Java CI with Maven](https://github.com/castroy10/tg-library/actions/workflows/maven.yml/badge.svg)
[![JitPack](https://jitpack.io/v/castroy10/tg-library.svg)](https://jitpack.io/#castroy10/tg-library)
![License](https://img.shields.io/github/license/castroy10/tg-library)
![Java](https://img.shields.io/badge/Java-21-orange)
![GitHub issues](https://img.shields.io/github/issues/castroy10/tg-library)
![GitHub stars](https://img.shields.io/github/stars/castroy10/tg-library?style=social)

[🇷🇺 Русская версия](README_RU.md)

Tg-library is a lightweight Java wrapper library for the Telegram Bots API. It simplifies the creation of Telegram Long Polling bots by providing a convenient Builder pattern and a chat ID-based logic routing system.

## Features

- **Fluent Builder API**: Easy configuration and bot startup using `TgBotBuilder`.
- **Logic Routing**: Routes updates to specific logic handlers based on the `Chat ID`.
- **Logic Abstraction**: A base class for focusing purely on business logic.
- **Minimal Dependencies**: Built on top of `telegrambots`.

## Used Technologies

- Java 21
- Maven
- Telegram Bots API

## Installation

### Build and install to local repository

```bash
git clone https://github.com/castroy10/tg-library.git
cd tg-library
mvn clean install
```

### Add dependency

#### Option 1: Via JitPack (Recommended)

1. Add the JitPack repository to your `pom.xml`:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

2. Add the dependency:
```xml
<dependency>
    <groupId>com.github.castroy10</groupId>
    <artifactId>tg-library</artifactId>
    <version>1.0.1</version>
</dependency>
```

#### Option 2: Local Installation

If you built the library locally using `mvn clean install`:
```xml
<dependency>
    <groupId>ru.castroy10</groupId>
    <artifactId>tg-library</artifactId>
    <version>1.0.1</version>
</dependency>
```

## Usage

### Bot Logic Implementation

Create a class that extends `AbstractBotLogic`. You need to implement methods to return the target Chat ID and to process updates. In the `processUpdate` method, implement your own logic for handling chat messages.

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
            sendMessage("Bot response: " + update.getMessage().getText());
        }
    }
}
```

### Configuration and Startup

Use `TgBotBuilder` to initialize and start the bot:

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

### Handling Unknown Users (Optional)

You can handle updates from users who are not explicitly registered in the main logic list by extending `DefaultBotLogic`.

1. Create a class extending `DefaultBotLogic`:
```java
import ru.castroy10.bot.DefaultBotLogic;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MyUnknownUserLogic extends DefaultBotLogic {
    @Override
    public void processUpdate(Update update) {
        // chatId is automatically set for the current update
        sendMessage("Hello, unknown user!");
    }
}
```

2. Register it using `registerDefaultLogic`:
```java
TgBotBuilder.create()
    // ... token and name configuration
    .registerDefaultLogic(new MyUnknownUserLogic()) 
    .start();
```
Now, all messages whose chatId is missing from the logic classes (or if those classes don't exist at all) will be processed here.
## Architecture

### RouterBot
The `RouterBot` class iterates through the list of registered `AbstractBotLogic` implementations and directs updates to the corresponding handler if the message's `Chat ID` matches the logic's `Chat ID`.

### AbstractBotLogic
Provides base functionality for business logic. All methods automatically use the `chatId` returned by your `getChatId()` method.

#### Sending Text Messages
* **`sendMessage(String text)`** — sends a simple text message.
* **`sendMessage(String text, ReplyKeyboard keyboard)`** — sends text with a keyboard (`InlineKeyboardMarkup` or `ReplyKeyboardMarkup`).

#### Sending Media
All methods accept an `InputFile` (file, URL, or stream) and an optional `caption`.
* **`sendPhoto(InputFile photo, String caption)`** — sends a photo.
* **`sendVideo(InputFile video, String caption)`** — sends a video.
* **`sendAudio(InputFile audio, String caption)`** — sends an audio file (MP3).
* **`sendVoice(InputFile voice, String caption)`** — sends a voice message (OGG).
* **`sendAnimation(InputFile animation, String caption)`** — sends a GIF or silent video.
* **`sendDocument(InputFile document, String caption)`** — sends any file as a document.
* **`sendSticker(InputFile sticker)`** — sends a sticker.

#### Location, Contacts, and Polls
* **`sendLocation(Double latitude, Double longitude)`** — sends a location on the map.
* **`sendContact(String phoneNumber, String firstName, String lastName)`** — sends a contact card.
* **`sendPoll(String question, List<String> options)`** — sends a poll with options.

#### State and Message Management
* **`sendChatAction(ActionType action)`** — displays bot status (e.g., `ActionType.TYPING`, `ActionType.UPLOAD_PHOTO`).
* **`deleteMessage(Integer messageId)`** — deletes a message by its ID.
* **`editMessageText(Integer messageId, String text, ReplyKeyboard keyboard)`** — edits the text and keyboard of an existing message.
* **`editMessageCaption(Integer messageId, String caption, ReplyKeyboard keyboard)`** — edits the caption and keyboard of a media message.
