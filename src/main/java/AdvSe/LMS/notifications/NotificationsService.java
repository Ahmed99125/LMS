package AdvSe.LMS.notifications;

import AdvSe.LMS.users.entities.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationsService {
    private final NotificationsRepository notificationsRepository;

    public NotificationsService(NotificationsRepository notificationsRepository) {
        this.notificationsRepository = notificationsRepository;
    }

    public void sendNotification(User user, String title, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notificationsRepository.save(notification);
    }

    public void sendNotifications(List<User> users, String title, String message) {
        for (User user : users) {
            sendNotification(user, title, message);
        }
    }
}
