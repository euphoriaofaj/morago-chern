package com.morago.backend.controller;


import com.morago.backend.dto.tokens.NotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.time.LocalDateTime;

@Controller
public class NotificationController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/notification.send")
    public void sendNotification(@Payload NotificationMessage notificationMessage,
                                 SimpMessageHeaderAccessor headerAccessor) {

        String sender = headerAccessor.getUser() != null ?
                headerAccessor.getUser().getName() : "System";

        notificationMessage.setSender(sender);
        notificationMessage.setTimestamp(LocalDateTime.now());

        if (notificationMessage.getRecipientId() != null) {
            messagingTemplate.convertAndSendToUser(
                    notificationMessage.getRecipientId(),
                    "/queue/notifications",
                    notificationMessage
            );
        } else {
            messagingTemplate.convertAndSend("/topic/notifications", notificationMessage);
        }
    }


    public void sendNotificationToUser(String userId, NotificationMessage notification) {
        messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", notification);
    }


    public void broadcastNotification(NotificationMessage notification) {
        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }
}