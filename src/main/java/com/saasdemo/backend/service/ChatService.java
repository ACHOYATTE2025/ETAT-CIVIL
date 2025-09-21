package com.saasdemo.backend.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.saasdemo.backend.dto.ChatMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    // Stores the set of connected users for each tenant
    private final Map<String, Set<String>> connectedUsers = new ConcurrentHashMap<>();
    // Stores the chat messages for each tenant
    private final Map<String, List<ChatMessage>> chatMessages = new ConcurrentHashMap<>();

    // Connect a user to a tenant's chat and return true if successful
    public boolean connectUser(String tenantId, String username) {
        boolean result = connectedUsers.computeIfAbsent(tenantId, k -> ConcurrentHashMap.newKeySet()).add(username);
        logger.info("User '{}' connected to tenant '{}'. Result: {}", username, tenantId, result);
        return result;
    }

    // Disconnect a user from a tenant's chat
    public void disconnectUser(String tenantId, String username) {
        Set<String> users = connectedUsers.get(tenantId);
        if (users != null) {
            users.remove(username);
            logger.info("User '{}' disconnected from tenant '{}'.", username, tenantId);
            // Remove tenant entry if no users are left
            if (users.isEmpty()) {
                connectedUsers.remove(tenantId);
                logger.info("No users left for tenant '{}', removed tenant from connected users.", tenantId);
            }
        } else {
            logger.warn("Attempted to disconnect user '{}' from tenant '{}', but no users found.", username, tenantId);
        }
    }

    // Get all connected users for a specific tenant
    public Set<String> getConnectedUsers(String tenantId) {
        Set<String> users = connectedUsers.getOrDefault(tenantId, Set.of());
        logger.debug("Fetched connected users for tenant '{}': {}", tenantId, users);
        return users;
    }

    // Check if a user is connected to a tenant's chat
    public boolean isUserConnected(String tenantId, String username) {
        boolean isConnected = connectedUsers.getOrDefault(tenantId, Set.of()).contains(username);
        logger.debug("Checked if user '{}' is connected to tenant '{}': {}", username, tenantId, isConnected);
        return isConnected;
    }

    // Add a new chat message for a tenant
    public void addMessage(String tenantId, ChatMessage message) {
        chatMessages.computeIfAbsent(tenantId, k -> new ArrayList<>()).add(message);
        logger.info("Added message from '{}' to tenant '{}'.", message.getSender(), tenantId);
    }

    // Get all chat messages for a tenant
    public List<ChatMessage> getMessages(String tenantId) {
        List<ChatMessage> messages = chatMessages.get(tenantId);
        logger.debug("Fetched all messages for tenant '{}'. Count: {}", tenantId, messages != null ? messages.size() : 0);
        return messages != null ? messages : new ArrayList<>();
    }

    // Get chat messages for a tenant since a specific timestamp
    public List<ChatMessage> getMessagesSince(String tenantId, Instant since) {
        List<ChatMessage> messages = chatMessages.getOrDefault(tenantId, List.of()).stream()
                .filter(msg -> Instant.parse(msg.getTimestamp()).isAfter(since))
                .limit(100)
                .collect(Collectors.toList());
        logger.debug("Fetched messages for tenant '{}' since '{}'. Count: {}", tenantId, since, messages.size());
        return messages;
    }
}
