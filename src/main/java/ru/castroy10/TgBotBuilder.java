package ru.castroy10;

import java.util.List;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.castroy10.bot.AbstractBotLogic;
import ru.castroy10.bot.DefaultBotLogic;
import ru.castroy10.bot.RouterBot;

/**
 * Builder class for creating and starting a Telegram bot instance.
 */
public class TgBotBuilder {

    /**
     * Private constructor for TgBotBuilder.
     * Use create() method to get a new instance.
     */
    private TgBotBuilder() {
    }

    private String token;
    private String botName;
    private List<AbstractBotLogic> logics;
    private DefaultBotLogic defaultLogic;

    /**
     * Creates a new instance of TgBotBuilder.
     *
     * @return a new TgBotBuilder instance
     */
    public static TgBotBuilder create() {
        return new TgBotBuilder();
    }

    /**
     * Sets the bot token.
     *
     * @param token the bot token provided by BotFather
     * @return the builder instance
     */
    public TgBotBuilder token(final String token) {
        this.token = token;
        return this;
    }

    /**
     * Sets the bot username.
     *
     * @param botName the bot username (without @)
     * @return the builder instance
     */
    public TgBotBuilder botName(final String botName) {
        this.botName = botName;
        return this;
    }

    /**
     * Registers the list of logic handlers for the bot.
     *
     * @param logics the list of AbstractBotLogic handlers
     * @return the builder instance
     */
    public TgBotBuilder registerLogic(final List<AbstractBotLogic> logics) {
        this.logics = logics;
        return this;
    }

    /**
     * Registers the default logic handler for unknown chats.
     *
     * @param defaultLogic the DefaultBotLogic handler
     * @return the builder instance
     */
    public TgBotBuilder registerDefaultLogic(final DefaultBotLogic defaultLogic) {
        this.defaultLogic = defaultLogic;
        return this;
    }

    /**
     * Builds the RouterBot instance without starting the session.
     *
     * @return the configured RouterBot instance
     * @throws IllegalArgumentException if token or botName is missing
     */
    public RouterBot build() {
        validate();
        return new RouterBot(token, botName, logics, defaultLogic);
    }

    /**
     * Validates the configuration and starts the Telegram bot session.
     *
     * @throws TelegramApiException     if the bot registration fails
     * @throws IllegalArgumentException if token or botName is missing
     */
    public void start() throws TelegramApiException {
        validate();
        final RouterBot bot = new RouterBot(token, botName, logics, defaultLogic);
        final TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(bot);
    }

    private void validate() {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token must not be empty");
        }
        if (botName == null || botName.isBlank()) {
            throw new IllegalArgumentException("Bot name must not be empty");
        }
    }
}
