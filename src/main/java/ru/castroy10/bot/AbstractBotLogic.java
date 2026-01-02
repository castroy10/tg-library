package ru.castroy10.bot;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Base abstract class for implementing bot logic.
 * Provides helper methods for sending messages and handling updates for a specific chat ID.
 */
public abstract class AbstractBotLogic {

    private static final Logger log = LoggerFactory.getLogger(AbstractBotLogic.class);

    /**
     * Constructs a new AbstractBotLogic instance.
     */
    protected AbstractBotLogic() {
    }

    private RouterBot bot;

    /**
     * Process the incoming update.
     *
     * @param update the update object from Telegram
     */
    public abstract void processUpdate(final Update update);

    /**
     * Get the Chat ID associated with this logic.
     *
     * @return the chat ID
     */
    public abstract Long getChatId();

    /**
     * Send a simple text message.
     *
     * @param text the text to send
     */
    public void sendMessage(final String text) {
        sendMessage(text, null);
    }

    /**
     * Send a text message with a keyboard.
     *
     * @param text     the text to send
     * @param keyboard the keyboard (Reply or Inline) to display
     */
    public void sendMessage(final String text, final ReplyKeyboard keyboard) {
        if (checkBotInit()) return;

        final SendMessage message = new SendMessage();
        message.setChatId(getChatId().toString());
        message.setText(text);
        if (keyboard != null) {
            message.setReplyMarkup(keyboard);
        }

        try {
            bot.execute(message);
        } catch (final TelegramApiException e) {
            log.error("Failed to send message to chat {}: {}", getChatId(), e.getMessage());
        }
    }

    /**
     * Send a photo without a caption.
     *
     * @param photo the photo to send
     */
    public void sendPhoto(final InputFile photo) {
        sendPhoto(photo, null);
    }

    /**
     * Send a photo with a caption.
     *
     * @param photo   the photo to send
     * @param caption the caption for the photo
     */
    public void sendPhoto(final InputFile photo, final String caption) {
        if (checkBotInit()) return;

        final SendPhoto message = new SendPhoto();
        message.setChatId(getChatId().toString());
        message.setPhoto(photo);
        message.setCaption(caption);

        try {
            bot.execute(message);
        } catch (final TelegramApiException e) {
            log.error("Failed to send photo to chat {}: {}", getChatId(), e.getMessage());
        }
    }

    /**
     * Send a video without a caption.
     *
     * @param video the video to send
     */
    public void sendVideo(final InputFile video) {
        sendVideo(video, null);
    }

    /**
     * Send a video with a caption.
     *
     * @param video   the video to send
     * @param caption the caption for the video
     */
    public void sendVideo(final InputFile video, final String caption) {
        if (checkBotInit()) return;

        final SendVideo message = new SendVideo();
        message.setChatId(getChatId().toString());
        message.setVideo(video);
        message.setCaption(caption);

        try {
            bot.execute(message);
        } catch (final TelegramApiException e) {
            log.error("Failed to send video to chat {}: {}", getChatId(), e.getMessage());
        }
    }

    /**
     * Send an audio file without a caption.
     *
     * @param audio the audio file to send
     */
    public void sendAudio(final InputFile audio) {
        sendAudio(audio, null);
    }

    /**
     * Send an audio file with a caption.
     *
     * @param audio   the audio file to send
     * @param caption the caption for the audio
     */
    public void sendAudio(final InputFile audio, final String caption) {
        if (checkBotInit()) return;

        final SendAudio message = new SendAudio();
        message.setChatId(getChatId().toString());
        message.setAudio(audio);
        message.setCaption(caption);

        try {
            bot.execute(message);
        } catch (final TelegramApiException e) {
            log.error("Failed to send audio to chat {}: {}", getChatId(), e.getMessage());
        }
    }

    /**
     * Send a voice note without a caption.
     *
     * @param voice the voice note to send
     */
    public void sendVoice(final InputFile voice) {
        sendVoice(voice, null);
    }

    /**
     * Send a voice note with a caption.
     *
     * @param voice   the voice note to send
     * @param caption the caption for the voice note
     */
    public void sendVoice(final InputFile voice, final String caption) {
        if (checkBotInit()) return;

        final SendVoice message = new SendVoice();
        message.setChatId(getChatId().toString());
        message.setVoice(voice);
        message.setCaption(caption);

        try {
            bot.execute(message);
        } catch (final TelegramApiException e) {
            log.error("Failed to send voice to chat {}: {}", getChatId(), e.getMessage());
        }
    }

    /**
     * Send an animation (GIF) without a caption.
     *
     * @param animation the animation to send
     */
    public void sendAnimation(final InputFile animation) {
        sendAnimation(animation, null);
    }

    /**
     * Send an animation (GIF) with a caption.
     *
     * @param animation the animation to send
     * @param caption   the caption for the animation
     */
    public void sendAnimation(final InputFile animation, final String caption) {
        if (checkBotInit()) return;

        final SendAnimation message = new SendAnimation();
        message.setChatId(getChatId().toString());
        message.setAnimation(animation);
        message.setCaption(caption);

        try {
            bot.execute(message);
        } catch (final TelegramApiException e) {
            log.error("Failed to send animation to chat {}: {}", getChatId(), e.getMessage());
        }
    }

    /**
     * Send a document without a caption.
     *
     * @param document the document to send
     */
    public void sendDocument(final InputFile document) {
        sendDocument(document, null);
    }

    /**
     * Send a document with a caption.
     *
     * @param document the document to send
     * @param caption  the caption for the document
     */
    public void sendDocument(final InputFile document, final String caption) {
        if (checkBotInit()) return;

        final SendDocument message = new SendDocument();
        message.setChatId(getChatId().toString());
        message.setDocument(document);
        message.setCaption(caption);

        try {
            bot.execute(message);
        } catch (final TelegramApiException e) {
            log.error("Failed to send document to chat {}: {}", getChatId(), e.getMessage());
        }
    }

    /**
     * Send a sticker.
     *
     * @param sticker the sticker to send
     */
    public void sendSticker(final InputFile sticker) {
        if (checkBotInit()) return;

        final SendSticker message = new SendSticker();
        message.setChatId(getChatId().toString());
        message.setSticker(sticker);

        try {
            bot.execute(message);
        } catch (final TelegramApiException e) {
            log.error("Failed to send sticker to chat {}: {}", getChatId(), e.getMessage());
        }
    }

    /**
     * Send a location on the map.
     *
     * @param latitude  the latitude
     * @param longitude the longitude
     */
    public void sendLocation(final Double latitude, final Double longitude) {
        if (checkBotInit()) return;

        final SendLocation message = new SendLocation();
        message.setChatId(getChatId().toString());
        message.setLatitude(latitude);
        message.setLongitude(longitude);

        try {
            bot.execute(message);
        } catch (final TelegramApiException e) {
            log.error("Failed to send location to chat {}: {}", getChatId(), e.getMessage());
        }
    }

    /**
     * Send a contact.
     *
     * @param phoneNumber the phone number
     * @param firstName   the first name of the contact
     * @param lastName    the last name of the contact
     */
    public void sendContact(final String phoneNumber, final String firstName, final String lastName) {
        if (checkBotInit()) return;

        final SendContact message = new SendContact();
        message.setChatId(getChatId().toString());
        message.setPhoneNumber(phoneNumber);
        message.setFirstName(firstName);
        message.setLastName(lastName);

        try {
            bot.execute(message);
        } catch (final TelegramApiException e) {
            log.error("Failed to send contact to chat {}: {}", getChatId(), e.getMessage());
        }
    }

    /**
     * Send a poll.
     *
     * @param question the poll question
     * @param options  the list of options
     */
    public void sendPoll(final String question, final List<String> options) {
        if (checkBotInit()) return;

        final SendPoll message = new SendPoll();
        message.setChatId(getChatId().toString());
        message.setQuestion(question);
        message.setOptions(options);

        try {
            bot.execute(message);
        } catch (final TelegramApiException e) {
            log.error("Failed to send poll to chat {}: {}", getChatId(), e.getMessage());
        }
    }

    /**
     * Send a chat action (e.g. typing, uploading photo).
     *
     * @param action the action to display
     */
    public void sendChatAction(final ActionType action) {
        if (checkBotInit()) return;

        final SendChatAction message = new SendChatAction();
        message.setChatId(getChatId().toString());
        message.setAction(action);

        try {
            bot.execute(message);
        } catch (final TelegramApiException e) {
            log.error("Failed to send chat action to chat {}: {}", getChatId(), e.getMessage());
        }
    }

    /**
     * Delete a message by its ID.
     *
     * @param messageId the ID of the message to delete
     */
    public void deleteMessage(final Integer messageId) {
        if (checkBotInit()) return;

        final DeleteMessage message = new DeleteMessage();
        message.setChatId(getChatId().toString());
        message.setMessageId(messageId);

        try {
            bot.execute(message);
        } catch (final TelegramApiException e) {
            log.error("Failed to delete message {} in chat {}: {}", messageId, getChatId(), e.getMessage());
        }
    }

    /**
     * Edit the text of an existing message.
     *
     * @param messageId the ID of the message to edit
     * @param text      the new text
     * @param keyboard  the new inline keyboard (optional)
     */
    public void editMessageText(final Integer messageId, final String text, final ReplyKeyboard keyboard) {
        if (checkBotInit()) return;

        final EditMessageText message = new EditMessageText();
        message.setChatId(getChatId().toString());
        message.setMessageId(messageId);
        message.setText(text);
        if (keyboard instanceof InlineKeyboardMarkup) {
            message.setReplyMarkup((InlineKeyboardMarkup) keyboard);
        }

        try {
            bot.execute(message);
        } catch (final TelegramApiException e) {
            log.error("Failed to edit message text {} in chat {}: {}", messageId, getChatId(), e.getMessage());
        }
    }

    /**
     * Edit the caption of an existing media message.
     *
     * @param messageId the ID of the message to edit
     * @param caption   the new caption
     * @param keyboard  the new inline keyboard (optional)
     */
    public void editMessageCaption(final Integer messageId, final String caption, final ReplyKeyboard keyboard) {
        if (checkBotInit()) return;

        final EditMessageCaption message = new EditMessageCaption();
        message.setChatId(getChatId().toString());
        message.setMessageId(messageId);
        message.setCaption(caption);
        if (keyboard instanceof InlineKeyboardMarkup) {
            message.setReplyMarkup((InlineKeyboardMarkup) keyboard);
        }

        try {
            bot.execute(message);
        } catch (final TelegramApiException e) {
            log.error("Failed to edit message caption {} in chat {}: {}", messageId, getChatId(), e.getMessage());
        }
    }

    /**
     * Sets the RouterBot instance for this logic handler.
     * This method is called automatically when the bot is initialized.
     *
     * @param bot the RouterBot instance to set
     */
    public void setBot(final RouterBot bot) {
        this.bot = bot;
    }

    private boolean checkBotInit() {
        if (bot == null) {
            log.error("Bot instance is not initialized in AbstractBotLogic. Cannot perform action.");
            return true;
        }
        return false;
    }

}
