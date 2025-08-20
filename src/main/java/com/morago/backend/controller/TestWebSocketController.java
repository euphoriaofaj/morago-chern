package com.morago.backend.controller;



import com.morago.backend.dto.tokens.NotificationMessage;
import com.morago.backend.dto.tokens.CallSignalMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


            /* Send a test notification to a specific user
            * GET http://localhost:8080/api/test/notification/user123?message=Hello User!*/
    @GetMapping("/notification/{userId}")
    public Map<String, Object> sendTestNotification(
            @PathVariable String userId,
            @RequestParam(defaultValue = "Test notification") String message) {

        NotificationMessage notification = NotificationMessage.builder()
                .id("test-" + System.currentTimeMillis())
                .title("Test Notification")
                .text(message)
                .recipientId(userId)
                .sender("System")
                .type("INFO")
                .timestamp(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", notification);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Notification sent to user: " + userId);
        response.put("notification", notification);

        return response;
    }

    /* Send a broadcast notification to all subscribers
     * GET http://localhost:8080/api/test/broadcast?message=Hello Everyone!
     */
    @GetMapping("/broadcast")
    public Map<String, Object> sendBroadcastNotification(
            @RequestParam(defaultValue = "Broadcast test message") String message) {

        NotificationMessage notification = NotificationMessage.builder()
                .id("broadcast-" + System.currentTimeMillis())
                .title("Broadcast Notification")
                .text(message)
                .sender("System")
                .type("INFO")
                .timestamp(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/topic/notifications", notification);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Broadcast notification sent");
        response.put("notification", notification);

        return response;
    }

    /*
     * Send a test call signal from caller to recipient
     * GET http://localhost:8080/api/test/call/caller123/recipient456?themeId=1
     */
    @GetMapping("/call/{callerId}/{recipientId}")
    public Map<String, Object> sendTestCallSignal(
            @PathVariable String callerId,
            @PathVariable String recipientId,
            @RequestParam(defaultValue = "1") Long themeId) {

        CallSignalMessage callMessage = CallSignalMessage.builder()
                .callId("test-call-" + System.currentTimeMillis())
                .callerId(callerId)
                .recipientId(recipientId)
                .type("CALL_INITIATE")
                .themeId(themeId)
                .channelName("test-channel")
                .timestamp(LocalDateTime.now())
                .status("PENDING")
                .build();

        messagingTemplate.convertAndSendToUser(recipientId, "/queue/calls", callMessage);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Call signal sent from " + callerId + " to " + recipientId);
        response.put("callMessage", callMessage);

        return response;
    }



    @GetMapping("/websocket-health")
    public Map<String, Object> checkWebSocketHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("websocket_enabled", true);
        response.put("timestamp", LocalDateTime.now());
        response.put("endpoints", Map.of(
                "stomp", "/ws",
                "native", "/ws-native"
        ));

        return response;
    }


}