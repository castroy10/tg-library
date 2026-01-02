package ru.castroy10.bot;

/**
 * Logic handler for unknown chat IDs.
 * Allows dynamic setting of chatId for the current update processing.
 */
public abstract class DefaultBotLogic extends AbstractBotLogic {

    /**
     * Constructs a new DefaultBotLogic instance.
     */
    protected DefaultBotLogic() {
        super();
    }

    private Long currentChatId;

    /**
     * Sets the Chat ID for the current update.
     * This is used by RouterBot to set the context before processing.
     *
     * @param chatId the chat ID to set
     */
    public void setChatId(final Long chatId) {
        this.currentChatId = chatId;
    }

    @Override
    public Long getChatId() {
        return currentChatId;
    }
}
