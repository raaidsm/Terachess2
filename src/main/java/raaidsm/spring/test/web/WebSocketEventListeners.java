package raaidsm.spring.test.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import raaidsm.spring.test.web_models.ChatMessage;
import raaidsm.spring.test.web_models.MessageType;

import java.util.Objects;

@Component
public class WebSocketEventListeners {
    private final Logger logger = LoggerFactory.getLogger(WebSocketEventListeners.class);

    @Autowired
    private SimpMessageSendingOperations sendingOperations;

    @EventListener
    public void handleWebSocketConnectedListener(final SessionConnectedEvent event) {
        logger.info("User connected!");
    }

    @EventListener
    public void handleWebSocketDisconnectedListener(final SessionDisconnectEvent event) {
        final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        final String username = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username");
        final ChatMessage chatMessage = ChatMessage.builder()
                .type(MessageType.DISCONNECT)
                .sender(username)
                .build();

        sendingOperations.convertAndSend("/topic/public", chatMessage);
    }
}