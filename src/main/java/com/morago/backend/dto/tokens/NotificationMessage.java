package com.morago.backend.dto.tokens;


import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationMessage {
    private String id;
    private String title;
    private String text;
    private String recipientId;
    private String sender;
    private LocalDateTime timestamp;
    private String type;
    private Object data;
}

