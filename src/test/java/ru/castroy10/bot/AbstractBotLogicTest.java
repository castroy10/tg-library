package ru.castroy10.bot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

class AbstractBotLogicTest {

    private RouterBot routerBot;
    private AbstractBotLogic botLogic;
    private final Long TEST_CHAT_ID = 12345L;

    @BeforeEach
    void setUp() {
        routerBot = Mockito.mock(RouterBot.class);

        botLogic = new AbstractBotLogic() {
            @Override
            public void processUpdate(final Update update) {
                // do nothing
            }

            @Override
            public Long getChatId() {
                return TEST_CHAT_ID;
            }
        };
        botLogic.setBot(routerBot);
    }

    @Test
    @DisplayName("Send text message")
    void testSendMessage() throws TelegramApiException {
        final String text = "Hello World";

        botLogic.sendMessage(text);

        final ArgumentCaptor<SendMessage> argument = ArgumentCaptor.forClass(SendMessage.class);
        verify(routerBot).execute(argument.capture());

        final SendMessage capturedMessage = argument.getValue();
        assertEquals(TEST_CHAT_ID.toString(), capturedMessage.getChatId());
        assertEquals(text, capturedMessage.getText());
    }

}
