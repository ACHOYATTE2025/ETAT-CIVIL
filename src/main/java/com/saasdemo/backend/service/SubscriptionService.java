package com.saasdemo.backend.service;

import java.time.LocalDateTime;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.saasdemo.backend.dto.SubscriptionDTO;
import com.saasdemo.backend.entity.Subscription;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.enums.StatutAbonnement;
import com.saasdemo.backend.enums.TypeRole;
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

       Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
if (!(principal instanceof Utilisateur admin)) {
    throw new RuntimeException("Utilisateur non authentifié correctement.");
}

        Utilisateur user = utilisateurRepository.findById(admin.getId())
            .orElseThrow(() -> new RuntimeException(admin.getUsername()+" introuvable"));

      if (user.getRole() == null || user.getRole().getLibele() != TypeRole.ADMIN) {
    throw new RuntimeException("Seul un ADMIN peut créer une souscription.");
}

    boolean exists = subscriptionRepository.existsByCommuneAndActiveTrue(admin.getCommune());
    if (exists) {
    throw new RuntimeException("Une souscription active existe déjà pour cette commune.");}

        
        Subscription subscription = Subscription.builder()
                                .status(StatutAbonnement.Active)
                                .active(true)
                                .amount(dto.getAmount())
                                .created(LocalDateTime.now())
                                .commune(admin.getCommune())
                                .endDate(LocalDateTime.now().plusMonths(1))
                                .build();
        subscriptionRepository.save(subscription);
    
        
    }
}