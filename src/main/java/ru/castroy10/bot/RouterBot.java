package ru.castroy10.bot;

import java.util.List;
import java.util.Optional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * The main bot class that routes incoming updates to specific logic handlers based on chat ID.
 * Extends TelegramLongPollingBot.
 */
public class RouterBot extends TelegramLongPollingBot {

    private final String botName;
    private final List<AbstractBotLogic> botLogics;
    private final DefaultBotLogic defaultBotLogic;

    /**
     * Constructs a new RouterBot instance.
     *
     * @param botToken  the bot token provided by BotFather
     * @param botName   the bot username
     * @param botLogics the list of logic handlers to be registered
     */
    public RouterBot(final String botToken, final String botName, final List<AbstractBotLogic> botLogics) {
        this(botToken, botName, botLogics, null);
    }

    /**
     * Constructs a new RouterBot instance.
     *
     * @param botToken        the bot token provided by BotFather
     * @param botName         the bot username
     * @param botLogics       the list of logic handlers to be registered
     * @param defaultBotLogic the optional logic handler for unknown chats
     */
    public RouterBot(final String botToken, final String botName, final List<AbstractBotLogic> botLogics, final DefaultBotLogic defaultBotLogic) {
        super(botToken);
        this.botName = botName;
        this.botLogics = botLogics != null ? botLogics : List.of();
        this.defaultBotLogic = defaultBotLogic;
        this.botLogics.forEach(logic -> logic.setBot(this));
        if (this.defaultBotLogic != null) {
            this.defaultBotLogic.setBot(this);
        }
    }

    /**
     * Returns the bot username.
     *
     * @return the bot username
     */
    @Override
    public String getBotUsername() {
        return botName;
    }

    /**
     * Handles incoming updates.
     * Filters logic based on the chat ID of the message in the update.
     * If a matching logic handler is found, the update is passed to it.
     * If no match is found, uses defaultBotLogic if available.
     *
     * @param update the update received from Telegram
     */
    @Override
    public void onUpdateReceived(final Update update) {
        final Long chatId = getChatIdFromUpdate(update);
        if (chatId == null) {
            return;
        }
        botLogics.stream()
                 .filter(botLogic -> botLogic.getChatId().equals(chatId))
                 .findFirst()
                 .ifPresentOrElse(
                         botLogic -> botLogic.processUpdate(update),
                         () -> {
                             if (defaultBotLogic != null) {
                                 defaultBotLogic.setChatId(chatId);
                                 defaultBotLogic.processUpdate(update);
                             }
                         }
                 );
    }

    private Long getChatIdFromUpdate(final Update upd) {
        if (upd == null) {
            return null;
        }

        return switch (upd) {
            case final Update update when update.hasMessage() -> update.getMessage().getChatId();
            case final Update update when update.hasEditedMessage() -> update.getEditedMessage().getChatId();
            case final Update update when update.hasChannelPost() -> update.getChannelPost().getChatId();
            case final Update update when update.hasEditedChannelPost() -> update.getEditedChannelPost().getChatId();

            case final Update update when update.hasCallbackQuery() -> Optional.ofNullable(update.getCallbackQuery().getMessage())
                                                                               .map(MaybeInaccessibleMessage::getChatId)
                                                                               .orElse(update.getCallbackQuery().getFrom().getId());

            case final Update update when update.hasMyChatMember() -> update.getMyChatMember().getChat().getId();
            case final Update update when update.hasChatMember() -> update.getChatMember().getChat().getId();
            case final Update update when update.hasChatJoinRequest() -> update.getChatJoinRequest().getChat().getId();

            case final Update update when update.getMessageReaction() != null -> update.getMessageReaction().getChat().getId();
            case final Update update when update.getMessageReactionCount() != null -> update.getMessageReactionCount().getChat().getId();

            case final Update update when update.getChatBoost() != null -> update.getChatBoost().getChat().getId();
            case final Update update when update.getRemovedChatBoost() != null -> update.getRemovedChatBoost().getChat().getId();

            case final Update update when update.hasInlineQuery() -> update.getInlineQuery().getFrom().getId();
            case final Update update when update.getChosenInlineQuery() != null -> update.getChosenInlineQuery().getFrom().getId();

            case final Update update when update.hasShippingQuery() -> update.getShippingQuery().getFrom().getId();
            case final Update update when update.hasPreCheckoutQuery() -> update.getPreCheckoutQuery().getFrom().getId();

            case final Update update when update.hasPollAnswer() -> {
                if (update.getPollAnswer().getVoterChat() != null) {
                    yield update.getPollAnswer().getVoterChat().getId();
                }
                yield update.getPollAnswer().getUser().getId();
            }

            default -> null;
        };
    }

}