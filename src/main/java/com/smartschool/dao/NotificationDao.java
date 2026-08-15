package com.smartschool.dao;

import com.smartschool.model.Notification;
import java.util.List;

public interface NotificationDao extends GenericDao<Notification, Long> {
    List<Notification> findByRecipientId(Long recipientId);
    List<Notification> findUnreadByRecipientId(Long recipientId);
    boolean markAsRead(Long notificationId);
    boolean markAllAsRead(Long recipientId);
    long countUnread(Long recipientId);
    boolean deleteOlderThan(int days);
}
