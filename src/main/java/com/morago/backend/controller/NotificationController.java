package com.morago.backend.controller;

import com.morago.backend.dto.tokens.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

/**
 * WebSocket controller for handling notification operations.
 * Manages sending notifications to specific users or broadcasting to all users.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Handles notification sending requests via WebSocket.
     * Can send to a specific user or broadcast to all users.
     * 
     * @param notificationMessage the notification message to send
     * @param headerAccessor WebSocket session header accessor
     */
    @MessageMapping("/notification.send")
    public void sendNotification(@Payload NotificationMessage notificationMessage,
                                 SimpMessageHeaderAccessor headerAccessor) {
        
        String sender = extractSenderFromSession(headerAccessor);
        NotificationMessage processedMessage = prepareNotificationMessage(notificationMessage, sender);
        
        if (isTargetedNotification(processedMessage)) {
            sendToSpecificUser(processedMessage);
        } else {
            broadcastToAllUsers(processedMessage);
        }
    }

    /**
     * Programmatically sends a notification to a specific user.
     * Used by other services to send notifications.
     * 
     * @param userId the target user ID
     * @param notification the notification message
     */
    public void sendNotificationToUser(String userId, NotificationMessage notification) {
        log.debug("Sending notification to user: {}", userId);
        
        NotificationMessage processedNotification = prepareNotificationMessage(notification, "System");
        messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", processedNotification);
        
        log.info("Notification sent to user: {} with title: {}", userId, notification.getTitle());
    }

    /**
     * Programmatically broadcasts a notification to all connected users.
     * Used by other services to send system-wide notifications.
     * 
     * @param notification the notification message to broadcast
     */
    public void broadcastNotification(NotificationMessage notification) {
        log.debug("Broadcasting notification to all users");
        
        NotificationMessage processedNotification = prepareNotificationMessage(notification, "System");
        messagingTemplate.convertAndSend("/topic/notifications", processedNotification);
        
        log.info("Notification broadcasted with title: {}", notification.getTitle());
    }
    
    /**
     * Extracts sender information from WebSocket session.
     */
    private String extractSenderFromSession(SimpMessageHeaderAccessor headerAccessor) {
        return headerAccessor.getUser() != null ? 
                headerAccessor.getUser().getName() : "System";
    }
    
    /**
     * Prepares notification message with sender and timestamp.
     */
    private NotificationMessage prepareNotificationMessage(NotificationMessage original, String sender) {
        return NotificationMessage.builder()
                .id(original.getId())
                .title(original.getTitle())
                .text(original.getText())
                .recipientId(original.getRecipientId())
                .sender(sender)
                .type(original.getType())
                .data(original.getData())
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Checks if notification is targeted to a specific user.
     */
    private boolean isTargetedNotification(NotificationMessage message) {
        return message.getRecipientId() != null && !message.getRecipientId().trim().isEmpty();
    }
    
    /**
     * Sends notification to a specific user.
     */
    private void sendToSpecificUser(NotificationMessage message) {
        log.debug("Sending targeted notification to user: {}", message.getRecipientId());
        
        messagingTemplate.convertAndSendToUser(
                message.getRecipientId(),
                "/queue/notifications",
                message
        );
        
        log.info("Targeted notification sent to user: {} with title: {}", 
                message.getRecipientId(), message.getTitle());
    }
    
    /**
     * Broadcasts notification to all connected users.
     */
    private void broadcastToAllUsers(NotificationMessage message) {
        log.debug("Broadcasting notification to all users");
        
        messagingTemplate.convertAndSend("/topic/notifications", message);
        
        log.info("Notification broadcasted to all users with title: {}", message.getTitle());
    }
}