package com.saasdemo.backend.service;

import java.time.Instant;
import java.time.LocalDateTime;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.saasdemo.backend.dto.SubscriptionDTO;
import com.saasdemo.backend.entity.Subscription;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.enums.StatutAbonnement;
import com.saasdemo.backend.repository.SubscriptionRepository;
import com.saasdemo.backend.repository.UtilisateurRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class SubscriptionService {

  final UtilisateurRepository utilisateurRepository;
  private final SubscriptionRepository subscriptionRepository;

  
  public void createSubscriptionForUser( SubscriptionDTO dto) {

         Utilisateur admin = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Utilisateur user = utilisateurRepository.findById(admin.getId())
            .orElseThrow(() -> new RuntimeException(admin.getUsername()+" introuvable"));

        if (!"ADMIN".equals(user.getRole().toString())) {
            throw new RuntimeException("Seul un ADMIN peut avoir une souscription.");
        }
        
        Subscription subscription = Subscription.builder()
                                .status(StatutAbonnement.Active)
                                .active(true)
                                .amount(dto.getAmount())
                                .createdAt(Instant.now())
                                .commune(admin.getCommune())
                                .endDate(LocalDateTime.now().plusMonths(1))
                                .build();
        subscriptionRepository.save(subscription);
    
        
    }
}