package ru.castroy10.bot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.mockito.Mockito.*;

class RouterBotTest {

    private AbstractBotLogic logic1;
    private AbstractBotLogic logic2;
    private RouterBot routerBot;

    @BeforeEach
    void setUp() {
        logic1 = Mockito.mock(AbstractBotLogic.class);
        logic2 = Mockito.mock(AbstractBotLogic.class);

        when(logic1.getChatId()).thenReturn(100L);
        when(logic2.getChatId()).thenReturn(200L);

        routerBot = new RouterBot("token", "bot", List.of(logic1, logic2));
    }

    @Test
    @DisplayName("Routing update to the correct logic")
    void testOnUpdateReceivedRoutesCorrectly() {
        final Update update = new Update();
        final Message message = new Message();
        final Chat chat = new Chat();
        chat.setId(100L);
        message.setChat(chat);
        update.setMessage(message);

        routerBot.onUpdateReceived(update);

        verify(logic1, times(1)).processUpdate(update);
        verify(logic2, never()).processUpdate(any());
    }

    @Test
    @DisplayName("Ignore update from unknown chat")
    void testOnUpdateReceivedIgnoresUnknownChat() {
        final Update update = new Update();
        final Message message = new Message();
        final Chat chat = new Chat();
        chat.setId(999L);
        message.setChat(chat);
        update.setMessage(message);

        routerBot.onUpdateReceived(update);

        verify(logic1, never()).processUpdate(any());
        verify(logic2, never()).processUpdate(any());
    }

    @Test
    @DisplayName("Route to DefaultBotLogic for unknown chat")
    void testOnUpdateReceivedRoutesToDefaultLogic() {
        final DefaultBotLogic defaultLogic = Mockito.mock(DefaultBotLogic.class);
        routerBot = new RouterBot("token", "bot", List.of(logic1, logic2), defaultLogic);

        final Update update = new Update();
        final Message message = new Message();
        final Chat chat = new Chat();
        chat.setId(999L);
        message.setChat(chat);
        update.setMessage(message);

        routerBot.onUpdateReceived(update);

        verify(logic1, never()).processUpdate(any());
        verify(logic2, never()).processUpdate(any());
        verify(defaultLogic, times(1)).setChatId(999L);
        verify(defaultLogic, times(1)).processUpdate(update);
    }

    @Test
    @DisplayName("Handle update when botLogics is null")
    void testOnUpdateReceivedWithNullLogics() {
        routerBot = new RouterBot("token", "bot", null);

        final Update update = new Update();
        final Message message = new Message();
        final Chat chat = new Chat();
        chat.setId(100L);
        message.setChat(chat);
        update.setMessage(message);

        Assertions.assertDoesNotThrow(() -> routerBot.onUpdateReceived(update));
    }

    @Test
    @DisplayName("Route to default logic when botLogics is null")
    void testOnUpdateReceivedWithNullLogicsAndDefaultLogic() {
        final DefaultBotLogic defaultLogic = Mockito.mock(DefaultBotLogic.class);
        routerBot = new RouterBot("token", "bot", null, defaultLogic);

        final Update update = new Update();
        final Message message = new Message();
        final Chat chat = new Chat();
        chat.setId(100L);
        message.setChat(chat);
        update.setMessage(message);

        routerBot.onUpdateReceived(update);

        verify(defaultLogic, times(1)).setChatId(100L);
        verify(defaultLogic, times(1)).processUpdate(update);
    }

}
        