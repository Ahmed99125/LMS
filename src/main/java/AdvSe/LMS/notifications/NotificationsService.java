package AdvSe.LMS.notifications;

import AdvSe.LMS.users.entities.Student;
import AdvSe.LMS.users.entities.User;
import AdvSe.LMS.users.services.EmailService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationsService {
    private final NotificationsRepository notificationsRepository;
    private final EmailService emailService;

    public NotificationsService(NotificationsRepository notificationsRepository, EmailService emailService) {
        this.notificationsRepository = notificationsRepository;
        this.emailService = emailService;
    }

    public void sendNotification(User user, String title, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        String email = user.getEmail();
        emailService.sendEmail(email, title, message);
        notificationsRepository.save(notification);
    }

    public void sendNotifications(List<Student> users, String title, String message) {
        for (Student user : users) {
            sendNotification(user, title, message);
        }
    }
}
