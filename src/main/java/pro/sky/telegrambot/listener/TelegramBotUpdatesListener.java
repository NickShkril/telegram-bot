package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final TelegramBot telegramBot;

    private final NotificationTaskRepository notificationTaskRepository;

    @Autowired
    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationTaskRepository notificationTaskRepository) {
        this.telegramBot = telegramBot;
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(this::processUpdate);

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void processUpdate(Update update) {
        logger.info("Processing update: {}", update);

        // Message text from the chat
        String text;
        if (update.message() != null) {
            text = update.message().text();


            // Chat ID
            final Long chatId = update.message().chat().id();

            // If /start was written to the chat, we send a welcome message in response
            if ("/start".equals(text)) {
                sendWelcomeMessage(chatId);
                return;
            }

            // Split the incoming message into date and text
            final Matcher matcher = Pattern.compile("([\\d\\.\\:\\s]{16})(\\s)([\\W+]+)").matcher(text);


            // If the text of the message does not match, we send a message about the incorrectness of the entered data
            if (!matcher.matches()) {
                sendErrorMessage(chatId);
                return;
            }

            // Date and time of notification
            final String notificationDateTimeStr = matcher.group(1);

            // Text of notification
            final String notificationText = matcher.group(3);

            // The pattern does not take into account all cases, so an error may occur when parsing a string in LocalDateTime
            LocalDateTime notificationDateTime;
            try {
                notificationDateTime = LocalDateTime.parse(notificationDateTimeStr, DATE_TIME_FORMATTER);
            } catch (Exception e) {
                sendErrorMessage(chatId);
                return;
            }

            // Create notification
            NotificationTask notificationTask = new NotificationTask(chatId, notificationText, notificationDateTime);

            // Add notification to DB
            notificationTask = notificationTaskRepository.save(notificationTask);

            //Generate notification message
            sendMessage(chatId, "Remind \"%s\" i will send to %s".formatted(notificationTask.getNotificationMessage(), notificationTask.getDateTIme().format(DATE_TIME_FORMATTER)));
        }
    }

    private void sendWelcomeMessage(Long chatId) {
        final String welcomeText = """
                Hello! I can send notifications at your appointed time!
                Please enter your message in the following format:
                "%s Your reminder text"
                """.formatted(LocalDateTime.now().plusMinutes(2).format(DATE_TIME_FORMATTER));
        sendMessage(chatId, welcomeText);
    }

    private void sendErrorMessage(Long chatId) {
        final String errorMessage = """
                    Invalid message entered!
                    Please enter your message in the following format:
                    "%s Your reminder text"
                """.formatted(LocalDateTime.now().plusMinutes(2).format(DATE_TIME_FORMATTER));
        sendMessage(chatId, errorMessage);
    }

    private void sendMessage(Long chatId, String text) {
        telegramBot.execute(new SendMessage(chatId, text));
    }
}



