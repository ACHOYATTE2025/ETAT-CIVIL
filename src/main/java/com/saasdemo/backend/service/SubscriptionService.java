package com.saasdemo.backend.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.saasdemo.backend.dto.ResponseDto;
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

  
  public  ResponseEntity<ResponseDto> createSubscriptionForUser( SubscriptionDTO dto) {

   
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Utilisateur admin)) {
            throw new RuntimeException("Utilisateur non authentifié correctement.");
        }

        Utilisateur user = utilisateurRepository.findById(admin.getId())
            .orElseThrow(() -> new RuntimeException(admin.getUsername()+" introuvable"));

      if (user.getRole() == null || user.getRole().getLibele() != TypeRole.ADMIN) {
    throw new RuntimeException("Seul un ADMIN peut créer une souscription.");
}

    boolean exists = subscriptionRepository.existsByCommuneAndActiveTrueAndEndDateAfter(admin.getCommune(), LocalDateTime.now());

    Subscription susex = subscriptionRepository.findByUsersNameAndActiveTrueAndEndDateAfterAndStatus(admin.getUsername(),
    LocalDateTime.now(),StatutAbonnement.TRIAL).orElseThrow(()-> new RuntimeException(""));


    if (exists && susex.getStatus() != StatutAbonnement.TRIAL) {
    throw new RuntimeException("Une souscription active existe déjà pour cette commune.");}


     //desactiver toutes les soubscriptions expirées
    LocalDateTime now = LocalDateTime.now();
    List<Subscription> expired = subscriptionRepository.findByUsersNameAndActiveTrueAndEndDateBefore(admin.getUsername(),now);

    expired.forEach(s -> {
        s.setActive(false);
        subscriptionRepository.save(s);
    });
    
    
        
    
        
        Subscription subscription = Subscription.builder()
                                .status(dto.getStatus())
                                .active(true)
                                .amount(dto.getAmount())
                                .created(LocalDateTime.now(ZoneId.of("UTC")))
                                .commune(admin.getCommune())
                                .endDate(LocalDateTime.now(ZoneId.of("UTC")).plusMonths(1))
                                .usersName(admin.getUsername())
                                .email(admin.getEmail())
                                .build();
        subscriptionRepository.save(subscription);

        susex.setActive(false);
        subscriptionRepository.save(susex);
    

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new ResponseDto(200, "Souscription éffectuée"));
        
    }
}