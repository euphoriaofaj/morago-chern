package com.morago.backend.dto.tokens;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CallSignalMessage {
    private String callId;
    private String callerId;
    private String recipientId;
    private String translatorId;
    private String type;
    private String channelName;
    private Object data;
    private LocalDateTime timestamp;
    private Long themeId;
    private String status;
}