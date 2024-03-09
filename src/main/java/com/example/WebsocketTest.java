package com.example;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/chat/{username}")
@ApplicationScoped
public class WebsocketTest {
    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        sessions.put(username, session);
        broadcastMessage(username + "님이 채팅에 접속하였습니다.");
    }

    @OnMessage
    public void onMessage(String message, @PathParam("username") String username) {
        broadcastMessage(username + ": " + message);
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        sessions.remove(username, session);
        broadcastMessage(username + "님이 채팅에서 나갔습니다.");
    }

    @OnError
    public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
        sessions.remove(username, session);
    }

    private void broadcastMessage(String message) {
        sessions.values().forEach(session -> session.getAsyncRemote().sendText(message));
    }

}
