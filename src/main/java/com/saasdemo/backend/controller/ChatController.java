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

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/chat")
@Slf4j
@AllArgsConstructor
public class ChatController {
      private final ChatService chatService;
      private final JwtUtil jwtUtil;
      private final SimpMessagingTemplate messagingTemplate;
   
      


    
      
    //envoie de message
    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(  @RequestParam String message) {
      Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      String tenantId = usex.getId().toString();
      ChatMessage content = new ChatMessage();
      content.setSender(usex.getUsername());
      content.setValue(message);
      log.info("Valeur de content.getvalue ="+content.getValue());
      content.setTimestamp(Instant.now().toString());

      chatService.addMessage(tenantId, content);

      // Broadcast (simulation ou websocket si activé)
      messagingTemplate.convertAndSend("/topic/" + tenantId, content);

      log.info("Message envoyé [{}] par {}", content.getValue(), usex.getUsername());
    return ResponseEntity.ok("Message envoyé");
}



     // Récupère l'historique des messages pour un tenant donné
    @GetMapping("/messages")
    public List<ChatMessage> getMessages() {
      Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      String tenantId = usex.getCommune().getId().toString();
      List<ChatMessage> messages = chatService.getMessages(tenantId);
    return messages;}


   //Récupère l'historique des messages pour un tenant donné dans un temps precis  
    @GetMapping("/messages/since")
    public List<ChatMessage> getMessagesSince(@RequestParam("since") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant since) {
      Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      String tenantId = usex.getCommune().getId().toString();  
      return chatService.getMessagesSince(tenantId, since);
    }



    //liste des users
    @GetMapping("/users")
    public ResponseEntity<Set<String>> listConnectedUsers() {
      Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      TenantContext.setCurrentTenantId(usex.getId());
     String tenantId =  TenantContext.getCurrentTenantId().toString();
        return ResponseEntity.ok(chatService.getConnectedUsers(tenantId));
    }


    //se connecter
    @PostMapping("/connect")
    public ResponseEntity<Boolean> connect() {
      Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      TenantContext.setCurrentTenantId(usex.getId());
      String tenantId = TenantContext.getCurrentTenantId().toString();
       
        String user = usex.getUsername();
        Boolean x = chatService.connectUser(tenantId, user);
        return ResponseEntity.ok().body(x);
    }


    //se deconnecter
    @PostMapping("/disconnect")
    public ResponseEntity<Void> disconnect() {
      Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      String tenantId = usex.getCommune().getId().toString();

        chatService.disconnectUser(tenantId, usex.getUsername());
        return ResponseEntity.ok().build();
    }

  // voir si un user est connecté
    @GetMapping("/status")
    public boolean isConnected() {
      Utilisateur user = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      String tenantId = user.getCommune().getId().toString();
    return chatService.isUserConnected(tenantId, user.getUsername());
}


  
    
}