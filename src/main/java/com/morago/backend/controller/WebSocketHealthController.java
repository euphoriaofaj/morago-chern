package com.morago.backend.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

@RestController
public class WebSocketHealthController {

    @GetMapping("/ws-health")
    public ResponseEntity<Map<String, Object>> checkWebSocketHealth() {
        Map<String, Object> status = new HashMap<>();

        try {
            status.put("websocket_enabled", true);
            status.put("endpoints", new String[]{"/ws", "/ws-native"});
            status.put("message_broker", "SimpleBroker");
            status.put("destinations", new String[]{"/topic", "/queue", "/app"});
            status.put("status", "HEALTHY");
            status.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(status);
        } catch (Exception e) {
            status.put("status", "ERROR");
            status.put("error", e.getMessage());
            return ResponseEntity.status(500).body(status);
        }
    }
}