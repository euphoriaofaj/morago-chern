package com.morago.backend.controller;

import com.morago.backend.dto.tokens.CallSignalMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

/**
 * WebSocket controller for handling call signaling operations.
 * Manages call initiation, acceptance, rejection, and WebRTC signaling.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class CallSignalingController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Handles call initiation requests.
     * Sends call invitation to the specified recipient.
     * 
     * @param callMessage the call signal message
     * @param headerAccessor WebSocket session header accessor
     */
    @MessageMapping("/call.initiate")
    public void initiateCall(@Payload CallSignalMessage callMessage,
                             SimpMessageHeaderAccessor headerAccessor) {
        
        String caller = extractUserFromSession(headerAccessor, callMessage.getCallerId());
        
        CallSignalMessage initiateMessage = buildCallMessage(callMessage, caller, "CALL_INITIATE");
        
        log.info("Call initiated from {} to {} for call ID: {}", 
                caller, callMessage.getRecipientId(), callMessage.getCallId());
        
        sendToUser(callMessage.getRecipientId(), "/queue/calls", initiateMessage);
    }

    /**
     * Handles call acceptance.
     * Notifies the caller that the call has been accepted and sets up the call room.
     * 
     * @param callMessage the call signal message
     * @param headerAccessor WebSocket session header accessor
     */
    @MessageMapping("/call.accept")
    public void acceptCall(@Payload CallSignalMessage callMessage,
                           SimpMessageHeaderAccessor headerAccessor) {
        
        CallSignalMessage acceptMessage = buildCallMessage(callMessage, null, "CALL_ACCEPTED");
        
        log.info("Call accepted for call ID: {} by recipient: {}", 
                callMessage.getCallId(), callMessage.getRecipientId());
        
        // Notify the caller
        sendToUser(callMessage.getCallerId(), "/queue/calls", acceptMessage);
        
        // Broadcast to call room for other participants
        broadcastToCallRoom(callMessage.getCallId(), acceptMessage);
    }

    /**
     * Handles call rejection.
     * Notifies the caller that the call has been rejected.
     * 
     * @param callMessage the call signal message
     * @param headerAccessor WebSocket session header accessor
     */
    @MessageMapping("/call.reject")
    public void rejectCall(@Payload CallSignalMessage callMessage,
                           SimpMessageHeaderAccessor headerAccessor) {
        
        CallSignalMessage rejectMessage = buildCallMessage(callMessage, null, "CALL_REJECTED");
        
        log.info("Call rejected for call ID: {} by recipient: {}", 
                callMessage.getCallId(), callMessage.getRecipientId());
        
        sendToUser(callMessage.getCallerId(), "/queue/calls", rejectMessage);
    }

    /**
     * Handles call termination.
     * Broadcasts call end notification to all participants in the call room.
     * 
     * @param callMessage the call signal message
     * @param headerAccessor WebSocket session header accessor
     */
    @MessageMapping("/call.end")
    public void endCall(@Payload CallSignalMessage callMessage,
                        SimpMessageHeaderAccessor headerAccessor) {
        
        CallSignalMessage endMessage = buildCallMessage(callMessage, null, "CALL_ENDED");
        
        log.info("Call ended for call ID: {}", callMessage.getCallId());
        
        broadcastToCallRoom(callMessage.getCallId(), endMessage);
    }

    /**
     * Handles WebRTC signaling messages (offer, answer, ICE candidates).
     * Broadcasts signaling data to all participants in the call room.
     * 
     * @param callId the call ID from the URL path
     * @param signalMessage the WebRTC signaling message
     * @param headerAccessor WebSocket session header accessor
     */
    @MessageMapping("/call.signal/{callId}")
    public void handleSignaling(@DestinationVariable String callId,
                                @Payload CallSignalMessage signalMessage,
                                SimpMessageHeaderAccessor headerAccessor) {
        
        signalMessage.setCallId(callId);
        signalMessage.setTimestamp(LocalDateTime.now());
        
        log.debug("WebRTC signaling for call ID: {}, type: {}", callId, signalMessage.getType());
        
        broadcastToCallRoom(callId, signalMessage);
    }

    /**
     * Handles translator joining a call.
     * Notifies all participants that a translator has joined the call.
     * 
     * @param callMessage the call signal message
     * @param headerAccessor WebSocket session header accessor
     */
    @MessageMapping("/call.translator.join")
    public void translatorJoin(@Payload CallSignalMessage callMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        
        String translatorId = extractUserFromSession(headerAccessor, callMessage.getTranslatorId());
        
        CallSignalMessage joinMessage = buildCallMessage(callMessage, null, "TRANSLATOR_JOINED");
        joinMessage.setTranslatorId(translatorId);
        
        log.info("Translator {} joined call ID: {}", translatorId, callMessage.getCallId());
        
        broadcastToCallRoom(callMessage.getCallId(), joinMessage);
    }
    
    /**
     * Extracts user ID from WebSocket session or falls back to provided ID.
     */
    private String extractUserFromSession(SimpMessageHeaderAccessor headerAccessor, String fallbackId) {
        return headerAccessor.getUser() != null ? 
                headerAccessor.getUser().getName() : fallbackId;
    }
    
    /**
     * Builds a standardized call signal message with timestamp and type.
     */
    private CallSignalMessage buildCallMessage(CallSignalMessage original, String callerId, String type) {
        CallSignalMessage message = CallSignalMessage.builder()
                .callId(original.getCallId())
                .callerId(callerId != null ? callerId : original.getCallerId())
                .recipientId(original.getRecipientId())
                .translatorId(original.getTranslatorId())
                .type(type)
                .channelName(original.getChannelName())
                .themeId(original.getThemeId())
                .data(original.getData())
                .status(original.getStatus())
                .timestamp(LocalDateTime.now())
                .build();
        
        return message;
    }
    
    /**
     * Sends a message to a specific user's queue.
     */
    private void sendToUser(String userId, String destination, CallSignalMessage message) {
        messagingTemplate.convertAndSendToUser(userId, destination, message);
    }
    
    /**
     * Broadcasts a message to all participants in a call room.
     */
    private void broadcastToCallRoom(String callId, CallSignalMessage message) {
        messagingTemplate.convertAndSend("/topic/call-room/" + callId, message);
    }
}