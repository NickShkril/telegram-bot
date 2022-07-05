package pro.sky.telegrambot.scheduler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class NotificationTaskScheduler {
    Logger logger = LoggerFactory.getLogger(NotificationTaskScheduler.class);


    private final TelegramBot telegramBot;

    private final NotificationTaskRepository notificationTaskRepository;

    @Autowired
    public NotificationTaskScheduler(TelegramBot telegramBot, NotificationTaskRepository notificationTaskRepository) {
        this.telegramBot = telegramBot;
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void sentNotifications() {

        logger.info("Сообщение отправлено");
        final LocalDateTime nowDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        final List<NotificationTask> notificationTaskList = notificationTaskRepository.findAllByDateTime(nowDateTime);
        notificationTaskList.forEach(this::sendNotifications);
    }

    private void sendNotifications(NotificationTask notificationTask) {
        final SendResponse response = telegramBot.execute(new SendMessage(notificationTask.getChatId(), notificationTask.getNotificationMessage()));
        if (response.isOk()) {
            notificationTaskRepository.delete(notificationTask);
        }
    }

}
