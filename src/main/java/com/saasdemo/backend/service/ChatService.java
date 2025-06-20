package com.saasdemo.backend.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.saasdemo.backend.dto.ChatMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final Map<String, Set<String>> connectedUsers = new ConcurrentHashMap<>();
    private final Map<String, List<ChatMessage>> chatMessages = new ConcurrentHashMap<>();

    public boolean connectUser(String tenantId, String username) {
        return connectedUsers.computeIfAbsent(tenantId, k -> ConcurrentHashMap.newKeySet()).add(username);
    }

    public void disconnectUser(String tenantId, String username) {
        Set<String> users = connectedUsers.get(tenantId);
        if (users != null) {
            users.remove(username);
            if (users.isEmpty()) {
                connectedUsers.remove(tenantId);
            }
        }
    }

    public Set<String> getConnectedUsers(String tenantId) {
        return connectedUsers.getOrDefault(tenantId, Set.of());
    }

    public boolean isUserConnected(String tenantId, String username) {
        return connectedUsers.getOrDefault(tenantId, Set.of()).contains(username);
    }

    public void addMessage(String tenantId, ChatMessage message) {
        chatMessages.computeIfAbsent(tenantId, k -> new ArrayList<>()).add(message);
    }

    public List<ChatMessage> getMessages(String tenantId) {
        List<ChatMessage> messages = chatMessages.get(tenantId);
    return messages != null ? messages : new ArrayList<>();
    }

    public List<ChatMessage> getMessagesSince(String tenantId, Instant since) {
        return chatMessages.getOrDefault(tenantId, List.of()).stream()
                .filter(msg -> Instant.parse(msg.getTimestamp()).isAfter(since))
                .limit(100)
                .collect(Collectors.toList());
    }


}
