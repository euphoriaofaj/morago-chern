package com.morago.backend.controller;

import com.morago.backend.dto.tokens.CallSignalMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.time.LocalDateTime;

@Controller
public class CallSignalingController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/call.initiate")
    public void initiateCall(@Payload CallSignalMessage callMessage,
                             SimpMessageHeaderAccessor headerAccessor) {

        String caller = headerAccessor.getUser() != null ?
                headerAccessor.getUser().getName() : callMessage.getCallerId();

        callMessage.setCallerId(caller);
        callMessage.setTimestamp(LocalDateTime.now());
        callMessage.setType("CALL_INITIATE");

        messagingTemplate.convertAndSendToUser(
                callMessage.getRecipientId(),
                "/queue/calls",
                callMessage
        );
    }

    @MessageMapping("/call.accept")
    public void acceptCall(@Payload CallSignalMessage callMessage,
                           SimpMessageHeaderAccessor headerAccessor) {

        callMessage.setType("CALL_ACCEPTED");
        callMessage.setTimestamp(LocalDateTime.now());

        messagingTemplate.convertAndSendToUser(
                callMessage.getCallerId(),
                "/queue/calls",
                callMessage
        );

        messagingTemplate.convertAndSend(
                "/topic/call-room/" + callMessage.getCallId(),
                callMessage
        );
    }


    @MessageMapping("/call.reject")
    public void rejectCall(@Payload CallSignalMessage callMessage,
                           SimpMessageHeaderAccessor headerAccessor) {

        callMessage.setType("CALL_REJECTED");
        callMessage.setTimestamp(LocalDateTime.now());

        messagingTemplate.convertAndSendToUser(
                callMessage.getCallerId(),
                "/queue/calls",
                callMessage
        );
    }


    @MessageMapping("/call.end")
    public void endCall(@Payload CallSignalMessage callMessage,
                        SimpMessageHeaderAccessor headerAccessor) {

        callMessage.setType("CALL_ENDED");
        callMessage.setTimestamp(LocalDateTime.now());

        messagingTemplate.convertAndSend(
                "/topic/call-room/" + callMessage.getCallId(),
                callMessage
        );
    }


    @MessageMapping("/call.signal/{callId}")
    public void handleSignaling(@DestinationVariable String callId,
                                @Payload CallSignalMessage signalMessage,
                                SimpMessageHeaderAccessor headerAccessor) {

        signalMessage.setCallId(callId);
        signalMessage.setTimestamp(LocalDateTime.now());

        messagingTemplate.convertAndSend(
                "/topic/call-room/" + callId,
                signalMessage
        );
    }


    @MessageMapping("/call.translator.join")
    public void translatorJoin(@Payload CallSignalMessage callMessage,
                               SimpMessageHeaderAccessor headerAccessor) {

        String translatorId = headerAccessor.getUser() != null ?
                headerAccessor.getUser().getName() : callMessage.getTranslatorId();

        callMessage.setTranslatorId(translatorId);
        callMessage.setType("TRANSLATOR_JOINED");
        callMessage.setTimestamp(LocalDateTime.now());

        messagingTemplate.convertAndSend(
                "/topic/call-room/" + callMessage.getCallId(),
                callMessage
        );
    }
}