package ru.castroy10;

import java.lang.reflect.Field;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import ru.castroy10.bot.RouterBot;
import java.util.Collections;

class TgBotBuilderTest {

    @Test
    @DisplayName("Successful bot creation")
    void testBuildSuccessful() throws NoSuchFieldException, IllegalAccessException {
        final RouterBot bot = TgBotBuilder.create()
                                          .token("test_token")
                                          .botName("test_bot")
                                          .registerLogic(Collections.emptyList())
                                          .build();

        final Field botToken = DefaultAbsSender.class.getDeclaredField("botToken");
        botToken.setAccessible(true);

        Assertions.assertNotNull(bot);
        Assertions.assertEquals("test_bot", bot.getBotUsername());
        Assertions.assertEquals("test_token", botToken.get(bot));
    }

    @Test
    @DisplayName("Error when creating without token")
    void testBuildFailWithoutToken() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                TgBotBuilder.create()
                            .botName("test_bot")
                            .build()
        );
    }

    @Test
    @DisplayName("Error when creating without name")
    void testBuildFailWithoutName() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                TgBotBuilder.create()
                            .token("test_token")
                            .build()
        );
    }

}
