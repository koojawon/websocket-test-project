package com.example.websocketTest.handler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private static final ConcurrentHashMap<String, WebSocketSession> clientMap = new ConcurrentHashMap<>();


    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        clientMap.put(session.getId(), session);
        broadCastWelcomeMessage(session.getId());
    }

    private void broadCastWelcomeMessage(String id) {
        TextMessage textMessage = new TextMessage(id + "님이 입장하셨습니다.");
        broadCastMessage(id, textMessage);
    }

    private void broadCastExitMessge(String id) {
        TextMessage textMessage = new TextMessage(id + "님이 퇴장하셨습니다.");
        broadCastMessage(id, textMessage);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        broadCastMessage(session.getId(), message);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        clientMap.remove(session.getId());
        broadCastExitMessge(session.getId());
    }

    @Override
    public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message)
            throws Exception {
        super.handleMessage(session, message);
    }

    @Override
    protected void handleBinaryMessage(@NonNull WebSocketSession session, @NonNull BinaryMessage message) {
        super.handleBinaryMessage(session, message);
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) {
        log.error("[TransPort Error Occurred] : {}", exception.getMessage());
    }

    private void broadCastMessage(String senderId, TextMessage textMessage) {
        clientMap.values().forEach(webSocketSession -> {
            if (!webSocketSession.getId().equals(senderId)) {
                try {
                    webSocketSession.sendMessage(textMessage);
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        });
    }
}
