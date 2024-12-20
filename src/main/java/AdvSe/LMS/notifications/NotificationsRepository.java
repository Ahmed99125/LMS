package AdvSe.LMS.notifications;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationsRepository extends JpaRepository<Notification, String> {
}
