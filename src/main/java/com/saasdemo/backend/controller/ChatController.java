package com.saasdemo.backend.controller;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.saasdemo.backend.dto.ChatMessage;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.security.TenantContext;
import com.saasdemo.backend.service.ChatService;
import com.saasdemo.backend.util.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for chat management in the ETAT CIVIL application.
 * Allows sending messages, retrieving history,
 * managing user connection/disconnection, and checking user status.
 */
@RestController
@RequestMapping("/chat")
@Slf4j
@AllArgsConstructor
@Tag(
  name = "TCHAT_CONTROLLER   REST Api for ETAT CIVIL",
  description="TCHAT_CONTROLLER  REST Api in  ETAT CIVIL APP TO TCHAT "
)
public class ChatController {

    // Injected dependencies needed for the controller logic
    private final ChatService chatService;
    private final JwtUtil jwtUtil;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Send a message to the chat for the current tenant.
     * @param message The content of the message to send
     * @return HTTP response indicating success
     */
    @Operation(
        summary="REST API to send message in Chat inside ETAT CIVIL App",
        description = "REST API to send message in Chat inside ETAT CIVIL App "
    )
    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestParam String message) {
        Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String tenantId = usex.getId().toString();

        ChatMessage content = new ChatMessage();
        content.setSender(usex.getUsername());
        content.setValue(message);
        content.setTimestamp(Instant.now().toString());

        log.info("📩 Sending message from user [{}]: {}", usex.getUsername(), content.getValue());

        chatService.addMessage(tenantId, content);
        messagingTemplate.convertAndSend("/topic/" + tenantId, content);

        log.info("✅ Message sent successfully for tenant [{}]", tenantId);
        return ResponseEntity.ok("Message sent");
    }

    /**
     * Retrieve the complete message history for the current tenant.
     * @return List of all chat messages
     */
    @Operation(
        summary="REST API to get all chat messages in ETAT CIVIL App",
        description = "REST API to get all chat messages in ETAT CIVIL App "
    )
    @GetMapping("/messages")
    public List<ChatMessage> getMessages() {
        Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String tenantId = usex.getCommune().getId().toString();
        log.info("🔍 Fetching all messages for tenant [{}]", tenantId);
        List<ChatMessage> messages = chatService.getMessages(tenantId);
        log.info("✅ {} messages retrieved for tenant [{}]", messages.size(), tenantId);
        return messages;
    }

    /**
     * Retrieve messages from a specific time for the current tenant.
     * @param since Start date/time for message retrieval
     * @return List of messages from the given time
     */
    @Operation(
        summary="REST API to get chat messages since a specific time in ETAT CIVIL App",
        description = "REST API to get chat messages since a specific time in ETAT CIVIL App "
    )
    @GetMapping("/messages/since")
    public List<ChatMessage> getMessagesSince(@RequestParam("since") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant since) {
        Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String tenantId = usex.getCommune().getId().toString();
        log.info("🔍 Fetching messages for tenant [{}] since {}", tenantId, since);
        List<ChatMessage> messages = chatService.getMessagesSince(tenantId, since);
        log.info("✅ {} messages retrieved since {} for tenant [{}]", messages.size(), since, tenantId);
        return messages;
    }

    /**
     * List connected users in the chat for the current tenant.
     * @return Set of connected usernames
     */
    @Operation(
        summary="REST API to get all users in Chat in ETAT CIVIL App",
        description = "REST API to get all users in Chat in ETAT CIVIL App "
    )
    @GetMapping("/users")
    public ResponseEntity<Set<String>> listConnectedUsers() {
        Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TenantContext.setCurrentTenantId(usex.getId());
        String tenantId =  TenantContext.getCurrentTenantId().toString();
        log.info("🔍 Listing connected users for tenant [{}]", tenantId);
        Set<String> users = chatService.getConnectedUsers(tenantId);
        log.info("✅ {} users currently connected in tenant [{}]", users.size(), tenantId);
        return ResponseEntity.ok(users);
    }

    /**
     * Connect the current user to the chat.
     * @return true if connection succeeded, false otherwise
     */
    @Operation(
        summary="REST API to connect user in Chat in ETAT CIVIL App",
        description = "REST API to connect user in Chat in ETAT CIVIL App "
    )
    @PostMapping("/connect")
    public ResponseEntity<Boolean> connect() {
        Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TenantContext.setCurrentTenantId(usex.getId());
        String tenantId = TenantContext.getCurrentTenantId().toString();
       
        String user = usex.getUsername();
        log.info("🔌 Connecting user [{}] to tenant [{}]", user, tenantId);
        Boolean connected = chatService.connectUser(tenantId, user);
        log.info("✅ User [{}] connection status: {}", user, connected);
        return ResponseEntity.ok().body(connected);
    }

    /**
     * Disconnect the current user from the chat.
     * @return Empty HTTP response indicating success
     */
    @Operation(
        summary="REST API to logout user from Chat in ETAT CIVIL App",
        description = "REST API to logout user from Chat in ETAT CIVIL App "
    )
    @PostMapping("/disconnect")
    public ResponseEntity<Void> disconnect() {
        Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String tenantId = usex.getCommune().getId().toString();
        log.info("🔌 Disconnecting user [{}] from tenant [{}]", usex.getUsername(), tenantId);
        chatService.disconnectUser(tenantId, usex.getUsername());
        log.info("✅ User [{}] disconnected successfully", usex.getUsername());
        return ResponseEntity.ok().build();
    }

    /**
     * Check if the current user is connected to the chat.
     * @return true if user is connected, false otherwise
     */
    @Operation(
        summary="REST API to check if user is connected in Chat in ETAT CIVIL App",
        description = "REST API to check if user is connected in Chat in ETAT CIVIL App "
    )
    @GetMapping("/status")
    public boolean isConnected() {
        Utilisateur user = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String tenantId = user.getCommune().getId().toString();
        boolean status = chatService.isUserConnected(tenantId, user.getUsername());
        log.info("🔍 Connection status for user [{}] in tenant [{}]: {}", user.getUsername(), tenantId, status);
        return status;
    }
}
