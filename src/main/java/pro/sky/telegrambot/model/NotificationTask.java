package pro.sky.telegrambot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class NotificationTask {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;

    private String text;
    private LocalDateTime dateTime;

    public NotificationTask(Long chatId, String notificationMessage, LocalDateTime dateTime) {
        this.chatId = chatId;
        this.text = notificationMessage;
        this.dateTime = dateTime;
    }

    public NotificationTask() {
    }


    public Long getId() {
        return id;
    }

    public Long getChatId() {
        return chatId;
    }

    public String getNotificationMessage() {
        return text;
    }

    public LocalDateTime getDateTIme() {
        return dateTime;
    }


    @Override
    public String toString() {
        return "NotificationTask{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", notificationMessage='" + text + '\'' +
                ", notificationDate=" + dateTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return Objects.equals(id, that.id) && Objects.equals(chatId, that.chatId) && Objects.equals(text, that.text ) && Objects.equals(dateTime, that.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, text, dateTime);
    }
}
