package com.morago.backend.dto;

import com.morago.backend.entity.enumFiles.CallStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CallDto {
    private Long id;
    private Long callerId;
    private Long recipientId;
    private Long themeId;

    private LocalDateTime createdAt;
    private int duration;
    private boolean status;
    private BigDecimal sumDecimal;
    private BigDecimal commission;
    private boolean translatorHasJoined;
    private boolean userHasRated;
    private LocalDateTime updatedAt;
    private String channelName;
    private CallStatus callStatus;
//    private boolean isEndCall;
}
